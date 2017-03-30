package com.kaamos.digitdetector.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {

    final private List<Point> pointsDrawn;
    final private NumberDrawnListener listener;

    public DrawPanel(final NumberDrawnListener listener) {
        pointsDrawn = new ArrayList<>();
        this.listener = listener;

        this.setBackground(Color.WHITE);
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                pointsDrawn.clear();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                final BufferedImage image = createImage();
                listener.drawingFinished(image);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                pointsDrawn.add(e.getPoint());
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D graphics = (Graphics2D) g;
        final BasicStroke stroke = new BasicStroke(20);
        graphics.setStroke(stroke);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < pointsDrawn.size() - 1; i++) {
            final Point p1 = pointsDrawn.get(i);
            final Point p2 = pointsDrawn.get(i+1);
            graphics.draw(new Line2D.Double(p1, p2));
        }
    }

    private BufferedImage createImage() {
        final int width = this.getWidth();
        final int height = this.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics2D graphics = image.createGraphics();
        this.paint(graphics);
        graphics.dispose();
        return image;
    }
}
