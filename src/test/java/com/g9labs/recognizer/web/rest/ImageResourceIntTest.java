package com.g9labs.recognizer.web.rest;

import com.g9labs.recognizer.RecognizerApp;

import com.g9labs.recognizer.domain.Image;
import com.g9labs.recognizer.repository.ImageRepository;
import com.g9labs.recognizer.service.dto.ImageDTO;
import com.g9labs.recognizer.service.mapper.ImageMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.util.List;
import java.io.File;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ImageResource REST controller.
 *
 * @see ImageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RecognizerApp.class)
public class ImageResourceIntTest {

    private static final String DEFAULT_PATH = "/tmp/path/to/something";
    private static final String UPDATED_PATH = "/tmp/path/to/something";

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImageMockMvc;

    private Image image;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageResource imageResource = new ImageResource();
        ReflectionTestUtils.setField(imageResource, "imageRepository", imageRepository);
        ReflectionTestUtils.setField(imageResource, "imageMapper", imageMapper);
        this.restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image()
                .path(DEFAULT_PATH);
        return image;
    }

    @Before
    public void initTest() {
        image = createEntity(em);
    }

    @Test
    @Transactional
    public void shouldCreateImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);
        File imageFixture = new File(System.getProperty("user.dir") + "/src/test/java/com/g9labs/recognizer/fixtures/hello.png");
        FileInputStream imageInputStream = new FileInputStream(imageFixture);
        String expectedOCROutput = "hello";

        MockMultipartFile multipartFile =
            new MockMultipartFile("file", "test.png", "image/png", imageInputStream);

        restImageMockMvc.perform(fileUpload("/api/images").file(multipartFile).param("path", "asdf"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
                //.andExpect(jsonPath("$.message").value(containsString("hello")));

        // Validate the Image in the database
        List<Image> images = imageRepository.findAll();
        assertThat(images).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = images.get(images.size() - 1);
        assertThat(testImage.getPath()).isEqualTo(expectedOCROutput);
    }

    @Test
    @Transactional
    public void checkPathIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setPath(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
                .andExpect(status().isBadRequest());

        List<Image> images = imageRepository.findAll();
        assertThat(images).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImages() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the images
        restImageMockMvc.perform(get("/api/images?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
                .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())));
    }

    @Test
    @Transactional
    public void getImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get the image
        ResultActions resultActions = restImageMockMvc.perform(get("/api/images/{id}", image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(image.getId().intValue()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImage() throws Exception {
        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image
        Image updatedImage = imageRepository.findOne(image.getId());
        updatedImage
                .path(UPDATED_PATH);
        ImageDTO imageDTO = imageMapper.imageToImageDTO(updatedImage);

        restImageMockMvc.perform(put("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
                .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> images = imageRepository.findAll();
        assertThat(images).hasSize(databaseSizeBeforeUpdate);
        Image testImage = images.get(images.size() - 1);
        assertThat(testImage.getPath()).isEqualTo(UPDATED_PATH);
    }

    @Test
    @Transactional
    public void deleteImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);
        int databaseSizeBeforeDelete = imageRepository.findAll().size();

        // Get the image
        restImageMockMvc.perform(delete("/api/images/{id}", image.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Image> images = imageRepository.findAll();
        assertThat(images).hasSize(databaseSizeBeforeDelete - 1);
    }
}
