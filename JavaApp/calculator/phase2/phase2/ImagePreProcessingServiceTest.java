// ImagePreProcessingServiceTest.java
package com.trainDelay.calculator.phase2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImagePreProcessingServiceTest {

    private ImagePreProcessingService imagePreProcessingService;
    final static Path currentDir = Paths.get("");
    final static Path validPath = Paths.get(currentDir.toAbsolutePath().toString(), "calculator", "src", "test", "resources", "20240726_175456.jpg");

    @BeforeEach
    public void setUp() {
        imagePreProcessingService = new ImagePreProcessingService();
    }

    @Test
    public void testPreProcessImage() throws IOException {
        // Load a sample image file
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "sample_image.png", "image/png", validImageData);

        // Pre-process the image
        byte[] processedImageBytes = imagePreProcessingService.preProcessImage(file);

        // Verify the processed image is not null
        Assertions.assertNotNull(processedImageBytes);

        // Convert byte array back to Mat for further verification
        Mat processedImage = Imgcodecs.imdecode(new Mat(processedImageBytes.length, 1, CvType.CV_8U), Imgcodecs.IMREAD_GRAYSCALE);

        // Verify the processed image is not empty
        Assertions.assertNotNull(processedImage);
    }
}