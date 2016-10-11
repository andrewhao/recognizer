package com.g9labs.recognizer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.g9labs.recognizer.domain.ProcessedImage;

import com.g9labs.recognizer.repository.ProcessedImageRepository;
import com.g9labs.recognizer.web.rest.util.HeaderUtil;
import com.g9labs.recognizer.service.dto.ProcessedImageDTO;
import com.g9labs.recognizer.service.mapper.ProcessedImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing ProcessedImage.
 */
@RestController
@RequestMapping("/api")
public class ProcessedImageResource {

    private final Logger log = LoggerFactory.getLogger(ProcessedImageResource.class);
        
    @Inject
    private ProcessedImageRepository processedImageRepository;

    @Inject
    private ProcessedImageMapper processedImageMapper;

    /**
     * POST  /processed-images : Create a new processedImage.
     *
     * @param processedImageDTO the processedImageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new processedImageDTO, or with status 400 (Bad Request) if the processedImage has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/processed-images",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProcessedImageDTO> createProcessedImage(@Valid @RequestBody ProcessedImageDTO processedImageDTO) throws URISyntaxException {
        log.debug("REST request to save ProcessedImage : {}", processedImageDTO);
        if (processedImageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("processedImage", "idexists", "A new processedImage cannot already have an ID")).body(null);
        }
        ProcessedImage processedImage = processedImageMapper.processedImageDTOToProcessedImage(processedImageDTO);
        processedImage = processedImageRepository.save(processedImage);
        ProcessedImageDTO result = processedImageMapper.processedImageToProcessedImageDTO(processedImage);
        return ResponseEntity.created(new URI("/api/processed-images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("processedImage", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /processed-images : Updates an existing processedImage.
     *
     * @param processedImageDTO the processedImageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated processedImageDTO,
     * or with status 400 (Bad Request) if the processedImageDTO is not valid,
     * or with status 500 (Internal Server Error) if the processedImageDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/processed-images",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProcessedImageDTO> updateProcessedImage(@Valid @RequestBody ProcessedImageDTO processedImageDTO) throws URISyntaxException {
        log.debug("REST request to update ProcessedImage : {}", processedImageDTO);
        if (processedImageDTO.getId() == null) {
            return createProcessedImage(processedImageDTO);
        }
        ProcessedImage processedImage = processedImageMapper.processedImageDTOToProcessedImage(processedImageDTO);
        processedImage = processedImageRepository.save(processedImage);
        ProcessedImageDTO result = processedImageMapper.processedImageToProcessedImageDTO(processedImage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("processedImage", processedImageDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /processed-images : get all the processedImages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of processedImages in body
     */
    @RequestMapping(value = "/processed-images",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProcessedImageDTO> getAllProcessedImages() {
        log.debug("REST request to get all ProcessedImages");
        List<ProcessedImage> processedImages = processedImageRepository.findAll();
        return processedImageMapper.processedImagesToProcessedImageDTOs(processedImages);
    }

    /**
     * GET  /processed-images/:id : get the "id" processedImage.
     *
     * @param id the id of the processedImageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the processedImageDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/processed-images/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProcessedImageDTO> getProcessedImage(@PathVariable Long id) {
        log.debug("REST request to get ProcessedImage : {}", id);
        ProcessedImage processedImage = processedImageRepository.findOne(id);
        ProcessedImageDTO processedImageDTO = processedImageMapper.processedImageToProcessedImageDTO(processedImage);
        return Optional.ofNullable(processedImageDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /processed-images/:id : delete the "id" processedImage.
     *
     * @param id the id of the processedImageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/processed-images/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProcessedImage(@PathVariable Long id) {
        log.debug("REST request to delete ProcessedImage : {}", id);
        processedImageRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("processedImage", id.toString())).build();
    }

}
