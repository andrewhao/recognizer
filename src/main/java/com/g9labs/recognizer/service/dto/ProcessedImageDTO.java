package com.g9labs.recognizer.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the ProcessedImage entity.
 */
public class ProcessedImageDTO implements Serializable {

    private Long id;

    @NotNull
    private String path;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProcessedImageDTO processedImageDTO = (ProcessedImageDTO) o;

        if ( ! Objects.equals(id, processedImageDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProcessedImageDTO{" +
            "id=" + id +
            ", path='" + path + "'" +
            '}';
    }
}
