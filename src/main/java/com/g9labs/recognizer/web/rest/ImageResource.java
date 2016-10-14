package com.g9labs.recognizer.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.g9labs.recognizer.domain.Image;

import com.g9labs.recognizer.repository.ImageRepository;
import com.g9labs.recognizer.service.ImageOCRConversionService;
import com.g9labs.recognizer.web.rest.util.HeaderUtil;
import com.g9labs.recognizer.service.dto.ImageDTO;
import com.g9labs.recognizer.service.mapper.ImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Image.
 */
@RestController
@RequestMapping("/api")
public class ImageResource {

    private final Logger log = LoggerFactory.getLogger(ImageResource.class);

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ImageMapper imageMapper;

    /**
     * POST  /images : Create a new image.
     *
     * @param imageDTO the imageDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imageDTO, or with status 400 (Bad Request) if the image has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageDTO> createImage(@Valid ImageDTO imageDTO) throws URISyntaxException {
        if (imageDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "idexists", "A new image cannot already have an ID")).body(null);
        }
        ImageDTO processedImage = new ImageOCRConversionService(imageDTO).invoke();

        Image image = imageMapper.imageDTOToImage(processedImage);
        image = imageRepository.save(image);
        ImageDTO result = imageMapper.imageToImageDTO(image);
        return ResponseEntity.created(new URI("/api/images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("image", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /images : Updates an existing image.
     *
     * @param imageDTO the imageDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imageDTO,
     * or with status 400 (Bad Request) if the imageDTO is not valid,
     * or with status 500 (Internal Server Error) if the imageDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageDTO> updateImage(@Valid @RequestBody ImageDTO imageDTO) throws URISyntaxException {
        log.debug("REST request to update Image : {}", imageDTO);
        if (imageDTO.getId() == null) {
            return createImage(imageDTO);
        }
        Image image = imageMapper.imageDTOToImage(imageDTO);
        image = imageRepository.save(image);
        ImageDTO result = imageMapper.imageToImageDTO(image);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("image", imageDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /images : get all the images.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of images in body
     */
    @RequestMapping(value = "/images",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ImageDTO> getAllImages() {
        log.debug("REST request to get all Images");
        List<Image> images = imageRepository.findAll();
        return imageMapper.imagesToImageDTOs(images);
    }

    /**
     * GET  /images/:id : get the "id" image.
     *
     * @param id the id of the imageDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imageDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageDTO> getImage(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        Image image = imageRepository.findOne(id);
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);
        return Optional.ofNullable(imageDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /images/:id : delete the "id" image.
     *
     * @param id the id of the imageDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/images/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("image", id.toString())).build();
    }

}
