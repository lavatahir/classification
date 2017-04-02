package src;

import java.text.DecimalFormat;

public class Edge {

	private Node node1;
	private Node node2;
	private double weight;
	private int id;

	public Edge(int id, Node node1, Node node2) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
	}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String toString() {
		return "" + (int)weight;
	}

	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		
		Edge edge = (Edge) obj;
		
		return this.weight == edge.weight && edge.node1.equals(node1) && edge.node2.equals(node2);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
