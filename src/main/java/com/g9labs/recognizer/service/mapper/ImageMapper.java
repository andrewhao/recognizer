package com.g9labs.recognizer.service.mapper;

import com.g9labs.recognizer.domain.*;
import com.g9labs.recognizer.service.dto.ImageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Image and its DTO ImageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageMapper {

    @Mapping(source = "processedImage.id", target = "processedImageId")
    ImageDTO imageToImageDTO(Image image);

    List<ImageDTO> imagesToImageDTOs(List<Image> images);

    @Mapping(source = "processedImageId", target = "processedImage")
    Image imageDTOToImage(ImageDTO imageDTO);

    List<Image> imageDTOsToImages(List<ImageDTO> imageDTOs);

    default ProcessedImage processedImageFromId(Long id) {
        if (id == null) {
            return null;
        }
        ProcessedImage processedImage = new ProcessedImage();
        processedImage.setId(id);
        return processedImage;
    }
}
