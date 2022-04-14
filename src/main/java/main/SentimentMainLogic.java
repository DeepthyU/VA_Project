package main;

import gsim.GraphSim;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.Viewer;
import vis.SentimentVisualizer;

import java.util.List;

/**
 * Maing logic controller which the UI calls
 * @author brandon
 *
 */
public class SentimentMainLogic {


	private GraphSim gs;

	public SentimentMainLogic()
	{
		gs = new GraphSim("SentimentGraph");
		SentimentVisualizer sv = new SentimentVisualizer();
		sv.drawGraph(false, gs.getGraph());
		gs.setStyle();
	}

	public Viewer simulate_graph()
	{
		return gs.get_display();
	}

	public void applyFilters(List filters){
		SentimentVisualizer sv = new SentimentVisualizer();
		sv.drawGraph(filters, gs.getGraph());
		gs.setStyle();
	}
	public Graph getGraph()
	{
		return gs.getGraph();
	}

}
