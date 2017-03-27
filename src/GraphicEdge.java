import java.text.DecimalFormat;

public class GraphicEdge {

	private GraphicNode node1;
	private GraphicNode node2;
	private double weight;
	private int id;

	public GraphicEdge(int id, GraphicNode node1, GraphicNode node2) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
	}

	public GraphicNode getNode1() {
		return node1;
	}

	public void setNode1(GraphicNode node1) {
		this.node1 = node1;
	}

	public GraphicNode getNode2() {
		return node2;
	}

	public void setNode2(GraphicNode node2) {
		this.node2 = node2;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat("#.####");

		return "" + df.format(weight);
	}

	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		
		GraphicEdge edge = (GraphicEdge) obj;
		
		return this.weight == edge.weight && edge.node1.equals(node1) && edge.node2.equals(node2);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
