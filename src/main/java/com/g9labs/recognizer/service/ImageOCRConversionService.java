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
        String tessdataPrefix = System.getenv("TESSDATA_PREFIX");
        System.out.println("env vars:");
        System.out.println(System.getenv());
        System.out.println("TESSDATA_PREFIX:");
        System.out.println(tessdataPrefix);

        MultipartFile dtoFile = imageDTO.getFile();
        String tmpDirPath = System.getProperty("user.dir") + "/tmp/";
        File imageFile = new File(tmpDirPath + dtoFile.getOriginalFilename());
        boolean successfulMkdir = new File(tmpDirPath).mkdirs();

        if (!successfulMkdir) { System.out.println("oops, couldn't create dir"); }

        try {
            dtoFile.transferTo(imageFile);
        } catch (IOException e) {
            System.out.println("Oops, transfer failed.");
            System.err.println(e.getMessage());
        }

        ITesseract tess = new Tesseract();
        if (tessdataPrefix != null) {
            tess.setDatapath(System.getenv("TESSDATA_PREFIX") + "/tessdata");
        }
        System.out.println("imageFile");
        System.out.println(imageFile);

        try {
            String recognizedText = tess.doOCR(imageFile).trim();
            System.out.println(recognizedText);
            imageDTO.setPath(recognizedText);
        } catch (TesseractException e) {
            System.out.println("Oops, doOCR failed.");
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
