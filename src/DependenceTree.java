package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DependenceTree extends Classification{

	private int numOfFeatures;
	private List<GraphicNode> nodes;
	private List<GraphicEdge> edges;
	private GraphicNode root;
	private DelegateTree<GraphicNode, GraphicEdge> tree;

	public DependenceTree(int numOfClasses, int numOfFeatures) {
		this.numOfFeatures = numOfFeatures;
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		createNodes();
		createCompleteConnectedGraph();
	}

	public Set<GraphicNode> getMaximumSpanningTree(List<Sample> samples) {
		for (GraphicEdge edge : edges) {
			edge.setWeight(getEdgeWeight(edge, samples));

		}

		List<GraphicEdge> newEdges = new ArrayList<>();
		List<GraphicEdge> neighBoringEdges = new ArrayList<>(edges);
		Set<GraphicNode> visitedNodes = new LinkedHashSet<>();


		while (visitedNodes.size() < nodes.size()) {
			Collections.sort(neighBoringEdges, (o1, o2) -> Double.compare(o2.getWeight(), o1.getWeight()));
			newEdges.add(neighBoringEdges.get(0));
			edges.remove(neighBoringEdges.get(0));
			visitedNodes = addNodeToVisitedNodes(neighBoringEdges.get(0), visitedNodes);
			neighBoringEdges = getNeighBoringEdge(neighBoringEdges.get(0), newEdges, visitedNodes);
		}
		
		buildTree(newEdges);
		
		return new LinkedHashSet<>(nodes);
	}

	private Set<GraphicNode> addNodeToVisitedNodes(GraphicEdge edge, Set<GraphicNode> visitedNodes) {
		if (!visitedNodes.contains(edge.getNode1()))
			visitedNodes.add(edge.getNode1());
		if (!visitedNodes.contains(edge.getNode2()))
			visitedNodes.add(edge.getNode2());

		return visitedNodes;
	}

	private void buildTree(List<GraphicEdge> newEdges) {
		edges = new ArrayList<>(newEdges);
		nodes = new ArrayList<>();
		tree = new DelegateTree<>();
		Stack<GraphicNode> newNodes = new Stack<>();
		List<GraphicEdge> visitedEdges = new ArrayList<>();
		Random rand = new Random();

		if (edges.size() > 0) {
			root = edges.get(0).getNode1();
			nodes.add(root);
			tree.setRoot(root);
			newNodes.push(root);

			GraphicNode newNode = new GraphicNode(edges.get(0).getNode2().getNodeID());
			newNode.setParent(root);
			newNodes.push(newNode);
			nodes.add(newNode);
			tree.addChild(edges.get(0), root, newNode);

			visitedEdges.add(edges.get(0));
		}

		while (!newNodes.isEmpty()) {
			GraphicNode currentNode = newNodes.pop();
			for (GraphicEdge edge : edges) {
				boolean found = false;
				GraphicNode newNode = null;

				if (edge.getNode1().getNodeID() == currentNode.getNodeID() && !visitedEdges.contains(edge)) {
					newNode = new GraphicNode(edge.getNode2().getNodeID());
					found = true;
				} else if (edge.getNode2().getNodeID() == currentNode.getNodeID() && !visitedEdges.contains(edge)) {
					newNode = new GraphicNode(edge.getNode1().getNodeID());
					found = true;
				}

				if (found) {
					newNode.setParent(currentNode);
					//tree.addVertex(newNode);
					tree.addChild(edge, currentNode, newNode);
					nodes.add(newNode);
					newNodes.push(newNode);
					visitedEdges.add(edge);
				}
			}
		}

	}

	private List<GraphicEdge> getNeighBoringEdge(GraphicEdge e, List<GraphicEdge> currentEdges,
			Set<GraphicNode> visitedNodes) {
		List<GraphicEdge> graphicEdges = new ArrayList<>();

		for (GraphicEdge edge : edges) {
			if ((!currentEdges.contains(edge) && !isEdgeFormingACycle(visitedNodes, edge)
					&& isEdgeFormingADirectedGraph(visitedNodes, edge)))
				graphicEdges.add(edge);
		}

		return graphicEdges;
	}

	private boolean isEdgeFormingACycle(Set<GraphicNode> visitedNodes, GraphicEdge edge) {
		if (visitedNodes.contains(edge.getNode1()) && visitedNodes.contains(edge.getNode2()))
			return true;
		return false;
	}

	private boolean isEdgeFormingADirectedGraph(Set<GraphicNode> visitedNodes, GraphicEdge edge) {
		if (visitedNodes.contains(edge.getNode1()) || visitedNodes.contains(edge.getNode2()))
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

			if (sample.getBinaryNumber(indexForFeatureI) == i
					&& sample.getBinaryNumber(indexForFeatureJ) == j)
				probIJ += 1.0;

			if (sample.getBinaryNumber(indexForFeatureI) == i)
				probI += 1;

			if (sample.getBinaryNumber(indexForFeatureJ) == j)
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
		System.out.println("Root of the graph is Node " + root.getNodeID());
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<GraphicNode, GraphicEdge> layout = new FRLayout<>(tree);
		layout.setSize(new Dimension(1100, 640)); // sets the initial size of the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<GraphicNode, GraphicEdge> vv = new BasicVisualizationServer<GraphicNode, GraphicEdge>(layout);
		vv.setPreferredSize(new Dimension(1100, 640)); // Sets the viewing area
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
		

		vv.addKeyListener(gm.getModeKeyListener());

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JFrame frame = new JFrame("Simple Graph View 2");
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	public GraphicNode getRoot() {
		return root;
	}

	public void setRoot(GraphicNode root) {
		this.root = root;
	}

	@Override
	public void classify(BufferedWriter bw, List<Sample> trainingSamples, List<Sample> testingSamples, State state,
			int foldNum) {
		
	}

	@Override
	protected double getProbabilityOfWGivenX(Sample sample, State state) {	
		double probability = 1.0;

		for(int i=0; i<numOfFeatures; i++) {
			int dependentIndex = state.getDependenceFeature(i).getDependentFeatureIndex();
			
			int num = -1;
			if (dependentIndex != -1)
				num = sample.getBinaryNumber(dependentIndex);
			probability *= state.getDependenceTreeProbabilityOfXGivenW(i, sample.getBinaryNumber(i), num);
		}
		
		return probability;
	}
}
