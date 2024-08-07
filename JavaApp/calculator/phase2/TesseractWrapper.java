// TesseractWrapper.java
package com.trainDelay.calculator.phase2;

import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public interface TesseractWrapper {
    String doOCR(BufferedImage image) throws TesseractException;
}