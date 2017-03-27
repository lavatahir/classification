import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class MainClassification {
	public static int numOfClasses = 2;
	public static int numofFeatures = 5;
	private static final String FILENAME = "classificationData.txt";
	public static List<State> classes;
	private static int totalDataNum = 3;
	public static int numOFCrossValidationFold = 5;
	
	private static String sampleType = "binary";

	private static void geneateActualProbability(BufferedWriter bw) throws IOException {
		classes = new ArrayList<>();
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

	private static void generateRandomSamplesForClasses(String sampleType, String fileName) throws IOException {

		FileWriter fw = new FileWriter(fileName);
		BufferedWriter bw = new BufferedWriter(fw);
		Random rand = new Random();
		DecimalFormat df = new DecimalFormat("0.00");
		geneateActualProbability(bw);

		for (State state : classes) {
			bw.write("\n\n***********************Sample Data For W" + state.getNum() + "**************************\n\n");
			printFeatures(bw);
			for (int i = 0; i < totalDataNum; i++) {
				Sample s = new Sample(numofFeatures);
				for (int j = 0; j < numofFeatures; j++) {
					double estimateProb = Double.valueOf(df.format(rand.nextDouble()));
					
					if (sampleType.equals(MainClassification.sampleType)) {
						int num = 1;
						if (estimateProb < state.getActualProbability(j)) {
							num = 0;
						}
						bw.write("   " + num + "  | ");
						s.initializeFeatureWithBinaryValue(j, num);
					} else {

					bw.write(" " + df.format(estimateProb) + " | ");
					s.initializeFeatureWithDecimalValue(j, estimateProb);
					}
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

		//bayesian
//		generateRandomSamplesForClasses("", "artificialDataSets.txt");
//		bw.close();
//		fw.close();
//		TrainingAndTestingClassification tr = new TrainingAndTestingClassification(new LinkedHashSet(classes),
//				totalDataNum, numOFCrossValidationFold, numofFeatures);
//		
//		 fw = new FileWriter("bayesianClassification.txt");
//		 bw = new BufferedWriter(fw);
//		tr.performTrainingAndTesting(bw, "bayesian");
//		bw.close();
//		fw.close();
		
		
		//dependence tree
		 fw = new FileWriter(FILENAME);
		 bw = new BufferedWriter(fw);

		generateRandomSamplesForClasses(MainClassification.sampleType, "artificialBinaryDataSets.txt");
		
		DependenceTree dependenceTree = new DependenceTree(numofFeatures);
		List<Sample> samples = new ArrayList<>();
		for (State state : classes) {
			samples.addAll(state.getSamples());
		}
		dependenceTree.getMaximumSpanningTree(samples);
		dependenceTree.drawInitialGraph();
	}

}
