package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrainingAndTestingClassification {
	private Set<State> classes;
	private int numOfCrossValidation;
	private int numOfFeatures;
	protected double[][] confusionMatrix;
	protected static double[] accuracy;
	private DependenceTree dependenceTree;

	public TrainingAndTestingClassification(Set<State> classes, int numOfCrossValidation, int numOfFeatures) {
		this.classes = classes;
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


		for (int i = 1; i <= numOfCrossValidation; i++) {
			confusionMatrix = new double[ClassificationUI.numOfClasses][ClassificationUI.numOfClasses];
            List<List<Sample>> testingSamplesForClasses = new ArrayList<List<Sample>>(ClassificationUI.numOfClasses);
			int[] totalTestingData = new int[classes.size()];

            for (State s : classes) {
				int division = s.getSamples().size() / numOfCrossValidation;
				int start = (i - 1) * division;
				int end = i* division;
				
				if (i == numOfCrossValidation)
					end = s.getSamples().size();
				ArrayList<Sample> trainingSamples = new ArrayList<>(
						s.getSamples());
				List<Sample> testingSamples = new ArrayList<>(
						trainingSamples.subList(start, end));
				trainingSamples.subList(start, end).clear();

				classification.trainSamples(bw, trainingSamples, s);
				totalTestingData[s.getNum()-1] = testingSamples.size();

				testingSamplesForClasses.add(testingSamples);				
			}
			
			for (State s : classes) {
				populateConfusionMatrix(testingSamplesForClasses.get(s.getNum()-1), s, classification);
			}

			printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			assignAccuracy(i - 1, totalTestingData);

			classification = getClassificationType(classificationType);

		}

		printAccuracy(bw);
	}
	
	private List<Sample> getAllTrainingSamplesForDecisionTrees(int i) {
		ArrayList<Sample> alltrainingSamples = new ArrayList<>();
		for (State s : classes) {
			int division = s.getSamples().size() / numOfCrossValidation;

			int start = (i - 1) * division;
			int end = i* division;
			
			if (i == numOfCrossValidation)
				end = s.getSamples().size();
			ArrayList<Sample> trainingSamples = new ArrayList<>(
					s.getSamples());
			trainingSamples.subList(start, end)
					.clear();
			alltrainingSamples.addAll(trainingSamples);
			
		}
		
		return alltrainingSamples;
	}

	public void performTrainingAndTestingForDecisionTree(BufferedWriter bw) throws IOException {
		Classification classification = new DecisionTree(classes.size(), numOfFeatures);


		for (int i = 1; i <= numOfCrossValidation; i++) {
			confusionMatrix = new double[ClassificationUI.numOfClasses][ClassificationUI.numOfClasses];
			int[] totalTestingData = new int[classes.size()];
			List<Sample> alltrainingSamples = getAllTrainingSamplesForDecisionTrees(i);
			classification.trainSamples(bw, alltrainingSamples, classes.iterator().next());

			for (State s : classes) {
				int division = s.getSamples().size() / numOfCrossValidation;
				int start = (i - 1) * division;
				int end = i* division;
				
				if (i == numOfCrossValidation)
					end = s.getSamples().size();
				List<Sample> testingSamples = new ArrayList<>(
						s.getSamples().subList(start, end));
				populateConfusionMatrixForDecisionTree(testingSamples, s, classification);
				totalTestingData[s.getNum()-1] = testingSamples.size();
			}
			

			printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			assignAccuracy(i - 1, totalTestingData);

			classification = new DecisionTree(classes.size(), numOfFeatures);

		}

		printAccuracy(bw);
	}
	
	private void populateConfusionMatrixForDecisionTree(List<Sample> testingSamples,
			State state, Classification classification) {
		for (Sample sample : testingSamples) {
			int indexForRightClass = (int)classification.getProbabilityOfWGivenX(sample, state) - 1;

			confusionMatrix[state.getNum() - 1][indexForRightClass] += 1;
		}
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

	public void assignAccuracy(int foldNum, int[] totalTestingData) {
		double sum = 0;
		for (int i = 0; i < ClassificationUI.numOfClasses; i++) {
			sum += 1.0 * confusionMatrix[i][i] / totalTestingData[i];
		}

		
		accuracy[foldNum] = sum / totalTestingData.length;
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
