package com.g9labs.recognizer.service.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;


/**
 * A DTO for the Image entity.
 */
public class ImageDTO implements Serializable {
    private Long id;

    private String path;

    private String base64File;

    private Long processedImageId;

    private String fileContentType;

    private String ocrResult;

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;

    }

    public String getBase64File() {
        return base64File;
    }

    public void setBase64File(String base64File) {
        this.base64File = base64File;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getProcessedImageId() {
        return processedImageId;
    }

    public void setProcessedImageId(Long processedImageId) {
        this.processedImageId = processedImageId;
    }

    /* For request conversion from the params object */
    public void setFile(String base64File) {
        setBase64File(base64File);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageDTO imageDTO = (ImageDTO) o;

        if ( ! Objects.equals(id, imageDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
            "id=" + id +
            ", fileContentType='" + fileContentType + "'" +
            ", base64File='" + base64File + "'" +
            ", path='" + path + "'" +
            ", processedImageId='" + processedImageId + "'" +
            ", ocrResult='" + ocrResult + "'" +
            '}';
    }

    private String detectSuffix() {
        return this.getFileContentType().split("/")[1];
    }
}
