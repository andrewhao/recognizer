package com.g9labs.recognizer.web.rest;

import com.g9labs.recognizer.RecognizerApp;

import com.g9labs.recognizer.domain.Image;
import com.g9labs.recognizer.repository.ImageRepository;
import com.g9labs.recognizer.service.dto.ImageDTO;
import com.g9labs.recognizer.service.mapper.ImageMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import java.io.IOException;
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
        String expectedOCROutput = "hello";
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        // PNG that reads: "hello"
        String base64File = "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";

        imageDTO.setBase64File(base64File);
        imageDTO.setFileContentType("image/png");

        restImageMockMvc
            .perform(post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
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

    private MockMultipartFile makeMockMultipartFile() throws IOException {
        File imageFixture = new File(System.getProperty("user.dir") + "/src/test/java/com/g9labs/recognizer/fixtures/hello.png");
        FileInputStream imageInputStream = new FileInputStream(imageFixture);
        String expectedOCROutput = "hello";

        MockMultipartFile multipartFile =
            new MockMultipartFile("file", "test.png", "image/png", imageInputStream);

        return multipartFile;
    }

}
