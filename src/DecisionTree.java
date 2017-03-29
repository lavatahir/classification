package src;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class DecisionTree extends Classification {

	private int numberOfFeatures;
	private int numberOfClasses;
	private double entropy;

	public DecisionTree(int numOfClasses, int numOfFeatures) {
		this.numberOfFeatures = numOfFeatures;
		this.numberOfClasses = numOfClasses;
	}

	public void getDecisionTree(List<Sample> samples) {
		entropy = getEntropy(samples);
		getInformationGain(samples, Integer.MIN_VALUE);
	}

	private double getInformationGain(List<Sample> samples, int excludedFeatureIndex) {
		List<Sample> samplesWithMaximumFeatureAsOne = new ArrayList<>();
		List<Sample> samplesWithMaximumFeatureAsZero = new ArrayList<>();
		double maximumInfoGain = Double.MIN_VALUE;
		int maximumFeatureNumber = 0;

		for (int i = 0; i < numberOfFeatures; i++) {
			if (i != excludedFeatureIndex) {
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
		}

		System.out.println("feature " + (maximumFeatureNumber + 1) + " has the maximum gain of " + maximumInfoGain);
		return 0;
	}

	private double getEntropy(List<Sample> samples) {
		int[] samplesInClass = new int[numberOfClasses];
		double entropy = 0;
		for (Sample s : samples) {
			samplesInClass[s.getClassNumber() - 1] += 1;
		}

		for (int i = 0; i < samplesInClass.length; i++) {
			if (samplesInClass[i] == 0)
				return 0;
			double probability = 1.0 * samplesInClass[i] / samples.size();
			entropy += -1 * probability * (Math.log(probability) / Math.log(2));
		}

		return entropy;
	}

	@Override
	public void classify(BufferedWriter bw, List<Sample> trainingSamples, List<Sample> testingSamples, State state,
			int foldNum) {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
