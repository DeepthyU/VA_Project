package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * UI Class that contains all the UI functions
 *
 */
public class ControlUI extends JFrame implements ActionListener, MouseWheelListener, MouseMotionListener {


    // Logic Variables
    private TabbedVisMainLogic tabbedVisMainLogic;
    private GraphUIProperty gUIProp;

    // UI Components

    private int frm_width, frm_height;
    private int ctrl_width, ctrl_height;

    private JFrame jfrm;

    private JLabel lbl_name;

    private JPanel ctrl_panel;


    /**
     * Constructor, inits and starts UI
     */
    public ControlUI() {
        //sets the values in the fields dealing with the screen
        init();
        //Creates the GUI
        startUI();
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

    }

    /**
     * initializes height, width variables, etc.
     * and creates main logic object for use by UI
     */
    private void init() {

        // Init GUI Settings
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
        frm_width = screen_dim.width;
        frm_height = screen_dim.height;

        ctrl_width = (int) (0.2 * (double) screen_dim.width);
        //ctrl_height = screen_dim.height;
        ctrl_height = (int) (0.2 * (double) screen_dim.height);
        //System.out.println(screen_dim.height);

        // Initialize graph UI property object to pass to sim
        gUIProp = new GraphUIProperty();
        gUIProp.height = screen_dim.height - 100;
        gUIProp.width = screen_dim.width - ctrl_width;
        gUIProp.posx = 0;
        gUIProp.posy = 0;

        tabbedVisMainLogic = new TabbedVisMainLogic();
    }

    /**
     * Adds objects to UI, prepares UI window
     */
    private void startUI() {

        jfrm = new JFrame();
        jfrm.setBounds(0, 0, frm_width, frm_height);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.getContentPane().setLayout(null);

        //This is the code that places the the new pannel
        //This may be where I need to shift everything to make it straight
        ctrl_panel = new JPanel();
        ctrl_panel.setBounds(frm_width - ctrl_width, 0, ctrl_width, ctrl_height);
        //ctrl_panel.setBounds(0, 0, ctrl_width, frm_height);
        //jfrm.getContentPane().add(ctrl_panel);
        ctrl_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        try {
            ControlUI.class.getResourceAsStream("./src/main/java/main/images/graph.png");
            BufferedImage myPicture = ImageIO.read(new File("./src/main/java/main/images/graph.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            picLabel.setPreferredSize(new Dimension(100, 100));
            ctrl_panel.add(picLabel);
            jfrm.setIconImage(myPicture);
        } catch (Exception e) {
            System.out.println("ERROR: Icon load failed");
        }
        jfrm.setTitle("Kronos Visualisation Tool");

        lbl_name = new JLabel("Kronos Visualisation Tool");
        lbl_name.setFont(new Font("Century", Font.BOLD, 16));
        ctrl_panel.add(lbl_name);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Article Tab", tabbedVisMainLogic.getArticlePanel());
        tabbedPane.addTab("Employee Tab", tabbedVisMainLogic.getEmployeePanel());

        tabbedPane.setSize(frm_width, frm_height );
        tabbedPane.setLocation(gUIProp.posx, gUIProp.posy);

        tabbedPane.setVisible(true);

        jfrm.getContentPane().add(tabbedPane);


        jfrm.setVisible(true);

    }

    /**
     * Action listener for click of buttons
     * This
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();


    }

    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
