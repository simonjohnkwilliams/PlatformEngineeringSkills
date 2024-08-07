package com.trainDelay.calculator.phase2;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class ImageUploadControllerTest {
    final static Path currentDir = Paths.get("");

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

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("File is empty", response.getBody());
    }

    @Test
    public void testHandleImageUpload_InvalidImageFormat() throws IOException {
        byte[] invalidImageData = "invalid image data".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", invalidImageData);

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Invalid image format", response.getBody());
    }

    @Test
    public void testHandleImageUpload_ValidImage() throws IOException, TesseractException {
        Path path = Paths.get(currentDir.toAbsolutePath().toString()+ "/calculator/src/test/resources/20240726_175456.jpg");
        if(Files.exists(path)){
            byte[] validImageData = Files.readAllBytes(path);
            MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

            Mockito.when(imageTextReaderService.extractTextFromImage(file)).thenReturn(new String[]{"Off—Peak Day Return", "from London Terminals ee", "fron Godaining 26-JLY-24\n"});

            ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("line1\nline2", response.getBody());
        }
        else {
            assert(false);
        }

    }

    @Test
    public void testHandleImageUpload_ErrorProcessingImage() throws IOException, TesseractException {
        Path path = Paths.get(currentDir.toAbsolutePath().toString()+ "/calculator/src/test/resources/20240726_175456.jpg");
        byte[] validImageData = Files.readAllBytes(path);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(imageTextReaderService.extractTextFromImage(file)).thenThrow(new TesseractException("Error"));

        ResponseEntity<String> response = imageUploadController.handleImageUpload(file);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Error processing image", response.getBody());
    }
}