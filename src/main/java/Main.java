import javafx.scene.canvas.GraphicsContext;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.util.Iterator;

public class Main {
    public static void main(String args[]) {
        new Main();
    }

    public Main() {
        Graph graph = new SingleGraph("tutorial 1");

        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        System.setProperty("org.graphstream.ui", "javafx");
        Viewer viewer = graph.display(true);
        FxDefaultView view = (FxDefaultView) viewer.getDefaultView();

        graph.addEdge("AB", "A", "B");
        graph.addEdge("AC", "A", "C");
        graph.addEdge("CA", "C", "A");
        graph.addEdge("AD", "A", "D");
        graph.addEdge("AI", "A", "I");
        graph.addEdge("AJ", "A", "J");
        graph.addEdge("AK", "A", "K");
        graph.addEdge("AL", "A", "L");
        graph.addEdge("AM", "A", "M");
        graph.addEdge("DE", "D", "E");
        graph.addEdge("DF", "D", "F");

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }

        explore(graph.getNode("A"));
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {e.printStackTrace();}
    }

    protected String styleSheet =
            "node {	fill-color: black; } node.marked { fill-color: red;}";

}