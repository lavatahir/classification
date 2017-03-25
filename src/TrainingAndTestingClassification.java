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
	private double[][][] trainingEstimates;

	public TrainingAndTestingClassification(Set<State> classes, int totalDataNum, int numOfCrossValidation,
			int numOfFeatures) {
		this.classes = classes;
		this.totalDataNum = totalDataNum;
		this.numOfCrossValidation = numOfCrossValidation;
		this.numOfFeatures = numOfFeatures;
		trainingEstimates = new double[numOfCrossValidation][classes.size()][numOfFeatures];

	}

	public void performTrainingAndTesting(BufferedWriter bw, String classificationType) throws IOException {
		Classification classification = new BayesianClassification(classes.size(), numOfFeatures);
		int division = totalDataNum / numOfCrossValidation;

		for (int i = 1; i <= numOfCrossValidation; i++) {
			for (State s : classes) {
				ArrayList<Sample> trainingSamples = new ArrayList<>(s.getSamples());
				List<Sample> testingSamples = new ArrayList<>(trainingSamples.subList((i - 1) * division, i * division));
				trainingSamples.subList((i - 1) * division, i * division).clear();

				classification.classify(bw, trainingSamples, testingSamples, s, i-1);
			}
			
			classification.printConfusionMatrix(bw, "Confusion Matrix For Fold " + i);
			classification.assignAccuracy(i-1, division * classes.size());

			classification = new BayesianClassification(classes.size(), numOfFeatures);
		}
		
		classification.printAccuracy(bw);
		
		

	}
//	public void performTrainingAndTesting(BufferedWriter bw) throws IOException {
//		for (int i = 1; i <= numOfCrossValidation; i++) {
//			for (State s : classes) {
//				int division = totalDataNum / numOfCrossValidation;
//				ArrayList<Sample> samples = new ArrayList<>(s.getSamples());
//				List<Sample> testingSamples = new ArrayList<>(samples.subList((i - 1) * division, i * division));
//				samples.subList((i - 1) * division, i * division).clear();
//
//				for (Sample sample : samples) {
//					updateTrainingEstimates(i - 1, s.getNum() - 1, sample);
//				}
//
//				printTrainingEstimates(bw, i - 1, s.getNum());
//			}
//			bw.write("\n\n");
//		}
//
//	}

	private void updateTrainingEstimates(int index, int classIndex, Sample sample) {
		for (int i = 0; i < numOfFeatures; i++) {
			if (sample.getProbabilityForBinaryFeature(i) == 0.0) {
				double num = totalDataNum - totalDataNum / numOfCrossValidation;

				trainingEstimates[index][classIndex][i] = (trainingEstimates[index][classIndex][i] + 1 / num);
			}
		}
	}

	private void printTrainingEstimates(BufferedWriter bw, int index, int classIndex) throws IOException {
		DecimalFormat df = new DecimalFormat("#.##");

		bw.write("\n\n****************Training Estimates For W" + classIndex + "********************\n");
		for (int j = 0; j < numOfFeatures; j++) {
			bw.write(" " + Double.valueOf(df.format(trainingEstimates[index][classIndex - 1][j])) + " | ");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
