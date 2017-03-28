package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrainingAndTestingClassification {
	private Set<State> classes;
	private int totalDataNum;
	private int numOfCrossValidation;
	private int numOfFeatures;
	protected double[][] confusionMatrix;
	protected static double[] accuracy;

	public TrainingAndTestingClassification(Set<State> classes, int totalDataNum, int numOfCrossValidation,
			int numOfFeatures) {
		this.classes = classes;
		this.totalDataNum = totalDataNum;
		this.numOfCrossValidation = numOfCrossValidation;
		this.numOfFeatures = numOfFeatures;
		accuracy = new double[MainClassification.numOFCrossValidationFold];
	}

	public void performTrainingAndTesting(BufferedWriter bw, String classificationType) throws IOException {
			Classification classification = getClassificationType(classificationType);

		int division = totalDataNum / numOfCrossValidation;

		for (int i = 1; i <= numOfCrossValidation; i++) {
			confusionMatrix = new double[MainClassification.numOfClasses][MainClassification.numOfClasses];

			for (State s : classes) {
				ArrayList<Sample> trainingSamples = new ArrayList<>(s.getSamples());
				List<Sample> testingSamples = new ArrayList<>(
						trainingSamples.subList((i - 1) * division, i * division));
				trainingSamples.subList((i - 1) * division, i * division).clear();

				classification.classify(bw, trainingSamples, testingSamples, s, i - 1);
				populateConfusionMatrix(testingSamples, s, classification);
			}

			printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			assignAccuracy(i - 1, division * classes.size());
			
			classification = getClassificationType(classificationType);

		}

	    printAccuracy(bw);
	}

	private Classification getClassificationType(String classificationType) {
		if (classificationType.equals(MainClassification.bayesianClassificationType))
			return new BayesianClassification(classes.size(), numOfFeatures);

		return new DependenceTree(classes.size(), numOfFeatures);
	}

	public void printConfusionMatrix(BufferedWriter bw, String message) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0");

		bw.write("\n\n*****************************" + message + "***********************\n");
		for (int i = 1; i <= MainClassification.numOfClasses; i++)
			bw.write("  W" + i + "  | ");

		bw.write("\n");
		for (int i = 0; i < MainClassification.numOfClasses; i++) {
			for (int j = 0; j < MainClassification.numOfClasses; j++) {
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
	
	public void assignAccuracy(int foldNum, int totalNumOfElement) {
		for (int i = 0; i < MainClassification.numOfClasses; i++) {
			accuracy[foldNum] += confusionMatrix[i][i];
		}

		accuracy[foldNum] = accuracy[foldNum] / totalNumOfElement;
		
		System.out.println(accuracy[foldNum]);

	}

	
	protected void populateConfusionMatrix(List<Sample> testingSamples, State state, Classification classification) {
		for (Sample sample : testingSamples) {
			double totalProbability = 0.0;
			int indexForRightClass = 0;
			for (int i = 0; i < MainClassification.numOfClasses; i++) {
				double sum = classification.getProbabilityOfWGivenX(sample, MainClassification.classes.get(i));

				if (i == 0 || sum > totalProbability) {
					totalProbability = sum;
					indexForRightClass = i;
				}
			}
			
			confusionMatrix[state.getNum() - 1][indexForRightClass] += 1;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
