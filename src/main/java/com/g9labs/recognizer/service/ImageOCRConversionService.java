package com.g9labs.recognizer.service;

import com.g9labs.recognizer.service.dto.ImageDTO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by andrewhao on 10/14/16.
 */
public class ImageOCRConversionService {
    private ImageDTO imageDTO;

    public ImageOCRConversionService(ImageDTO imageDTO) {
        this.imageDTO = imageDTO;
    }

    public ImageDTO invoke() {
        // Workaround required for mac - OK, my laptop.
        if (isMacOS()) {
            System.setProperty("jna.library.path", "/opt/boxen/homebrew/Cellar/tesseract/3.04.01_2/lib");
        }
        System.out.println(System.getenv());
        System.out.println(System.getenv("TESSDATA_PREFIX"));
        MultipartFile dtoFile = imageDTO.getFile();
        String tmpDirPath = System.getProperty("user.dir") + "/tmp/";
        File imageFile = new File(tmpDirPath + dtoFile.getOriginalFilename());
        imageFile.mkdirs();
        try {
            dtoFile.transferTo(imageFile);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        ITesseract tess = new Tesseract();
        tess.setDatapath(System.getenv("TESSDATA_PREFIX") + "/tessdata");

        try {
            String recognizedText = tess.doOCR(imageFile).trim();
            System.out.println(recognizedText);
            imageDTO.setPath(recognizedText);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

        return imageDTO;
    }

    private boolean isMacOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");
        return isMacOs;
    }
}
