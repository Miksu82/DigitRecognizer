package com.kaamos.digitdetector.ui;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
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

    public static final int NUMBER_OF_IMAGES_IN_ROW = 10;
    public static final int NUMBER_OF_IMAGES_IN_COLUMN = 10;
    public static final int PADDING = 4;

    public MnistPanel() throws IOException {
        this.setLayout(new GridLayout(NUMBER_OF_IMAGES_IN_ROW, NUMBER_OF_IMAGES_IN_COLUMN));
        final INDArray mnistImagesFlatten = Nd4j.readNumpy("../trainer/mnist.csv");

        final int[] mnistImagesShape = new int[] {NUMBER_OF_IMAGES,
                                                  ImageUtils.MNIST_IMAGE_SIZE,
                                                  ImageUtils.MNIST_IMAGE_SIZE};
        final INDArray mnistImages = mnistImagesFlatten.reshape(mnistImagesShape);
        final double[] mnistImageRawData = mnistImages.data().asDouble();
        final int oneMnistImageLength = ImageUtils.MNIST_IMAGE_SIZE * ImageUtils.MNIST_IMAGE_SIZE;
        for (int imageIndex = 0; imageIndex < NUMBER_OF_IMAGES; imageIndex++) {
            // MNIST images have been exported as unsigned bytes...
            final double[] oneMnistImageRaw = Arrays.copyOfRange(mnistImageRawData,
                                                                 imageIndex * oneMnistImageLength,
                                                                 (imageIndex + 1) * oneMnistImageLength);

            // Now we need to convert the to signed bytes so that Java understands them
            final byte[] oneMnistImageSigned = new byte[oneMnistImageRaw.length];
            for (int i = 0; i < oneMnistImageRaw.length; i++) {
                oneMnistImageSigned[i] = (byte) (~(int) oneMnistImageRaw[i] & 0xFF);
            }

            // Create the image
            final DataBufferByte dataBuffer = new DataBufferByte(oneMnistImageSigned,
                                                                 oneMnistImageLength);

            final BufferedImage mnistImage = new BufferedImage(ImageUtils.MNIST_IMAGE_SIZE,
                                                               ImageUtils.MNIST_IMAGE_SIZE,
                                                               BufferedImage.TYPE_BYTE_GRAY);

            final Raster raster = Raster.createRaster(mnistImage.getSampleModel(),
                                                      dataBuffer,
                                                      new Point());
            mnistImage.setData(raster);

            // Create the UI component
            final JLabel mnistImageLabel = new JLabel();
            final int manistImageLabelSize = ImageUtils.MNIST_IMAGE_SIZE + PADDING;
            mnistImageLabel.setPreferredSize(new Dimension(manistImageLabelSize,
                                                           manistImageLabelSize));
            mnistImageLabel.setMaximumSize(new Dimension(manistImageLabelSize,
                                                         manistImageLabelSize));
            mnistImageLabel.setIcon(new ImageIcon(mnistImage));

            this.add(mnistImageLabel);
        }
    }
}
