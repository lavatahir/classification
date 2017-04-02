package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {

	private int num;
	private List<Node> children;
	private Node parent;
	private String decisionClass;
	private int pathNumber;
	private int classNum;

	public Node(int num, int classNum) {
		this.decisionClass = "W" +classNum;
		children = new ArrayList<>();
		this.classNum = classNum;
	}

	public Node(int num) {
		this.num = num;
		children = new ArrayList<>();
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Node getParent() {
		return parent;
	}

	public void addChildren(Node node) {
		this.children.add(node);
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public void print(BufferedWriter bw) {
		print("", true, bw);
	}

	private void print(String prefix, boolean isTail, BufferedWriter bw) {
		try {
			bw.write("\n" + prefix + (isTail ? "|____ " : "|---- ") + toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "     " : "|     "), false, bw);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isTail ? "     " : "|     "), true, bw);
		}
	}

	public void printDecisionTree () {
		if (num == -1) {
			System.out.println("Node is " + decisionClass + "and my parent is " + parent.getNum() + " and my pathNumber is " + pathNumber);
		} else {
			if (parent == null) {
				System.out.println("Node is " + num + " and my parent is null and my pathNumber is " + pathNumber);

			} else
				System.out.println("Node is " + num + " and my parent is " + parent.num + " and my pathNumber is " + pathNumber);

			for (Node n : children) {
				n.printDecisionTree();
			}
		}
	}

	public int getPathNumber() {
		return pathNumber;
	}

	public void setPathNumber(int pathNumber) {
		this.pathNumber = pathNumber;
	}

	public String getDecisionClass() {
		return decisionClass;
	}

	public void setDecisionClass(String decisionClass) {
		this.decisionClass = decisionClass;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		Node node = (Node) obj;

		return this.num == node.num;
	}

	public Node getChildNode(int pathNum) {
		for (Node n : children) {
			if (pathNum == n.getPathNumber() - 1) {
				return n;
			}
		}
		
		return null;
	}
	public String toString() {
		if ((decisionClass == null || decisionClass.isEmpty())) { 
			if(pathNumber > 0) {
				return (pathNumber - 1) + " ->  N" + num;
			}
			return "N" + num;
		}
		
		return (pathNumber - 1) + " ->  " +decisionClass;
	}
	

	public int getClassNum() {
		return classNum;
	}

	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
