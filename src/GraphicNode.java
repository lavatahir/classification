package src;


public class GraphicNode {

	private int nodeID;
	
	public GraphicNode (int nodeID) {
		this.nodeID = nodeID;
	}
	
	
	public int getNodeID() {
		return nodeID;
	}


	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		
		GraphicNode node = (GraphicNode) obj;
		
		return this.nodeID == node.nodeID;
	}

	public String toString() {
		return "N" + nodeID;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(nodeID);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
