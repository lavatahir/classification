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
	private static double[] accuracy = new double[MainClassification.numOFCrossValidationFold];

	public BayesianClassification(int numOfClasses, int numOfFeatures) {
		this.numOfClasses = numOfClasses;
		this.numOfFeatures = numOfFeatures;
		this.mean = new double[numOfClasses][numOfFeatures];
		this.variance = new double[numOfClasses][numOfFeatures];
		this.confusionMatrix = new double[numOfClasses][numOfClasses];
	}

	@Override
	public void classify(BufferedWriter bw, List<Sample> trainingSamples, List<Sample> testingSamples, State state,
			int foldNum) {
		populateMean(trainingSamples, state);
		populateVariance(trainingSamples, state);
		populateConfusionMatrix(testingSamples, state);
		try {
			print(bw, mean, state.getNum()-1, "Mean for W" + state.getNum());
			print(bw, variance, state.getNum()-1,"Variance for W" + state.getNum());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateMean(List<Sample> trainingSamples, State state) {
		for (int i = 0; i < numOfFeatures; i++) {
			double sum = 0;
			for (Sample sample : trainingSamples) {
				sum += sample.getProbability(i);
			}

			mean[state.getNum() - 1][i] = sum / trainingSamples.size();
		}
	}

	private void populateVariance(List<Sample> trainingSamples, State state) {
		for (int i = 0; i < numOfFeatures; i++) {
			double sum = 0;
			for (Sample sample : trainingSamples) {
				sum += Math.pow(sample.getProbability(i) - mean[state.getNum() - 1][i], 2);
			}

			variance[state.getNum() - 1][i] = sum / trainingSamples.size();
		}
	}

	private void populateConfusionMatrix(List<Sample> testingSamples, State state) {
		for (Sample sample : testingSamples) {
			double totalProbability = 0.0;
			int indexForRightClass = 0;
			for (int i = 0; i < numOfClasses; i++) {
				double sum = getProbabilityOfWGivenX(sample, MainClassification.classes.get(i));

				if (i == 0 || sum > totalProbability) {
					totalProbability = sum;
					indexForRightClass = i;
				}
			}
			confusionMatrix[state.getNum() - 1][indexForRightClass] += 1;

		}
	}

	private double getProbabilityOfWGivenX(Sample sample, State state) {
		double totalProbability = state.getAprioriProbability();
		for (int i = 0; i < numOfFeatures; i++) {
			double gaussianProbabilityForW = getGaussianProbabilityOfXGivenW(sample.getProbability(i),
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

	public void assignAccuracy(int foldNum, int totalNumOfElement) {
		for (int i = 0; i < numOfClasses; i++) {
			accuracy[foldNum] += confusionMatrix[i][i];
		}

		accuracy[foldNum] = accuracy[foldNum] / totalNumOfElement;
		
		System.out.println(accuracy[foldNum]);

	}

	private void print(BufferedWriter bw, double[][] array, int i, String message) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0000");

		bw.write("\n\n*****************************" + message + "***********************\n");
		MainClassification.printFeatures(bw);
			for (int j = 0; j < numOfFeatures; j++) {
				bw.write(" " + df.format(array[i][j]) + " | ");
			}
			bw.write("\n");
		
	}

	public void printConfusionMatrix(BufferedWriter bw, String message) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0");

		bw.write("\n\n*****************************" + message + "***********************\n");
		for (int i = 1; i <= numOfClasses; i++)
			bw.write("  W" + i + "  | ");

		bw.write("\n");
		for (int i = 0; i < numOfClasses; i++) {
			for (int j = 0; j < numOfClasses; j++) {
				bw.write(" " + df.format(confusionMatrix[i][j]) + " | ");
			}
			bw.write("\n");
		}
	}
	
	public void printAccuracy(BufferedWriter bw) throws IOException {
		DecimalFormat df = new DecimalFormat("0.00");

		bw.write("\n\n*****************************Accuracy Matrix***********************\n");
		double sum = 0.0;
		for (int i = 0; i < accuracy.length; i++) {
				bw.write(" " + df.format(accuracy[i]) + " | ");
			 sum += accuracy[i];
			bw.write("\n");
		}
		
		bw.write("\n\nOverall Accuracy is " + df.format((sum/accuracy.length * 100)) + "%.");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
