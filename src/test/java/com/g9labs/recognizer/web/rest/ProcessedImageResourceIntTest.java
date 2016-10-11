package com.g9labs.recognizer.web.rest;

import com.g9labs.recognizer.RecognizerApp;

import com.g9labs.recognizer.domain.ProcessedImage;
import com.g9labs.recognizer.repository.ProcessedImageRepository;
import com.g9labs.recognizer.service.dto.ProcessedImageDTO;
import com.g9labs.recognizer.service.mapper.ProcessedImageMapper;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProcessedImageResource REST controller.
 *
 * @see ProcessedImageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RecognizerApp.class)
public class ProcessedImageResourceIntTest {

    private static final String DEFAULT_PATH = "AAAAA";
    private static final String UPDATED_PATH = "BBBBB";

    @Inject
    private ProcessedImageRepository processedImageRepository;

    @Inject
    private ProcessedImageMapper processedImageMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProcessedImageMockMvc;

    private ProcessedImage processedImage;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProcessedImageResource processedImageResource = new ProcessedImageResource();
        ReflectionTestUtils.setField(processedImageResource, "processedImageRepository", processedImageRepository);
        ReflectionTestUtils.setField(processedImageResource, "processedImageMapper", processedImageMapper);
        this.restProcessedImageMockMvc = MockMvcBuilders.standaloneSetup(processedImageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProcessedImage createEntity(EntityManager em) {
        ProcessedImage processedImage = new ProcessedImage()
                .path(DEFAULT_PATH);
        return processedImage;
    }

    @Before
    public void initTest() {
        processedImage = createEntity(em);
    }

    @Test
    @Transactional
    public void createProcessedImage() throws Exception {
        int databaseSizeBeforeCreate = processedImageRepository.findAll().size();

        // Create the ProcessedImage
        ProcessedImageDTO processedImageDTO = processedImageMapper.processedImageToProcessedImageDTO(processedImage);

        restProcessedImageMockMvc.perform(post("/api/processed-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(processedImageDTO)))
                .andExpect(status().isCreated());

        // Validate the ProcessedImage in the database
        List<ProcessedImage> processedImages = processedImageRepository.findAll();
        assertThat(processedImages).hasSize(databaseSizeBeforeCreate + 1);
        ProcessedImage testProcessedImage = processedImages.get(processedImages.size() - 1);
        assertThat(testProcessedImage.getPath()).isEqualTo(DEFAULT_PATH);
    }

    @Test
    @Transactional
    public void checkPathIsRequired() throws Exception {
        int databaseSizeBeforeTest = processedImageRepository.findAll().size();
        // set the field null
        processedImage.setPath(null);

        // Create the ProcessedImage, which fails.
        ProcessedImageDTO processedImageDTO = processedImageMapper.processedImageToProcessedImageDTO(processedImage);

        restProcessedImageMockMvc.perform(post("/api/processed-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(processedImageDTO)))
                .andExpect(status().isBadRequest());

        List<ProcessedImage> processedImages = processedImageRepository.findAll();
        assertThat(processedImages).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProcessedImages() throws Exception {
        // Initialize the database
        processedImageRepository.saveAndFlush(processedImage);

        // Get all the processedImages
        restProcessedImageMockMvc.perform(get("/api/processed-images?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(processedImage.getId().intValue())))
                .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())));
    }

    @Test
    @Transactional
    public void getProcessedImage() throws Exception {
        // Initialize the database
        processedImageRepository.saveAndFlush(processedImage);

        // Get the processedImage
        restProcessedImageMockMvc.perform(get("/api/processed-images/{id}", processedImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(processedImage.getId().intValue()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProcessedImage() throws Exception {
        // Get the processedImage
        restProcessedImageMockMvc.perform(get("/api/processed-images/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProcessedImage() throws Exception {
        // Initialize the database
        processedImageRepository.saveAndFlush(processedImage);
        int databaseSizeBeforeUpdate = processedImageRepository.findAll().size();

        // Update the processedImage
        ProcessedImage updatedProcessedImage = processedImageRepository.findOne(processedImage.getId());
        updatedProcessedImage
                .path(UPDATED_PATH);
        ProcessedImageDTO processedImageDTO = processedImageMapper.processedImageToProcessedImageDTO(updatedProcessedImage);

        restProcessedImageMockMvc.perform(put("/api/processed-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(processedImageDTO)))
                .andExpect(status().isOk());

        // Validate the ProcessedImage in the database
        List<ProcessedImage> processedImages = processedImageRepository.findAll();
        assertThat(processedImages).hasSize(databaseSizeBeforeUpdate);
        ProcessedImage testProcessedImage = processedImages.get(processedImages.size() - 1);
        assertThat(testProcessedImage.getPath()).isEqualTo(UPDATED_PATH);
    }

    @Test
    @Transactional
    public void deleteProcessedImage() throws Exception {
        // Initialize the database
        processedImageRepository.saveAndFlush(processedImage);
        int databaseSizeBeforeDelete = processedImageRepository.findAll().size();

        // Get the processedImage
        restProcessedImageMockMvc.perform(delete("/api/processed-images/{id}", processedImage.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ProcessedImage> processedImages = processedImageRepository.findAll();
        assertThat(processedImages).hasSize(databaseSizeBeforeDelete - 1);
    }
}
