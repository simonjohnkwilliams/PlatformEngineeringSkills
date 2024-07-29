package com.trainDelay.calculator;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageTextReaderService {

    public String[] extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Path to tessdata directory

        String text = tesseract.doOCR(file.getInputStream());
        return text.split("\\r?\\n");
    }
}