// ImagePreProcessingService.java
package com.trainDelay.calculator.phase2;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImagePreProcessingService {

    static {
        // Set the path to the directory containing the OpenCV native library
        System.setProperty("java.library.path", "C:\\Users\\simon\\.m2\\repository\\org\\openpnp\\opencv\\4.5.1-2\\opencv-4.5.1-2.jar");
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public byte[] preProcessImage(MultipartFile file) throws IOException {
        // Convert MultipartFile to Mat
        Path tempFile = Files.createTempFile("temp", ".png");
        file.transferTo(tempFile.toFile());
        Mat image = Imgcodecs.imread(tempFile.toString(), Imgcodecs.IMREAD_GRAYSCALE);


        // Convert the image to binary once
        Mat binaryImage = new Mat();
        Imgproc.threshold(image, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        //Deskew the image
        Mat deskewedImage = deskewImage(binaryImage);

        Mat bwColours = updateBackgroundColours(deskewedImage);
        // Trim the background
        //Mat trimmedImage = trimBackground(deskewedImage);

        // Convert to black and white
        //Mat bwImage = convertToBlackAndWhite(image);

        // Binarize the image
        //Mat binarizedImage = binarizeImage(bwImage);

        // Convert Mat back to byte array
        byte[] processedImage = new byte[(int) (bwColours.total() * bwColours.channels())];
        //bwImage.get(0, 0, processedImage);

        // Write the processed image back to disk
        //Path processedFilePath = Paths.get("processed_image.png");
        //Imgcodecs.imwrite(processedFilePath.toString(), bwImage);

        // Clean up temporary file
        Files.delete(tempFile);

        return processedImage;
    }

   private Mat updateBackgroundColours(Mat image) {
    // Check the number of channels in the input image
    if (image.channels() == 1) {
        // Convert single-channel image to 3-channel BGR image
        Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
    }

    Mat grayImage = new Mat();
    Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

    // Create a mask for non-black pixels
    Mat mask = new Mat();
    Core.inRange(grayImage, new Scalar(1), new Scalar(255), mask);

    // Set non-black pixels to white
    image.setTo(new Scalar(255, 255, 255), mask);
    Imgcodecs.imwrite("mono.png", image);

    return image;
}
    private Mat deskewImage(Mat binaryImage) {
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the minimum area rectangle that bounds the contours
        MatOfPoint2f largestContour = new MatOfPoint2f();
        double maxArea = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                contour.convertTo(largestContour, CvType.CV_32F);
            }
        }

        RotatedRect minRect = Imgproc.minAreaRect(largestContour);

        // Calculate the angle of the rectangle
        double angle = minRect.angle;
        if (angle < -45) {
            angle += 90;
        }

        // Rotate the image by the calculated angle
        Mat rotated = new Mat();
        Point center = new Point(binaryImage.width() / 2.0, binaryImage.height() / 2.0);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        Imgproc.warpAffine(binaryImage, rotated, rotationMatrix, binaryImage.size(), Imgproc.INTER_CUBIC);

        return rotated;
    }

    private Mat trimBackground(Mat binaryImage) {
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Identify the largest contour
        double maxArea = 0;
        MatOfPoint largestContour = new MatOfPoint();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        // Compute the bounding box of the largest contour
        Rect boundingBox = Imgproc.boundingRect(largestContour);

        // Crop the image to the bounding box
        Mat cropped = new Mat(binaryImage, boundingBox);

        return cropped;
    }

    private Mat convertToBlackAndWhite(Mat binaryImage) {
        Mat bw = new Mat();
        Imgproc.threshold(binaryImage, bw, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        return bw;
    }

    private Mat binarizeImage(Mat bwImage) {
        Mat binarized = new Mat();
        Imgproc.adaptiveThreshold(bwImage, binarized, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        return binarized;
    }
}