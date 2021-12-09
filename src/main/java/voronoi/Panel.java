package voronoi;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private Map map;

    public Panel() {
        setAutoscrolls(true);

        map = new Map();
        JScrollPane pictureScrollPane = new JScrollPane(map, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        pictureScrollPane.setPreferredSize(new Dimension(950, 950));
        pictureScrollPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));


        pictureScrollPane.setVisible(true);
        add(pictureScrollPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("ScrollDemo");
//                frame.setSize(WIDTH, WIDTH);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);

                //Create and set up the content pane.
                JComponent newContentPane = new Panel();
                newContentPane.setOpaque(true); //content panes must be opaque
                frame.setContentPane(newContentPane);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
