package gsim;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.Viewer;

public class GraphSim {
	private Graph graph;

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
						"node.positive { fill-color: green;}" +
						"node.negative { fill-color: red;}" +
						"edge {\n" +
						"\tshape: line;\n" +
						"\tarrow-size: 3px, 2px;\n" +
						"}" ;

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
