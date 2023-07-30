import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ShapeDrawingApplet extends JApplet {
    private String[] shapes = {"Line", "Rectangle", "Oval", "Triangle", "Star", "Parallelogram"};
    private Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.ORANGE, Color.PINK,
            Color.CYAN, Color.MAGENTA, Color.DARK_GRAY,
            Color.LIGHT_GRAY
    };

    private JComboBox<String> shapeComboBox;
    private JCheckBox filledCheckBox;
    private JButton eraserButton;
    private JButton penButton;
    private JButton colorButton;
    private JSlider thicknessSlider;
    private DrawingPanel drawingPanel;

    public void init() {
        setLayout(new BorderLayout());

        shapeComboBox = new JComboBox<>(shapes);

        Font font = new Font("Arial", Font.BOLD, 14);
        shapeComboBox.setFont(font);
        shapeComboBox.setForeground(Color.BLUE);
        shapeComboBox.setBackground(Color.LIGHT_GRAY);

        colorButton = new JButton("Color");
        colorButton.setPreferredSize(new Dimension(80, 20));
        colorButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChooser = new JColorChooser();
                Color chosenColor = colorChooser.showDialog(ShapeDrawingApplet.this, "Choose Color", drawingPanel.getCurrentColor());
                if (chosenColor != null) {
                    drawingPanel.setCurrentColor(chosenColor);
                }
            }
        });
		filledCheckBox = new JCheckBox("Fill Shapes");
        eraserButton = new JButton("Eraser");
        penButton = new JButton("Pen");
        thicknessSlider = new JSlider(1, 10, 1);
        drawingPanel = new DrawingPanel();

        ButtonGroup radioGroup = new ButtonGroup();

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Shape: "));
        controlPanel.add(shapeComboBox);
        controlPanel.add(new JLabel("Color: "));
        controlPanel.add(colorButton);
        controlPanel.add(filledCheckBox);
        controlPanel.add(eraserButton);
        controlPanel.add(penButton);
        controlPanel.add(new JLabel("Thickness: "));
        controlPanel.add(thicknessSlider);

        eraserButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setEraserMode(!drawingPanel.isEraserMode());
            }
        });

        shapeComboBox.addActionListener(new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setCurrentShape(shapeComboBox.getSelectedItem().toString());
            }
        });

        filledCheckBox.addActionListener(new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setFillShapes(filledCheckBox.isSelected());
            }
        });

        thicknessSlider.addChangeListener(e -> {
            int thickness = thicknessSlider.getValue();
            drawingPanel.setCurrentThickness(thickness);
        });

        penButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setCurrentColor(Color.BLACK);
                drawingPanel.setCurrentThickness(2);
                drawingPanel.setEraserMode(false);
                drawingPanel.setCurrentShape("Pen");
            }
        });

        add(controlPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
    }
    private static class DrawingPanel extends JPanel {
        private String currentShape;
        private Color currentColor;
        private boolean fillShapes;
        private boolean eraserMode;
        private int currentThickness;
        private int startX, startY, endX, endY;
        private ArrayList<ShapeData> shapesList = new ArrayList<>();
        private ArrayList<Point> penPointsList = new ArrayList<>();
        private BufferedImage bufferImage;

        public DrawingPanel() {
            currentShape = "Line";
            currentColor = Color.RED;
            fillShapes = false;
            eraserMode = false;
            currentThickness = 1;

            addMouseListener(new MouseAdapter() {
                
                public void mousePressed(MouseEvent e) {
                    startX = e.getX();
                    startY = e.getY();
                    endX = startX;
                    endY = startY;

                    if (eraserMode) {
                        for (int i = shapesList.size() - 1; i >= 0; i--) {
                            ShapeData shapeData = shapesList.get(i);
                            int x1 = shapeData.getStartX();
                            int y1 = shapeData.getStartY();
                            int x2 = shapeData.getEndX();
                            int y2 = shapeData.getEndY();

                            // Check if the pressed point is inside the shape's bounding box
                            if (startX >= Math.min(x1, x2) && startX <= Math.max(x1, x2) &&
                                    startY >= Math.min(y1, y2) && startY <= Math.max(y1, y2)) {
                                shapesList.remove(i);
                                bufferImage = null;
                                repaint();
                                break;
                            }
                        }

                        setCurrentColor(getBackground()); // Change the color to the background color (eraser color)
                    } else if (currentShape.equals("Pen")) {
                        penPointsList.clear();
                        penPointsList.add(new Point(startX, startY));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int width = e.getX() - startX;
                    int height = e.getY() - startY;

                    if (!eraserMode && !currentShape.equals("Pen")) {
                        shapesList.add(new ShapeData(startX, startY, endX, endY, currentShape, currentColor, fillShapes, currentThickness));
                    }

                    bufferImage = null;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseAdapter() {
               
                public void mouseDragged(MouseEvent e) {
                    endX = e.getX();
                    endY = e.getY();

                    if (currentShape.equals("Pen")) {
                        penPointsList.add(new Point(endX, endY));
                    }

                    repaint();
                }
         });
         }
public void setCurrentShape(String shape) {
            currentShape = shape;
        }

        public void setCurrentColor(Color color) {
            currentColor = color;
        }

        public void setFillShapes(boolean fill) {
            fillShapes = fill;
        }

        public boolean isEraserMode() {
            return eraserMode;
        }

        public void setEraserMode(boolean eraser) {
            eraserMode = eraser;
            if (eraserMode) {
                currentColor = getBackground();
            }
        }

        public void setCurrentThickness(int thickness) {
            currentThickness = thickness;
        }

        public Color getCurrentColor() {
            return currentColor;
        }

       
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (bufferImage == null) {
                bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) bufferImage.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setStroke(new BasicStroke(currentThickness));
                for (ShapeData shapeData : shapesList) {
                    g2d.setColor(shapeData.getColor());
                    String shape = shapeData.getShape();
                    int x1 = shapeData.getStartX();
                    int y1 = shapeData.getStartY();
                    int x2 = shapeData.getEndX();
                    int y2 = shapeData.getEndY();
                    if (shape.equals("Line")) {
                        g2d.drawLine(x1, y1, x2, y2);
                    } else if (shape.equals("Rectangle")) {
                        int width = x2 - x1;
                        int height = y2 - y1;
                        if (shapeData.isFillShapes()) {
                            g2d.fillRect(x1, y1, width, height);
                        } else {
                            g2d.drawRect(x1, y1, width, height);
                        }
                    } else if (shape.equals("Oval")) {
                        int width = x2 - x1;
                        int height = y2 - y1;
                        if (shapeData.isFillShapes()) {
                            g2d.fillOval(x1, y1, width, height);
                        } else {
                            g2d.drawOval(x1, y1, width, height);
                        }
                    } else if (shape.equals("Triangle")) {
                        int[] xPoints = {x1, (x1 + x2) / 2, x2};
                        int[] yPoints = {y2, y1, y2};
                        if (shapeData.isFillShapes()) {
                            g2d.fillPolygon(xPoints, yPoints, 3);
                        } else {
                            g2d.drawPolygon(xPoints, yPoints, 3);
                        }
                    } else if (shape.equals("Star")) {
                        int armLength = (int) (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) / 2);
                        int cx = (x1 + x2) / 2;
                        int cy = (y1 + y2) / 2;
                        int outerArmLength = armLength * 2;
                        int innerArmLength = armLength;
                        int rotation = -18; // Change this value to adjust the star shape
                        int[] xPoints = new int[10];
                        int[] yPoints = new int[10];
                        for (int i = 0; i < 10; i++) {
                            int angle = (i * 36 + rotation) % 360;
                            if (i % 2 == 0) {
                                xPoints[i] = cx + (int) (Math.cos(Math.toRadians(angle)) * outerArmLength);
                                yPoints[i] = cy + (int) (Math.sin(Math.toRadians(angle)) * outerArmLength);
                            } else {
                                xPoints[i] = cx + (int) (Math.cos(Math.toRadians(angle)) * innerArmLength);
                                yPoints[i] = cy + (int) (Math.sin(Math.toRadians(angle)) * innerArmLength);
                            }
                        }
                        if (shapeData.isFillShapes()) {
                            g2d.fillPolygon(xPoints, yPoints, 10);
                        } else {
                            g2d.drawPolygon(xPoints, yPoints, 10);
                        }
                    } else if (shape.equals("Parallelogram")) {
                        int[] xPoints = {x1, x1 + (x2 - x1) / 3, x2, x1 + (x2 - x1) * 2 / 3};
                        int[] yPoints = {y1, y1, y2, y2};
                        if (shapeData.isFillShapes()) {
                            g2d.fillPolygon(xPoints, yPoints, 4);
                        } else {
                            g2d.drawPolygon(xPoints, yPoints, 4);
                        }
                    }
                }
                g2d.dispose();
            }
			if (bufferImage != null) {
                g.drawImage(bufferImage, 0, 0, null);
            }

            if (!eraserMode && !currentShape.equals("Pen")) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(currentColor);
                g2d.setStroke(new BasicStroke(currentThickness));
                if (currentShape.equals("Line")) {
                    g2d.drawLine(startX, startY, endX, endY);
                } else if (currentShape.equals("Rectangle")) {
                    int width = endX - startX;
                    int height = endY - startY;
                    if (fillShapes) {
                        g2d.fillRect(startX, startY, width, height);
                    } else {
                        g2d.drawRect(startX, startY, width, height);
                    }
                } else if (currentShape.equals("Oval")) {
                    int width = endX - startX;
                    int height = endY - startY;
                    if (fillShapes) {
                        g2d.fillOval(startX, startY, width, height);
                    } else {
                        g2d.drawOval(startX, startY, width, height);
                    }
                } else if (currentShape.equals("Triangle")) {
                    int[] xPoints = {startX, (startX + endX) / 2, endX};
                    int[] yPoints = {endY, startY, endY};
                    if (fillShapes) {
                        g2d.fillPolygon(xPoints, yPoints, 3);
                    } else {
                        g2d.drawPolygon(xPoints, yPoints, 3);
                    }
                } else if (currentShape.equals("Star")) {
                    int armLength = (int) (Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)) / 2);
                    int cx = (startX + endX) / 2;
                    int cy = (startY + endY) / 2;
                    int outerArmLength = armLength * 2;
                    int innerArmLength = armLength;
                    int rotation = -18; // Change this value to adjust the star shape
                    int[] xPoints = new int[10];
                    int[] yPoints = new int[10];
                    for (int i = 0; i < 10; i++) {
                        int angle = (i * 36 + rotation) % 360;
                        if (i % 2 == 0) {
                            xPoints[i] = cx + (int) (Math.cos(Math.toRadians(angle)) * outerArmLength);
                            yPoints[i] = cy + (int) (Math.sin(Math.toRadians(angle)) * outerArmLength);
                        } else {
                            xPoints[i] = cx + (int) (Math.cos(Math.toRadians(angle)) * innerArmLength);
                            yPoints[i] = cy + (int) (Math.sin(Math.toRadians(angle)) * innerArmLength);
                        }
                    }
                    if (fillShapes) {
                        g2d.fillPolygon(xPoints, yPoints, 10);
                    } else {
                        g2d.drawPolygon(xPoints, yPoints, 10);
                    }
                } else if (currentShape.equals("Parallelogram")) {
                    int[] xPoints = {startX, startX + (endX - startX) / 3, endX, startX + (endX - startX) * 2 / 3};
                    int[] yPoints = {startY, startY, endY, endY};
                    if (fillShapes) {
                        g2d.fillPolygon(xPoints, yPoints, 4);
                    } else {
                        g2d.drawPolygon(xPoints, yPoints, 4);
                    }
                }
            } else if (!eraserMode && currentShape.equals("Pen")) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(currentColor);
                g2d.setStroke(new BasicStroke(currentThickness));
                if (!penPointsList.isEmpty()) {
                    Point prevPoint = penPointsList.get(0);
                    for (int i = 1; i < penPointsList.size(); i++) {
                        Point currentPoint = penPointsList.get(i);
                        g2d.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
                        prevPoint = currentPoint;
                    }
                }
            }
        }
    }

    private static class ShapeData {
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private String shape;
        private Color color;
        private boolean fillShapes;
        private int thickness;

        public ShapeData(int startX, int startY, int endX, int endY, String shape, Color color, boolean fillShapes, int thickness) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.shape = shape;
            this.color = color;
            this.fillShapes = fillShapes;
            this.thickness = thickness;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getEndX() {
            return endX;
        }

        public int getEndY() {
            return endY;
        }

        public String getShape() {
            return shape;
        }

        public Color getColor() {
            return color;
        }

        public boolean isFillShapes() {
            return fillShapes;
        }

        public int getThickness() {
            return thickness;
        }
    }
}