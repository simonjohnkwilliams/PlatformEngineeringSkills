// TesseractWrapperImpl.java
package com.trainDelay.calculator.phase2;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class TesseractWrapperImpl implements TesseractWrapper {
    private final Tesseract tesseract;

    public TesseractWrapperImpl() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("tessdata"); // Path to tessdata directory
    }

    @Override
    public String doOCR(BufferedImage image) throws TesseractException {
        return tesseract.doOCR(image);
    }
}