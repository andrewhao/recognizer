package com.g9labs.recognizer.service.mapper;

import com.g9labs.recognizer.domain.*;
import com.g9labs.recognizer.service.dto.ProcessedImageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ProcessedImage and its DTO ProcessedImageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProcessedImageMapper {

    ProcessedImageDTO processedImageToProcessedImageDTO(ProcessedImage processedImage);

    List<ProcessedImageDTO> processedImagesToProcessedImageDTOs(List<ProcessedImage> processedImages);

    ProcessedImage processedImageDTOToProcessedImage(ProcessedImageDTO processedImageDTO);

    List<ProcessedImage> processedImageDTOsToProcessedImages(List<ProcessedImageDTO> processedImageDTOs);
}
