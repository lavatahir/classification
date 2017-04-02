package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;

import cern.colt.Arrays;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DecisionTree extends Classification {

	private int numberOfFeatures;
	private int numberOfClasses;
	private double entropy;
	private Map<Node, List<Node>> tree;
	private Node root;
	private DelegateTree<Node, Edge> treeGUI;

	public DecisionTree(int numOfClasses, int numOfFeatures) {
		this.numberOfFeatures = numOfFeatures;
		this.numberOfClasses = numOfClasses;
		tree = new LinkedHashMap<>();
		
		treeGUI = new DelegateTree<>();

	}

	public Node getDecisionTree(State state, List<Sample> samples) {
		entropy = getEntropy(samples);

		root = getInformationGain(samples, getListOfIntegers(), 1);
		
		System.out.println("root is " + root.getNum());
		
		return root;
	}


	private List<Integer> getListOfIntegers() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < numberOfFeatures; i++) {
			list.add(new Integer(i));
		}

		return list;
	}

	private Node getMajority(List<Sample> samples) {
		int[] numberOfSamplesBelongingToClass = new int[numberOfClasses];

		for (Sample s : samples) {
			numberOfSamplesBelongingToClass[s.getClassNumber() - 1] += 1;
		}

		int maximumSampleInClasses = -1;
		int maximumClassNumber = -1;
		for (int i = 0; i < numberOfClasses; i++) {
			if (numberOfSamplesBelongingToClass[i] > maximumSampleInClasses) {
				maximumClassNumber = i + 1;
				maximumSampleInClasses = numberOfSamplesBelongingToClass[i];
			}
		}

		return new Node(-1, maximumClassNumber);
	}

	private Node getInformationGain(List<Sample> samples, List<Integer> features, int currentDepth) {
		List<Sample> samplesWithMaximumFeatureAsOne = new ArrayList<>();
		List<Sample> samplesWithMaximumFeatureAsZero = new ArrayList<>();
		double maximumInfoGain = -1;
		int maximumFeatureNumber = 0;

		if (getEntropy(samples) == 0) {
			return new Node(-1, samples.get(0).getClassNumber());
		}
		
		if (features.isEmpty() || currentDepth >= features.size()) {
			return getMajority(samples);
		}
		
		for (int i : features) {
				List<Sample> samplesWithFeatureAsOne = new ArrayList<>();
				List<Sample> samplesWithFeatureAsZero = new ArrayList<>();

				for (Sample s : samples) {
					if (s.getBinaryNumber(i) == 0)
						samplesWithFeatureAsZero.add(s);
					else
						samplesWithFeatureAsOne.add(s);
				}

				double infoGain = getEntropy(samples);

				infoGain += -1 * (1.0 * samplesWithFeatureAsOne.size() / samples.size())
						* getEntropy(samplesWithFeatureAsOne);
				infoGain += -1 * (1.0 * samplesWithFeatureAsZero.size() / samples.size())
						* getEntropy(samplesWithFeatureAsZero);


				if (infoGain > maximumInfoGain) {
					maximumInfoGain = infoGain;
					samplesWithMaximumFeatureAsOne = new ArrayList<>(samplesWithFeatureAsOne);
					samplesWithMaximumFeatureAsZero = new ArrayList<>(samplesWithFeatureAsZero);
					maximumFeatureNumber = i;
				}
		}

		features.remove(features.indexOf(maximumFeatureNumber));
		Node node = new Node(maximumFeatureNumber + 1);
		
		if (samplesWithMaximumFeatureAsZero.isEmpty()) {
			Node newNode = getMajority(samples);
			newNode.setPathNumber(1);
			newNode.setParent(node);
			node.addChildren(newNode);
		} else {
			Node nodeWithPathZero = getInformationGain(samplesWithMaximumFeatureAsZero, new ArrayList<>(features), currentDepth + 1);
			nodeWithPathZero.setPathNumber(1);
			nodeWithPathZero.setParent(node);
			node.addChildren(nodeWithPathZero);
		}
		
		if (samplesWithMaximumFeatureAsOne.isEmpty()) {
			Node newNode = getMajority(samples);
			newNode.setPathNumber(2);
			newNode.setParent(node);
			node.addChildren(newNode);
		} else {
			Node nodeWithPathOne = getInformationGain(samplesWithMaximumFeatureAsOne, new ArrayList<>(features), currentDepth + 1);
			nodeWithPathOne.setPathNumber(2);
			nodeWithPathOne.setParent(node);
			node.addChildren(nodeWithPathOne);
		}
		
		return node;
	}

	private double getEntropy(List<Sample> samples) {
		int[] numberOfSamplesBelongingToClass = new int[numberOfClasses];
		double entropy = 0;
		
		if(samples.isEmpty())
			return entropy;
		for (Sample s : samples) {
			numberOfSamplesBelongingToClass[s.getClassNumber() - 1] += 1;
		}

		for (int i = 0; i < numberOfSamplesBelongingToClass.length; i++) {
			double probability = 1.0 * numberOfSamplesBelongingToClass[i] / samples.size();
			if (probability == 0)
				entropy += 0;
			else
				entropy += -1 * probability * (Math.log(probability) / Math.log(2));
		}

		return entropy;
	}
	
	private int getClassNumber(Sample sample) {
		
		Node node = root;
		
		while(node != null && node.getDecisionClass() == null) {
			node = node.getChildNode(sample.getBinaryNumber(node.getNum() - 1));
		}
		
		int finalNumber = node.getClassNum();
		
		return finalNumber;
	}

	protected double getProbabilityOfWGivenX(Sample sample, State state) {
		// TODO Auto-generated method stub
		return getClassNumber(sample);
	}
	
	@Override
	public void trainSamples(BufferedWriter bw, List<Sample> trainingSamples, State state) {
		getDecisionTree(state, trainingSamples);
		root.print(bw);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
