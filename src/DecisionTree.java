package src;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecisionTree extends Classification {

	private int numberOfFeatures;
	private int numberOfClasses;
	private double entropy;
	Map<Node, List<Node>> tree;

	public DecisionTree(int numOfClasses, int numOfFeatures) {
		this.numberOfFeatures = numOfFeatures;
		this.numberOfClasses = numOfClasses;
		tree = new LinkedHashMap<>();
	}

	public void getDecisionTree(State state, List<Sample> samples) {
		entropy = getEntropy(samples);

		getInformationGain(samples, getListOfIntegers(), 1);
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

		return new Node("W" + maximumClassNumber);
	}

	private Node getInformationGain(List<Sample> samples, List<Integer> features, int currentDepth) {
		List<Sample> samplesWithMaximumFeatureAsOne = new ArrayList<>();
		List<Sample> samplesWithMaximumFeatureAsZero = new ArrayList<>();
		double maximumInfoGain = Double.MIN_VALUE;
		int maximumFeatureNumber = 0;

		if (getEntropy(samples) == 0) {
			return new Node("W" + samples.get(0).getClassNumber());
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

				System.out.println("info gain is " + infoGain);
				if (infoGain > maximumInfoGain) {
					maximumInfoGain = infoGain;
					samplesWithMaximumFeatureAsOne = new ArrayList<>(samplesWithFeatureAsOne);
					samplesWithMaximumFeatureAsZero = new ArrayList<>(samplesWithFeatureAsZero);
					maximumFeatureNumber = i;
				}
		}
		
		features.remove(maximumFeatureNumber);
		
		Node node = new Node(maximumFeatureNumber);
		
		if (samplesWithMaximumFeatureAsZero.isEmpty()) {
			node.addChildren(getMajority(samples));
		} else {
			Node nodeWithPathZero = getInformationGain(samples, features, currentDepth + 1);
			//nodeWithPathZero.
		}
		System.out.println("feature " + (maximumFeatureNumber + 1) + " has the maximum gain of " + maximumInfoGain);
		return null;
	}

	private boolean isLeafNode() {
		return false;
	}

	private double getEntropy(List<Sample> samples) {
		int[] numberOfSamplesBelongingToClass = new int[numberOfClasses];
		double entropy = 0;
		for (Sample s : samples) {
			numberOfSamplesBelongingToClass[s.getClassNumber() - 1] += 1;
		}

		for (int i = 0; i < numberOfSamplesBelongingToClass.length; i++) {
			if (numberOfSamplesBelongingToClass[i] == 0)
				return 0;
			double probability = 1.0 * numberOfSamplesBelongingToClass[i] / samples.size();
			entropy += -1 * probability * (Math.log(probability) / Math.log(2));
		}

		return entropy;
	}

	@Override
	public void trainSamples(BufferedWriter bw, List<Sample> trainingSamples, State state, int foldNum) {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
