package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;


public class DependenceTree {

	private int numOfFeatures;
	private List<Node> nodes;
	private List<Edge> edges;
	private Node root;

	public DependenceTree(int numOfClasses, int numOfFeatures, List<Sample> samples) {
		this.numOfFeatures = numOfFeatures;
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		createNodes();
		createCompleteConnectedGraph();
		getMaximumSpanningTree(samples);
	}

	public void getMaximumSpanningTree(List<Sample> samples) {
		for (Edge edge : edges) {
			edge.setWeight(getEdgeWeight(edge, samples));

		}

		List<Edge> newEdges = new ArrayList<>();
		List<Edge> neighBoringEdges = new ArrayList<>(edges);
		Set<Node> visitedNodes = new LinkedHashSet<>();


		while (visitedNodes.size() < nodes.size()) {
			Collections.sort(neighBoringEdges, (o1, o2) -> Double.compare(o2.getWeight(), o1.getWeight()));
			newEdges.add(neighBoringEdges.get(0));
			edges.remove(neighBoringEdges.get(0));
			visitedNodes = addNodeToVisitedNodes(neighBoringEdges.get(0), visitedNodes);
			neighBoringEdges = getNeighBoringEdge(neighBoringEdges.get(0), newEdges, visitedNodes);
		}
		
		buildTree(newEdges);		
	}
	
	public Set<Node> getDependencyTreeNodes () {
		return new LinkedHashSet<>(nodes);
	}

	private Set<Node> addNodeToVisitedNodes(Edge edge, Set<Node> visitedNodes) {
		if (!visitedNodes.contains(edge.getNode1()))
			visitedNodes.add(edge.getNode1());
		if (!visitedNodes.contains(edge.getNode2()))
			visitedNodes.add(edge.getNode2());

		return visitedNodes;
	}

	private void buildTree(List<Edge> newEdges) {
		edges = new ArrayList<>(newEdges);
		nodes = new ArrayList<>();
		Stack<Node> newNodes = new Stack<>();
		List<Edge> visitedEdges = new ArrayList<>();

		if (edges.size() > 0) {
			root = edges.get(0).getNode1();
			nodes.add(root);
			newNodes.push(root);

			Node newNode = new Node(edges.get(0).getNode2().getNum());
			newNode.setParent(root);
			newNodes.push(newNode);
			nodes.get(0).addChildren(newNode);
			nodes.add(newNode);

			visitedEdges.add(edges.get(0));
		}

		while (!newNodes.isEmpty()) {
			Node currentNode = newNodes.pop();
			for (Edge edge : edges) {
				boolean found = false;
				Node newNode = null;

				if (edge.getNode1().getNum() == currentNode.getNum() && !visitedEdges.contains(edge)) {
					newNode = new Node(edge.getNode2().getNum());
					found = true;
				} else if (edge.getNode2().getNum() == currentNode.getNum() && !visitedEdges.contains(edge)) {
					newNode = new Node(edge.getNode1().getNum());
					found = true;
				}

				if (found) {
					newNode.setParent(currentNode);
					nodes.get(nodes.indexOf(currentNode)).addChildren(newNode);
					nodes.add(newNode);
					newNodes.push(newNode);
					visitedEdges.add(edge);
				}
			}
		}

	}

	private List<Edge> getNeighBoringEdge(Edge e, List<Edge> currentEdges,
			Set<Node> visitedNodes) {
		List<Edge> graphicEdges = new ArrayList<>();

		for (Edge edge : edges) {
			if ((!currentEdges.contains(edge) && !isEdgeFormingACycle(visitedNodes, edge)
					&& isEdgeFormingADirectedGraph(visitedNodes, edge)))
				graphicEdges.add(edge);
		}

		return graphicEdges;
	}

	private boolean isEdgeFormingACycle(Set<Node> visitedNodes, Edge edge) {
		if (visitedNodes.contains(edge.getNode1()) && visitedNodes.contains(edge.getNode2()))
			return true;
		return false;
	}

	private boolean isEdgeFormingADirectedGraph(Set<Node> visitedNodes, Edge edge) {
		if (visitedNodes.contains(edge.getNode1()) || visitedNodes.contains(edge.getNode2()))
			return true;
		return false;
	}

	private double getEdgeWeight(Edge edge, List<Sample> samples) {
		double weight = 0.0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				weight += getWeight(edge, i, j, samples);
			}
		}

		return weight;
	}

	private double getWeight(Edge edge, int i, int j, List<Sample> samples) {
		double probIJ = 0.0;
		double probI = 0.0;
		double probJ = 0.0;
		
		for (Sample sample : samples) {
			int indexForFeatureI = edge.getNode1().getNum() - 1;
			int indexForFeatureJ = edge.getNode2().getNum() - 1;

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
			Node n = new Node(i + 1);
			nodes.add(n);
		}
	}

	private void createCompleteConnectedGraph() {
		int count = 1;
		for (int i = 0; i < numOfFeatures; i++) {
			for (int j = i + 1; j < numOfFeatures; j++) {
				Edge edge = new Edge(count, nodes.get(i), nodes.get(j));
				edges.add(edge);
				count++;
			}
		}
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
}
