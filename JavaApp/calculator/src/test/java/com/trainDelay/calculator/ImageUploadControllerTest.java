package com.trainDelay.calculator;

import com.example.demo.service.ImageTextReaderService;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ImageUploadControllerTest {

    @Mock
    private ImageTextReaderService imageTextReaderService;

    @InjectMocks
    private ImageUploadController imageUploadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleImageUpload_FileIsEmpty() {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty", response.getBody());
    }

    @Test
    public void testHandleImageUpload_InvalidImageFormat() throws IOException {
        byte[] invalidImageData = "invalid image data".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", invalidImageData);

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid image format", response.getBody());
    }

    @Test
    public void testHandleImageUpload_ValidImage() throws IOException, TesseractException {
        byte[] validImageData = new byte[]{/* valid image data */};
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(validImageData));

        when(imageTextReaderService.extractTextFromImage(file)).thenReturn(new String[]{"line1", "line2"});

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("line1\nline2", response.getBody());
    }

    @Test
    public void testHandleImageUpload_ErrorProcessingImage() throws IOException, TesseractException {
        byte[] validImageData = new byte[]{/* valid image data */};
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(validImageData));

        when(imageTextReaderService.extractTextFromImage(file)).thenThrow(new TesseractException("Error"));

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error processing image", response.getBody());
    }
}
