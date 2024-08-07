// ImageTextReaderServiceTest.java
package com.trainDelay.calculator.phase2;

import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ImageTextReaderServiceTest {

    final static Path currentDir = Paths.get("");
    final static Path validPath = Paths.get(currentDir.toAbsolutePath().toString(), "calculator", "src", "test", "resources", "20240726_175456.jpg");

    @Mock
    private TesseractWrapper tesseractWrapper;

    @InjectMocks
    private ImageTextReaderService imageTextReaderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExtractTextFromImage_EmptyFile() throws IOException, TesseractException {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{""}, result);
    }

    @Test
    public void testExtractTextFromImage_SingleLine() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("single line");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"single line"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLines() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\nline2\nline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    }

    @Test
    public void testExtractTextFromImage_EmptyLines() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("\n\n");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"", "", ""}, result);
    }

    @Test
    public void testExtractTextFromImage_WhitespaceLines() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("   \n   \n");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"   ", "   ", ""}, result);
    }

    @Test
    public void testExtractTextFromImage_SpecialCharacters() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("!@#$%^&*()");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"!@#$%^&*()"}, result);
    }

    @Test
    public void testExtractTextFromImage_Numbers() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("1234567890");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"1234567890"}, result);
    }

    @Test
    public void testExtractTextFromImage_MixedContent() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\n123\n!@#");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1", "123", "!@#"}, result);
    }

    @Test
    public void testExtractTextFromImage_EmptyFile_Exception() throws IOException, TesseractException {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenThrow(new TesseractException("Error"));

        Assertions.assertThrows(TesseractException.class, () -> {
            imageTextReaderService.extractTextFromImage(file);
        });
    }

    @Test
    public void testExtractTextFromImage_ValidImage_Exception() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenThrow(new TesseractException("Error"));

        Assertions.assertThrows(TesseractException.class, () -> {
            imageTextReaderService.extractTextFromImage(file);
        });
    }

    @Test
    public void testExtractTextFromImage_NullFile() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            imageTextReaderService.extractTextFromImage(null);
        });
    }

    @Test
    public void testExtractTextFromImage_EmptyString() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{""}, result);
    }

    @Test
    public void testExtractTextFromImage_SingleCharacter() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("a");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"a"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleCharacters() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("abc");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"abc"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithSpaces() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1 \n line2 \n line3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1 ", " line2 ", " line3"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithTabs() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\t\n\tline2\t\n\tline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1\t", "\tline2\t", "\tline3"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithMixedWhitespace() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1 \t\n \tline2 \t\n \tline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1 \t", " \tline2 \t", " \tline3"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithNewlines() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\n\nline2\n\nline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1", "", "line2", "", "line3"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithCarriageReturns() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\r\nline2\r\nline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    }

    @Test
    public void testExtractTextFromImage_MultipleLinesWithMixedNewlines() throws IOException, TesseractException {
        byte[] validImageData = Files.readAllBytes(validPath);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validImageData);

        Mockito.when(tesseractWrapper.doOCR(ImageIO.read(file.getInputStream()))).thenReturn("line1\nline2\r\nline3");

        String[] result = imageTextReaderService.extractTextFromImage(file);

        Assertions.assertArrayEquals(new String[]{"line1", "line2", "line3"}, result);
    }
}