package com.g9labs.recognizer.repository;

import com.g9labs.recognizer.domain.ProcessedImage;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProcessedImage entity.
 */
@SuppressWarnings("unused")
public interface ProcessedImageRepository extends JpaRepository<ProcessedImage,Long> {

}
