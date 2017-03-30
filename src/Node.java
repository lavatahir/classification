package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {

	private int num;
	private List<Node> children;
	private Node parent;

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
			bw.write("\n" + prefix + (isTail ? "|____ " : "|---- ") + num);
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

	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		Node node = (Node) obj;

		return this.num == node.num;
	}
	
	public String toString() {
		return "N" + num;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
