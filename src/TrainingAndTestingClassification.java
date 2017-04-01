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
	private DependenceTree dependenceTree;

	public TrainingAndTestingClassification(Set<State> classes,
			int totalDataNum, int numOfCrossValidation, int numOfFeatures) {
		this.classes = classes;
		this.totalDataNum = totalDataNum;
		this.numOfCrossValidation = numOfCrossValidation;
		this.numOfFeatures = numOfFeatures;
		accuracy = new double[numOfCrossValidation];
	}

	public List<Sample> getAllSamples() {
		List<Sample> samples = new ArrayList<>();
		for (State state : classes) {
			samples.addAll(state.getSamples());
		}

		return samples;
	}

	public void performTrainingAndTesting(BufferedWriter bw,
			String classificationType) throws IOException {

		if (classificationType
				.equals(ClassificationUI.dependentTreeClassificationType)) {
			this.dependenceTree = new DependenceTree(classes.size(), numOfFeatures, getAllSamples());
			this.dependenceTree.getRoot().print(bw);
			bw.write("\n\n");
			for(Node node : dependenceTree.getDependencyTreeNodes()) {
				bw.write("\nnode " + node.getNum() + " has children: " + node.getChildren());
			}
			bw.write("\n\n");
		}
		
		Classification classification = getClassificationType(classificationType);

		int division = totalDataNum / numOfCrossValidation;

		for (int i = 1; i <= numOfCrossValidation; i++) {
			confusionMatrix = new double[ClassificationUI.numOfClasses][ClassificationUI.numOfClasses];

			for (State s : classes) {
				ArrayList<Sample> trainingSamples = new ArrayList<>(
						s.getSamples());
				List<Sample> testingSamples = new ArrayList<>(
						trainingSamples.subList((i - 1) * division, i
								* division));
				trainingSamples.subList((i - 1) * division, i * division)
						.clear();

				classification.trainSamples(bw, trainingSamples, s, i - 1);
				populateConfusionMatrix(testingSamples, s, classification);
			}

			printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			assignAccuracy(i - 1, division * classes.size());

			classification = getClassificationType(classificationType);

		}

		printAccuracy(bw);
	}
	
	private List<Sample> getAllTrainingSamplesForDecisionTrees(int division, int i) {
		ArrayList<Sample> alltrainingSamples = new ArrayList<>();
		for (State s : classes) {
			ArrayList<Sample> trainingSamples = new ArrayList<>(
					s.getSamples());
			List<Sample> testingSamples = new ArrayList<>(
					trainingSamples.subList((i - 1) * division, i
							* division));
			trainingSamples.subList((i - 1) * division, i * division)
					.clear();
			alltrainingSamples.addAll(trainingSamples);
			
		}
		
		return alltrainingSamples;
	}

	public void performTrainingAndTestingForDecisionTree(BufferedWriter bw) throws IOException {
		Classification classification = new DecisionTree(classes.size(), numOfFeatures);

		int division = totalDataNum / numOfCrossValidation;

		for (int i = 1; i <= numOfCrossValidation; i++) {
			confusionMatrix = new double[ClassificationUI.numOfClasses][ClassificationUI.numOfClasses];

			List<Sample> alltrainingSamples = getAllTrainingSamplesForDecisionTrees(division, i);
			for (State s : classes) {
				List<Sample> testingSamples = new ArrayList<>(
						s.getSamples().subList((i - 1) * division, i
								* division));
				classification.trainSamples(bw, alltrainingSamples, s, i - 1);
				populateConfusionMatrix(testingSamples, s, classification);
				
			}
			

			printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			assignAccuracy(i - 1, division * classes.size());

			classification = new DecisionTree(classes.size(), numOfFeatures);

		}

		printAccuracy(bw);
	}
	
	private Classification getClassificationType(String classificationType) {
		if (classificationType
				.equals(ClassificationUI.naivebayesianClassificationType))
			return new NaiveBayesClassifier(classes.size(), numOfFeatures);

		return new BayesNetworkClassifier(classes.size(), numOfFeatures, dependenceTree);
	}

	public void printConfusionMatrix(BufferedWriter bw, String message)
			throws IOException {
		DecimalFormat df = new DecimalFormat("0.0");

		bw.write("\n\n*****************************" + message
				+ "***********************\n");
		for (int i = 1; i <= ClassificationUI.numOfClasses; i++)
			bw.write("  W" + i + "  | ");

		bw.write("\n");
		for (int i = 0; i < ClassificationUI.numOfClasses; i++) {
			for (int j = 0; j < ClassificationUI.numOfClasses; j++) {
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

		bw.write("\n\nOverall Accuracy is "
				+ df.format((sum / accuracy.length * 100)) + "%.");

	}

	public void assignAccuracy(int foldNum, int totalNumOfElement) {
		for (int i = 0; i < ClassificationUI.numOfClasses; i++) {
			accuracy[foldNum] += confusionMatrix[i][i];
		}

		accuracy[foldNum] = accuracy[foldNum] / totalNumOfElement;
	}

	private void populateConfusionMatrix(List<Sample> testingSamples,
			State state, Classification classification) {
		for (Sample sample : testingSamples) {
			double totalProbability = Double.MIN_VALUE;
			int indexForRightClass = 0;

			for (State s : classes) {
				double sum = classification.getProbabilityOfWGivenX(sample, s);
				if (sum > totalProbability) {
					totalProbability = sum;
					indexForRightClass = s.getNum() - 1;
				}
			}

			confusionMatrix[state.getNum() - 1][indexForRightClass] += 1;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
