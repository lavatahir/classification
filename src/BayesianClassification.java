package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class BayesianClassification extends Classification {

	private double[][] mean;
	private double[][] variance;
	private double[][] confusionMatrix;
	private int numOfClasses;
	private int numOfFeatures;

	public BayesianClassification(int numOfClasses, int numOfFeatures) {
		this.numOfClasses = numOfClasses;
		this.numOfFeatures = numOfFeatures;
		this.mean = new double[numOfClasses][numOfFeatures];
		this.variance = new double[numOfClasses][numOfFeatures];
	}

	@Override
	public void trainSamples(BufferedWriter bw, List<Sample> trainingSamples,  State state,
			int foldNum) {
		populateMean(trainingSamples, state);
		populateVariance(trainingSamples, state);
		try {
			print(bw, mean, state.getNum() - 1, "Mean for W" + state.getNum());
			print(bw, variance, state.getNum() - 1, "Variance for W" + state.getNum());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateMean(List<Sample> trainingSamples, State state) {
		for (int i = 0; i < numOfFeatures; i++) {
			double sum = 0;
			for (Sample sample : trainingSamples) {
				sum += sample.getDecimalValueForFeature(i);
			}

			mean[state.getNum() - 1][i] = sum / trainingSamples.size();
		}
	}

	private void populateVariance(List<Sample> trainingSamples, State state) {
		for (int i = 0; i < numOfFeatures; i++) {
			double sum = 0;
			for (Sample sample : trainingSamples) {
				sum += Math.pow(sample.getDecimalValueForFeature(i) - mean[state.getNum() - 1][i], 2);
			}

			variance[state.getNum() - 1][i] = sum / trainingSamples.size();
		}
	}

	@Override
	protected double getProbabilityOfWGivenX(Sample sample, State state) {
		double totalProbability = state.getAprioriProbability();
		for (int i = 0; i < numOfFeatures; i++) {
			double gaussianProbabilityForW = getGaussianProbabilityOfXGivenW(sample.getDecimalValueForFeature(i),
					state.getNum() - 1, i);

			totalProbability *= gaussianProbabilityForW;
		}

		return totalProbability;
	}

	private double getGaussianProbabilityOfXGivenW(double x, int classNum, int featureNum) {
		double meanValue = mean[classNum][featureNum];
		double varianceValue = variance[classNum][featureNum];

		double innerBracketEval = Math.exp((-1 * Math.pow(x - meanValue, 2)) / (2 * varianceValue));
		double sqrEval = Math.sqrt(2 * Math.PI * varianceValue);

		return innerBracketEval / sqrEval;
	}

	private void print(BufferedWriter bw, double[][] array, int i, String message) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0000");

		bw.write("\n\n*****************************" + message + "***********************\n");
		//MainClassification.printFeatures(bw);
		for (int j = 0; j < numOfFeatures; j++) {
			bw.write(" " + df.format(array[i][j]) + " | ");
		}
		bw.write("\n");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
