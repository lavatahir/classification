package src;

public class GraphicNode {

	private int nodeID;
	private GraphicNode parent;

	public GraphicNode(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		GraphicNode node = (GraphicNode) obj;

		return this.nodeID == node.nodeID;
	}

	public GraphicNode getParent() {
		return parent;
	}

	public void setParent(GraphicNode parent) {
		this.parent = parent;
	}

	public String toString() {
		return "N" + nodeID;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + Integer.hashCode(nodeID);
		return hash;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
