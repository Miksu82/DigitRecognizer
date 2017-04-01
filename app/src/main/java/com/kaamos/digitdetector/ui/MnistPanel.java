package com.kaamos.digitdetector.ui;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created by Mika on 29/03/17.
 */
public class MnistPanel extends JPanel {

    private static final int NUMBER_OF_IMAGES = 100;
    private static final int MNIST_IMAGE_LENGTH
            = ImageUtils.MNIST_IMAGE_SIZE * ImageUtils.MNIST_IMAGE_SIZE;


    public static final int NUMBER_OF_IMAGES_IN_ROW = 10;
    public static final int NUMBER_OF_IMAGES_IN_COLUMN = 10;
    public static final int PADDING = 4;

    private static final int MNIST_IMAGE_LABEL_SIZE = ImageUtils.MNIST_IMAGE_SIZE + PADDING;

    public MnistPanel() throws IOException {
        this.setLayout(new GridLayout(NUMBER_OF_IMAGES_IN_ROW, NUMBER_OF_IMAGES_IN_COLUMN));
        final INDArray mnistImagesFlatten = Nd4j.readNumpy("../trainer/mnist.csv");

        final double[] mnistImageRawData =  getMnistRawPixels(mnistImagesFlatten);

        for (int imageIndex = 0; imageIndex < NUMBER_OF_IMAGES; imageIndex++) {
            final byte[] oneMnistImage = getOneMnistImage(mnistImageRawData, imageIndex);
            final BufferedImage image = ImageUtils.convertBytesToImage(oneMnistImage);
            addImageToUi(image);
        }
    }

    private double[] getMnistRawPixels(final INDArray mnistData) {
        final int[] mnistImagesShape = new int[] {NUMBER_OF_IMAGES,
                                                  ImageUtils.MNIST_IMAGE_SIZE,
                                                  ImageUtils.MNIST_IMAGE_SIZE};
        final INDArray mnistImages = mnistData.reshape(mnistImagesShape);
        return mnistImages.data().asDouble();
    }

    private byte[] getOneMnistImage(final double[] rawMnistData, final int imageNumber) {
        // MNIST images have been exported as unsigned bytes...
        final double[] oneMnistImageRaw = Arrays.copyOfRange(rawMnistData,
                                                             imageNumber * MNIST_IMAGE_LENGTH,
                                                             (imageNumber + 1) * MNIST_IMAGE_LENGTH);

        // Now we need to convert them to signed bytes so that Java understands them
        final byte[] oneMnistImageSigned = new byte[oneMnistImageRaw.length];
        for (int i = 0; i < oneMnistImageRaw.length; i++) {
            oneMnistImageSigned[i] = (byte) (~(int) oneMnistImageRaw[i] & 0xFF);
        }

        return oneMnistImageSigned;
    }

    private void addImageToUi(final BufferedImage image) {
        // Create the UI component
        final JLabel mnistImageLabel = new JLabel();
        final Dimension labelSize = new Dimension(MNIST_IMAGE_LABEL_SIZE, MNIST_IMAGE_LABEL_SIZE);
        mnistImageLabel.setPreferredSize(labelSize);
        mnistImageLabel.setMaximumSize(labelSize);
        mnistImageLabel.setIcon(new ImageIcon(image));
        this.add(mnistImageLabel);
    }
}
