package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DependenceTree {

	private int numOfFeatures;
	private List<GraphicNode> nodes;
	private List<GraphicEdge> edges;
	private Graph<GraphicNode, GraphicEdge> tree;

	public DependenceTree(int numOfFeatures) {
		this.numOfFeatures = numOfFeatures;
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		createNodes();
		createCompleteConnectedGraph();
	}

	public void getMaximumSpanningTree(List<Sample> samples) {
		for (GraphicEdge edge : edges) {
			edge.setWeight(getEdgeWeight(edge, samples));

		}

		List<GraphicEdge> newEdges = new ArrayList<>();
		List<GraphicEdge> neighBoringEdges = new ArrayList<>(edges);
		Set<GraphicNode> visitedNodes = new LinkedHashSet<>();

		while (newEdges.size() < nodes.size() - 1) {
			Collections.sort(neighBoringEdges, (o1, o2) -> Double.compare(o2.getWeight(), o1.getWeight()));
			System.out.println(neighBoringEdges.size());
			newEdges.add(neighBoringEdges.get(0));
			visitedNodes = addNodeToVisitedNodes(neighBoringEdges.get(0), visitedNodes);
			neighBoringEdges = getNeighBoringEdge(neighBoringEdges.get(0), newEdges, visitedNodes);
		}
		
		buildTree(newEdges);
	}
	
	private Set<GraphicNode> addNodeToVisitedNodes(GraphicEdge edge, Set<GraphicNode> visitedNodes) {
		if(!visitedNodes.contains(edge.getNode1()))
			visitedNodes.add(edge.getNode1());
		if(!visitedNodes.contains(edge.getNode2()))
			visitedNodes.add(edge.getNode2());
		
		return visitedNodes;
	}
	private void buildTree(List<GraphicEdge> newEdges) {
		edges = new ArrayList<>(newEdges);
		
		tree = new SparseMultigraph<>();
		for (GraphicNode n : nodes) {
			tree.addVertex(n);
		}	
		
		for (GraphicEdge edge : edges) {
			tree.addEdge(edge, edge.getNode1(), edge.getNode2());
		}
	}

	private List<GraphicEdge> getNeighBoringEdge(GraphicEdge e, List<GraphicEdge> currentEdges, Set<GraphicNode> visitedNodes) {
		List<GraphicEdge> graphicEdges = new ArrayList<>();

		for (GraphicEdge edge : edges) {
			if ((!currentEdges.contains(edge) && !isEdgeFormingACycle(visitedNodes, edge)))
				graphicEdges.add(edge);
		}

		return graphicEdges;
	}
	
	private boolean isEdgeFormingACycle(Set<GraphicNode> visitedNodes, GraphicEdge edge) {
		if(visitedNodes.contains(edge.getNode1()) && visitedNodes.contains(edge.getNode2()))
			return true;
		return false;
	}


	private double getEdgeWeight(GraphicEdge edge, List<Sample> samples) {
		double weight = 0.0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				weight += getWeight(edge, i, j, samples);
			}
		}

		return weight;
	}

	private double getWeight(GraphicEdge edge, int i, int j, List<Sample> samples) {
		double probIJ = 0.0;
		double probI = 0.0;
		double probJ = 0.0;

		for (Sample sample : samples) {
			int indexForFeatureI = edge.getNode1().getNodeID() - 1;
			int indexForFeatureJ = edge.getNode2().getNodeID() - 1;

			if (sample.getProbabilityForBinaryFeature(indexForFeatureI) == i
					&& sample.getProbabilityForBinaryFeature(indexForFeatureJ) == j)
				probIJ += 1.0;

			if (sample.getProbabilityForBinaryFeature(indexForFeatureI) == i)
				probI += 1;

			if (sample.getProbabilityForBinaryFeature(indexForFeatureJ) == j)
				probJ += 1;

		}

		probIJ = probIJ / samples.size();
		probI /= samples.size();
		probJ /= samples.size();

		if (probI == 0 || probJ == 0 || probIJ == 0)
			return 0;

		return probIJ * Math.log(probIJ / (probI * probJ));
	}

	private void createNodes() {
		for (int i = 0; i < numOfFeatures; i++) {
			GraphicNode n = new GraphicNode(i + 1);
			nodes.add(n);
		}
	}

	private void createCompleteConnectedGraph() {
		int count = 1;
		for (int i = 0; i < numOfFeatures; i++) {
			for (int j = i + 1; j < numOfFeatures; j++) {
				GraphicEdge edge = new GraphicEdge(count, nodes.get(i), nodes.get(j));
				edges.add(edge);
				count++;
			}
		}
	}

	public void drawInitialGraph() {
		System.out.println("The graph g = " + tree.toString());
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<GraphicNode, GraphicEdge> layout = new CircleLayout(tree);
		layout.setSize(new Dimension(300, 300)); // sets the initial size of the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		VisualizationViewer<GraphicNode, GraphicEdge> vv = new VisualizationViewer<GraphicNode, GraphicEdge>(layout);
		vv.setPreferredSize(new Dimension(350, 350)); // Sets the viewing area
														// size

		Transformer<GraphicNode, Paint> vertexPaint = new Transformer<GraphicNode, Paint>() {
			@Override
			public Paint transform(GraphicNode arg0) {
				// TODO Auto-generated method stub
				return Color.GREEN;
			}
		};
		// Set up a new stroke Transformer for the edges
		float dash[] = { 10.0f };
		final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
				0.0f);
		Transformer<GraphicEdge, Stroke> edgeStrokeTransformer = new Transformer<GraphicEdge, Stroke>() {

			@Override
			public Stroke transform(GraphicEdge arg0) {
				// TODO Auto-generated method stub
				return edgeStroke;
			}
		};

		// Create a graph mouse and add it to the visualization component
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(Mode.TRANSFORMING);
		vv.setGraphMouse(gm);

		vv.addKeyListener(gm.getModeKeyListener());

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JFrame frame = new JFrame("Simple Graph View 2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DependenceTree dependenceTree = new DependenceTree(4);
		dependenceTree.drawInitialGraph();
	}

}
