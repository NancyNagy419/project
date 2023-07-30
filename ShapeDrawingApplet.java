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
            @Override
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
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setEraserMode(!drawingPanel.isEraserMode());
            }
        });

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setCurrentShape(shapeComboBox.getSelectedItem().toString());
            }
        });

        filledCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.setFillShapes(filledCheckBox.isSelected());
            }
        });

        thicknessSlider.addChangeListener(e -> {
            int thickness = thicknessSlider.getValue();
            drawingPanel.setCurrentThickness(thickness);
        });

        penButton.addActionListener(new ActionListener() {
            @Override
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
                @Override
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
                @Override
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