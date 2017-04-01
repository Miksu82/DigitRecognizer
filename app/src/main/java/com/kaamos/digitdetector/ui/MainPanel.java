package com.kaamos.digitdetector.ui;

import com.kaamos.digitdetector.DigitDetector;
import com.kaamos.digitdetector.Main;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Created by Mika on 27/03/17.
 */
public class MainPanel extends JPanel implements NumberDrawnListener {

    private static final int TEXTBOX_HEIGHT = 50;

    private final JPanel drawPanelContainer;
    private final DrawPanel drawPanel;
    private final JButton showMnistData;
    private final JLabel croppedImage;
    private final JLabel scaledImage;
    private final JLabel finalImage;
    private final JTextField resultText;
    private final DigitDetector digitDetector;

    public MainPanel() throws UnsupportedKerasConfigurationException,
                              IOException,
                              InvalidKerasConfigurationException {
        // Init fields
        digitDetector = new DigitDetector();
        drawPanel = new DrawPanel(this);
        showMnistData = new JButton();
        drawPanelContainer = new JPanel();
        croppedImage = new JLabel();
        scaledImage = new JLabel();
        finalImage = new JLabel();
        resultText = new JTextField();

        // Set image labels
        croppedImage.setText("Cropped image");
        scaledImage.setText("Cropped image scaled to 20x20 box");
        finalImage.setText("Final 28x28 model input image");
        showMnistData.setText("Show 100 MNIST training data samples");

        // Set JLabel text positions
        croppedImage.setVerticalTextPosition(SwingConstants.BOTTOM);
        scaledImage.setVerticalTextPosition(SwingConstants.BOTTOM);
        finalImage.setVerticalTextPosition(SwingConstants.BOTTOM);

        croppedImage.setHorizontalTextPosition(SwingConstants.CENTER);
        scaledImage.setHorizontalTextPosition(SwingConstants.CENTER);
        finalImage.setHorizontalTextPosition(SwingConstants.CENTER);

        drawPanelContainer.setBackground(Color.LIGHT_GRAY);

        // Layout panels
        final JPanel rightSideContainer = new JPanel();
        rightSideContainer.setLayout(new BoxLayout(rightSideContainer, BoxLayout.Y_AXIS));

        final JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.X_AXIS));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setComponentSizes();

        rightSideContainer.add(croppedImage);
        rightSideContainer.add(scaledImage);
        rightSideContainer.add(finalImage);

        drawPanelContainer.add(drawPanel);
        drawPanelContainer.add(resultText);
        drawPanelContainer.add(showMnistData);

        topContainer.add(drawPanelContainer);
        topContainer.add(rightSideContainer);
        this.add(topContainer);

        showMnistData.addActionListener(event -> {
            final JFrame frame = new JFrame("First 100 MNIST training data");
            final int mnistImageRectangleSize = (ImageUtils.MNIST_IMAGE_SIZE + MnistPanel.PADDING);
            final int frameWidth = MnistPanel.NUMBER_OF_IMAGES_IN_ROW * mnistImageRectangleSize;
            final int frameHeight = MnistPanel.NUMBER_OF_IMAGES_IN_COLUMN * mnistImageRectangleSize;
            frame.setSize(frameWidth, frameHeight);
            frame.setResizable(false);

            try {
                final MnistPanel mnistPanel = new MnistPanel();
                frame.add(mnistPanel);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

    private void setComponentSizes() {
        final int half = Main.UI_SIZE / 2;
        final int leftSizeComponentHeight = Main.UI_SIZE;
        final int rightSideComponentHeight = Main.UI_SIZE / 3;

        final Dimension rightSideComponentSize = new Dimension(half, rightSideComponentHeight);
        drawPanelContainer.setPreferredSize(new Dimension(half, leftSizeComponentHeight));
        drawPanel.setPreferredSize(new Dimension(half, half));
        showMnistData.setPreferredSize(new Dimension(half, TEXTBOX_HEIGHT));

        croppedImage.setPreferredSize(rightSideComponentSize);
        scaledImage.setPreferredSize(rightSideComponentSize);
        finalImage.setPreferredSize(rightSideComponentSize);
        resultText.setPreferredSize(new Dimension(half, TEXTBOX_HEIGHT));

        // Apparently JLabel needs maximum size set also or the icon does
        // not align correctly. I don't understand Swing...
        croppedImage.setMaximumSize(rightSideComponentSize);
        scaledImage.setMaximumSize(rightSideComponentSize);
        finalImage.setMaximumSize(rightSideComponentSize);

        croppedImage.setHorizontalAlignment(SwingConstants.CENTER);
        scaledImage.setHorizontalAlignment(SwingConstants.CENTER);
        finalImage.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void drawingFinished(final BufferedImage image) {

        final Rectangle bounds = ImageUtils.findBoundsOfBlackShape(image);
        final Dimension newDim = getScaledMnistDigitDimensions(bounds);

        // Get only the digit out from the rest of the image
        final BufferedImage cropped = image.getSubimage(bounds.x,
                                                        bounds.y,
                                                        bounds.width,
                                                        bounds.height);

        // Scale the digit to 20x20 box as required by MNIST
        final BufferedImage scaled = ImageUtils.scale(cropped, newDim.width, newDim.height);

        // Put the 20x20 image to 28x28 background and center it by the
        // center of mass.
        final BufferedImage mnistInputImage
                = ImageUtils.addImageToCenter(scaled,
                                              ImageUtils.MNIST_IMAGE_SIZE,
                                              ImageUtils.MNIST_IMAGE_SIZE);

        // Add images to the UI
        croppedImage.setIcon(new ImageIcon(cropped));
        scaledImage.setIcon(new ImageIcon(scaled));
        finalImage.setIcon(new ImageIcon(mnistInputImage));

        final int result = digitDetector.recognize(mnistInputImage);
        resultText.setText("You wrote: " + result);
    }

    // Shamelessly copied from
    // http://stackoverflow.com/questions/10245220/java-image-resize-maintain-aspect-ratio
    private static Dimension getScaledMnistDigitDimensions(final Rectangle bounds) {

        final int originalWidth = bounds.width;
        final int originalHeight = bounds.height;
        final int boundWidth = ImageUtils.MNIST_DIGIT_BOUNDS_SIZE;
        final int boundHeight = ImageUtils.MNIST_DIGIT_BOUNDS_SIZE;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // first check if we need to scale width
        if (originalWidth > boundWidth) {
            //scale width to fit
            newWidth = boundWidth;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (newHeight > boundHeight) {
            //scale height to fit instead
            newHeight = boundHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return new Dimension(newWidth, newHeight);
    }
}
