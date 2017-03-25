import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class MainClassification {
	public static int numOfClasses = 4;
	public static int numofFeatures = 10;
	private static final String FILENAME = "classificationData.txt";
	public static List<State> classes = new ArrayList<>();
	private static int totalDataNum = 15;
	public static int numOFCrossValidationFold = 5;

	private static void geneateActualProbability(BufferedWriter bw) throws IOException {
		Random rand = new Random();
		DecimalFormat df = new DecimalFormat("#.##");

		for (int i = 1; i <= numOfClasses; i++) {
			bw.write("*************************W" + i + " actual probability **************************\n");
			State s = new State(i, numofFeatures, totalDataNum / (totalDataNum * numOfClasses * 1.0));

			for (int j = 0; j < numofFeatures; j++) {
				double probability = Double.valueOf(df.format(rand.nextDouble()));
				bw.write(probability + " | ");
				s.addActualProbability(j, probability);
			}

			bw.write("\n\n");
			classes.add(s);
		}
	}

	public static void printFeatures(BufferedWriter bw) throws IOException {
		for (int i = 1; i <= numofFeatures; i++)
			bw.write("  x" + i + "  | ");

		bw.write("\n");

	}

	private static void generateRandomSamplesForClasses() throws IOException {
		FileWriter fw = new FileWriter("artificialDataSets.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		Random rand = new Random();
		DecimalFormat df = new DecimalFormat("0.00");

		for (State state : classes) {
			bw.write("\n\n***********************Sample Data For W" + state.getNum() + "**************************\n\n");
			printFeatures(bw);
			for (int i = 0; i < totalDataNum; i++) {
				Sample s = new Sample(numofFeatures);
				for (int j = 0; j < numofFeatures; j++) {
					double estimateProb = Double.valueOf(df.format(rand.nextDouble()));

					// int num = 1;
					// if (estimateProb < state.getActualProbability(j)) {
					// num = 0;
					// }

					bw.write(" " + df.format(estimateProb) + " | ");
					s.initializeFeatureWithDecimalValue(j, estimateProb);
				}

				state.addSample(s);

				bw.write("\n");
			}

		}

		bw.close();
		fw.close();
	}

	public static void main(String[] args) throws IOException {
		FileWriter fw = new FileWriter(FILENAME);
		BufferedWriter bw = new BufferedWriter(fw);

		geneateActualProbability(bw);
		generateRandomSamplesForClasses();
		bw.close();
		fw.close();
		TrainingAndTestingClassification tr = new TrainingAndTestingClassification(new LinkedHashSet(classes),
				totalDataNum, numOFCrossValidationFold, numofFeatures);
		
		 fw = new FileWriter("bayesianClassification.txt");
		 bw = new BufferedWriter(fw);
		tr.performTrainingAndTesting(bw, "bayesian");
		bw.close();
		fw.close();
	}

}
