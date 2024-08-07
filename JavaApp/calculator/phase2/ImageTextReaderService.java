// ImageTextReaderService.java
package com.trainDelay.calculator.phase2;

import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageTextReaderService {

    private final TesseractWrapper tesseractWrapper;
    private final ImagePreProcessingService imagePreProcessingService;

    @Autowired
    public ImageTextReaderService(TesseractWrapper tesseractWrapper, ImagePreProcessingService imagePreProcessingService) {
        this.tesseractWrapper = tesseractWrapper;
        this.imagePreProcessingService = imagePreProcessingService;
    }

    public String[] extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        // Convert MultipartFile to BufferedImage
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        // Perform OCR on the pre-processed image
        String text = tesseractWrapper.doOCR(bufferedImage);
        return text.split("\\r?\\n");
    }


}