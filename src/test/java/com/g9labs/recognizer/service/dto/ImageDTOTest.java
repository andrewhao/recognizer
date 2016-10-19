package com.g9labs.recognizer.service.dto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.*;

/**
 * Created by andrewhao on 10/12/16.
 */
public class ImageDTOTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void expectFileContentType() throws Exception {
        ImageDTO subject = new ImageDTO();
        String fileContentType = "image/png";
        subject.setFileContentType(fileContentType);
        assertEquals(fileContentType, subject.getFileContentType());
    }
}
