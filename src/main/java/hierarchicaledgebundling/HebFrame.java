package hierarchicaledgebundling;

import com.google.gson.Gson;
import style.EmailColors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HebFrame extends JPanel{
    private int[][][] adjacencyMatrix;
    // This is a mapping from the email to the user's name, id, dept, and deptid
    // Keys of the sub-map are "Name", "Id", "Department", "DeptId"
    private final Map<String, Employee> emailNameIdMap = new HashMap<>();

    // For easier access of which IDs belong to who
    private final Map<Integer, String> idEmailMap = new HashMap<>();
    private int radius;
    private final int padding = 180;

    private int startFilter = 0;
    private int endFilter = 6;  // end is inclusive of the day

    private int maxValue = 0;

    public HebFrame(String jsonString) {

        JLabel title = new JLabel("<html>Hierarchical Edge Bundling of <br/> Employee Email Communication</html>", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(title);
        readDataFromJsonString(jsonString);
        setPreferredSize(new Dimension(650,750));

    }

    @Override
    public void setSize(int x, int y) {
        super.setSize(x, y);
        if (x > y) {
            this.radius = (y / 2) - this.padding;
        } else {
            this.radius = (x / 2) - this.padding;
        }
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        if (d.width > d.height) {
            this.radius = (d.height / 2) - this.padding;
        } else {
            this.radius = (d.width / 2) - this.padding;
        }
    }

    public void setStartFilter(int startFilter) {
        // Value between 0-6
        this.startFilter = startFilter;
        this.updateUI();
//        this.repaint();
    }

    public void setEndFilter(int endFilter) {
        // Inclusive the end date. Should be a value between 1-6
        this.endFilter = endFilter;
        this.updateUI();
//        this.repaint();
    }

    private void readDataFromJsonString(String jsonString) {
        long startTime = System.nanoTime();
        // Takes a json string and turns it into a hashmap
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(jsonString, Map.class);

        // Has the user's email as the key
        // Has a map as value with keys "Name", "Id", "Department, "DeptId"
        Map<String, Map<String, Object>> userData = (Map<String, Map<String, Object>>) data.get("emailNameIdMap");

        float angleSeparation = (float) (Math.PI * 2.0) / (float) userData.size();
        for (String userEmail: userData.keySet()) {
            // Process the raw string input into the correct types and add the angle that user should be placed at.
            String name = (String) userData.get(userEmail).get("Name");
            int id;
            try {
                id = (int) Double.parseDouble((String) userData.get(userEmail).get("Id"));
            } catch (ClassCastException e) {
                id = ((Double) userData.get(userEmail).get("Id")).intValue();
            }
            String department = (String) userData.get(userEmail).get("Department");
            int deptId = ((Double) userData.get(userEmail).get("DeptId")).intValue();
            float angle = (float) id * angleSeparation;

            // Creates the new mapping
            Employee userInfo = new Employee(name, id, department, deptId, angle);

            this.emailNameIdMap.put(userEmail, userInfo);
            this.idEmailMap.put(id, userEmail);
        }

        // Process edges by first getting the raw string representation
        Map<String, Map<String, Map<String, Double>>> adjMatrix = (Map<String, Map<String, Map<String, Double>>>) data.get("edges");

        ArrayList<Double> shape = (ArrayList<Double>) data.get("edgesShape");

        // Actually give a shape for the adj matrix
        this.adjacencyMatrix = new int[shape.get(0).intValue()][shape.get(1).intValue()][shape.get(2).intValue()];

        // Parse the initial map into an array for the adjacency matrix
        for (String i: adjMatrix.keySet()) {
            int i_int = Integer.parseInt(i);
            for (String j: adjMatrix.get(i).keySet()) {
                int j_int = Integer.parseInt(j);
                int personSum = 0;
                for (String k: adjMatrix.get(i).get(j).keySet()) {
                    int k_int = Integer.parseInt(k);
                    int val = adjMatrix.get(i).get(j).get(k).intValue();
                    this.adjacencyMatrix[i_int][j_int][k_int] = val;
                    personSum += val;
                }
                if (personSum > maxValue) {
                    maxValue = personSum;
                }
            }
        }
        long elapsedTime = System.nanoTime();
        System.out.println("DEBUG: Time taken to read JSON (ms):" + ((elapsedTime - startTime) / 1000000));
    }

    private int[] polarToCartesian(float r, float theta) {
        // Converts polar coordinates to cartesian centered at the center of the panel
        int[] origin = new int[]{this.getSize().width / 2, this.getSize().height / 2 - 20};
        int[] result = new int[2];
        result[0] = (int) (r * Math.cos(theta)) + origin[0];
        result[1] = (int) (r * Math.sin(theta)) + origin[1];
        return result;
    }

    private int[] labelLocator(String text, Float angle, Graphics2D g2d) {
        int labelPadding = 8;
        int[] position;
        if (angle > Math.PI / 2 && angle < 3 * Math.PI / 2) {
            // If the it's on the left side, we have to flip it and move it over
            Rectangle2D stringBounds = g2d.getFont().getStringBounds(text, g2d.getFontRenderContext());
            position = polarToCartesian((float) (this.radius + stringBounds.getWidth() + labelPadding), angle);
        } else {
            position = polarToCartesian(this.radius + labelPadding, angle);
        }
        return position;
    }

    private void drawCircleFromCenter(int centerX, int centerY, int r, Color color, Graphics2D g2d) {
        // Draws a circle from a given center with radius r
        g2d.setColor(color);
        g2d.fillOval(centerX - r, centerY - r, r * 2, r * 2);
    }

    private float[][] sumConnections() {
        // Sums connections between a start and end date. Assumes that start and end are within the right boundary
        // otherwise silently clips it.
        int start = Math.max(this.startFilter, 0);
        int end = Math.min(this.endFilter, adjacencyMatrix[0][0].length);
        // Ensure start is less than end otherwise draw nothing.
        if (start >= end) {
            start = 0;
            end = 0;
        }

        // Sum connections
        float[][] connections = new float[adjacencyMatrix.length][adjacencyMatrix[0].length];
        ArrayList<Float> counts = new ArrayList<>();
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
                float numConnections = 0;
                if (i != j) {
                    for (int k = start; k <= end; k++) {
                        // End is inclusive
                        // Count number of connections between two people
                        if (adjacencyMatrix[i][j][k] > 0) {
                            numConnections += adjacencyMatrix[i][j][k];
                        }
                    }
                }
                connections[i][j] = numConnections;
                counts.add(numConnections);
            }
        }
        return connections;
    }

    private void drawArcs(float[][] connections, Graphics2D g2d) {
        // Draws the arcs between each node according to the given adjacency graph
        for (int i = 0; i < connections.length; i++) {
            for (int j = 0; j < connections[i].length; j++) {
                float value = connections[i][j];
                if (value > 0) {
                    // Get sender info
                    Employee senderInfo = emailNameIdMap.get(idEmailMap.get(i));
                    float senderAngle = senderInfo.getAngle();
                    int senderDept = senderInfo.getDeptId();

                    // Get recipient info
                    Employee recipientInfo = emailNameIdMap.get(idEmailMap.get(j));
                    float recipientAngle = recipientInfo.getAngle();
                    int recipientDept = recipientInfo.getDeptId();

                    // Figure out control point radius
                    int ctrlRadius = senderDept == recipientDept ? this.radius / 3 * 2 : this.radius / 3;

                    // Set up curve coordinates
                    int[] source = polarToCartesian(this.radius, senderAngle);
                    int[] sourceCtrl = polarToCartesian(ctrlRadius, senderAngle);

                    int[] dest = polarToCartesian(this.radius, recipientAngle);
                    int[] destCtrl = polarToCartesian(ctrlRadius, recipientAngle + (float) (2 * Math.PI));
                    CubicCurve2D curve = new CubicCurve2D.Float(
                            source[0], source[1],
                            sourceCtrl[0], sourceCtrl[1],
                            destCtrl[0], destCtrl[1],
                            dest[0], dest[1]
                    );

                    // Connection strength is is between 0 and 1
                    float strength = value / maxValue;
                    int opacity = Math.max(Math.round(strength * 200), 20);

                    float thickness = Math.max((strength * 6), 0.5f);
                    g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(94, 207, 255, opacity));

                    g2d.draw(curve);
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2d) {
        // Place nodes around the frame
        for (String email:this.emailNameIdMap.keySet()) {
            // Add the node
            Employee data = this.emailNameIdMap.get(email);
            int[] center = polarToCartesian(this.radius, data.getAngle());
            drawCircleFromCenter(
                    center[0], center[1], 4,
                    EmailColors.deptColors[data.getDeptId()],
                    g2d
            );

            // Add the label
            AffineTransform origGeom = g2d.getTransform();
            int[] labelLocation = labelLocator(data.getName(), data.getAngle(), g2d);
            g2d.translate(labelLocation[0], labelLocation[1]);
            float angle;
            if (data.getAngle() > Math.PI / 2 && data.getAngle() < 3 * Math.PI / 2) {
                angle = (float) (data.getAngle() - Math.PI);
            } else {
                angle = data.getAngle();
            }
            g2d.rotate(angle);
            g2d.drawString(data.getName(), 0, 3);
            g2d.setTransform(origGeom);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        long startTime = System.nanoTime();
        Graphics2D g2d = (Graphics2D) g;

        // Make things anti-aliased
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Change font to be smaller
        Font currentFont = g2d.getFont();
        g2d.setFont(currentFont.deriveFont(9F));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, this.getSize().width, this.getSize().height);
        g2d.setColor(new Color(229, 235, 247));
        g2d.fillRect(10, 70, this.getSize().width - 20, this.getSize().height - 150);
        Stroke stroke1 = new BasicStroke(0.5f);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(stroke1);
        g2d.drawRect(10, 70, this.getSize().width - 20, this.getSize().height - 150);

        // TODO Make start and end not hardcoded
        float[][] connections = sumConnections();

        drawArcs(connections, g2d);
        drawNodes(g2d);

        long elapsedTime = System.nanoTime();
        System.out.println("Time to paint (ms):");
        System.out.println((elapsedTime - startTime) / 1000000);
    }
}
