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