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