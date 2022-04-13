package gsim;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.Viewer;

/**
 * The Graph Simulator object. This should be the object that the caller
 * should interface with.
 * 
 * To use this class:
 * 1) create an object
 * 2) (optional) importGraph - import a graph 
 * 3) (optional) display - display graph
 * 4) (optional) compute - Starts worker thread to simulate graph animations
 * 5) (optional) importEvents/exportEvents - import/export events for worker
 * thread
 * 
 * 
 * 
 * *) Run actions
 * - addVertex
 * - addEdge
 * - ppr_teleport
 * - ppr_go
 * - etc.
 * 
 * @author brandon
 *
 */
public class GraphSim { 
	private Graph graph;
	private GraphSimAlgorithm galgo;


	/**
	 * Creates a new Graph Simulator Object
	 * @param graph_name
	 */
	public GraphSim(String graph_name) {
		this.graph = new SingleGraph(graph_name);
	}


	/**
	 * display() displays the graph by rendering the UI with the graph from the
	 * graphstream library
	 */
	public void display() {
		Viewer vwr = graph.display();

		// Set it so program doesnt abort when close window 
		vwr.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

	}

	public Viewer get_display()
	{	
		Viewer viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.disableAutoLayout();
		return viewer;
	}

	/**
	 * Initializes the simulation
	 */
	public void setStyle() {
//		// Set Graph Style
		String style =
				"graph {\n" +
						" fill-color: white;" +
						" fill-mode: plain;"+
						"}"+
						"node {	size: 5px, 5px; fill-color: black; } " +
						"node.marked { fill-color: red;}" +
						"node.positive { fill-color: blue;}" +
						"node.negative { fill-color: red;}" +
						"edge {\n" +
						"\tshape: line;\n" +
						"\tarrow-size: 3px, 2px;\n" +
						"}" +
						"edge.marked {\n" +
						"\tshape: line;\n" +
						"\tfill-color: red;\n" +
						"\tarrow-size: 3px, 2px;\n" +
						"}";

		graph.setAttribute("ui.stylesheet", style);

	}


	/**
	 * Get the graph object of the current graph 
	 * @return graph object of the current graph 
	 */
	public Graph getGraph()
	{
		return graph;
	}

}
