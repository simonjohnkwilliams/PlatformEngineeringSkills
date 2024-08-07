/** package com.trainDelay.calculator.phase2;

import com.trainDelay.calculator.Ticket;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ImageUploadController {

    @Autowired
    private ImageTextReaderService imageTextReaderService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return new ResponseEntity<>("Invalid image format", HttpStatus.BAD_REQUEST);
            }

            String[] lines = imageTextReaderService.extractTextFromImage(file);
            Ticket ticket = TicketParser.parseTicket(String.join("\n", lines));
            //now we take the ticket information and look up the late train times.
            return new ResponseEntity<>(String.join("\n", lines), HttpStatus.OK);

        } catch (IOException | TesseractException e) {
            return new ResponseEntity<>("Error processing image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
 /*