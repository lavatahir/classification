package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClassificationUI {
	public static int numOfClasses;
	public static int numofFeatures = 0;
	public static int numOFCrossValidationFold = 5;

	public static List<State> classes;
	public static List<Integer> totalData;

	// artificial file names
	private static final String artificialDataSetsFileName = "decimalValueDataSets.csv";
	private static final String binaryDataSetsFileName = "binaryDataSets.txt";
	private static final String artificialNaiveBayesFileName = "artificialNaiveBayes.txt";
	private static final String artificialDependenceFileName = "artificialDependenceTree.txt";
	private static final String artificialDecisionFileName = "artificialDecisionTree.txt";

	// artificial file names
	private static final String realDataSetsFileName = "wine.csv";
	private static final String realBinaryDataSetsFileName = "realBinaryDataSets.txt";
	private static final String realNaiveBayesFileName = "realNaiveBayes.txt";
	private static final String realDependenceFileName = "realDependenceTree.txt";
	private static final String realDecisionFileName = "realDecisionTree.txt";

	// tree
	private static List<Node> dependenceTree;
	private static Node dependenceTreeRoot;

	// actual probabilities based on dependency tree
	private static double[][][] actualProbabilities;

	// decimal samples for classes
	private static double[][][] samples;

	// classification types
	public static String naivebayesianClassificationType = "naive";
	public static String dependentTreeClassificationType = "dependentTree";
	public static String decisionTreeClassificationType = "decisionTree";

	// mean and variance
	private static double[][] mean;
	private static double[][] variance;

	private static void printDataOptions() {
		System.out.println("1. Classify artificial data sets");
		System.out.println("2. Classify real data sets");
		System.out.println("3. Exit");

	}

	public static void printFeatures(BufferedWriter bw) throws IOException {
		for (int i = 1; i <= numofFeatures; i++)
			bw.write("  x" + i + "  | ");

		bw.write("\n");

	}

	private static void createStates() {
		classes = new ArrayList<>();
		classes.clear();

		for (int i = 1; i <= numOfClasses; i++) {
			State s = new State(i, numofFeatures);
			classes.add(s);
		}
	}

	private static void readSamplesFromFile(String fileName) {
		samples = new double[10][8000][70];
		totalData = new ArrayList<>();
		numOfClasses = 1;

		try {
			String splitBy = ",";
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			int stateNum = 1;
			int sampleNumber = 0;
			while ((line = br.readLine()) != null) {
				String[] b = line.split(splitBy);
				numofFeatures = b.length - 1;

				if (stateNum != Integer.parseInt(b[0])) {
					stateNum = Integer.parseInt(b[0]);
					totalData.add(sampleNumber);
					System.out.println(sampleNumber);
					sampleNumber = 0;
					numOfClasses += 1;
				}

				for (int i = 0; i < b.length - 1; i++) {
					samples[stateNum - 1][sampleNumber][i] = Double.parseDouble(b[i + 1]);
				}

				sampleNumber++;
			}

			System.out.println(sampleNumber);
			System.out.println(numOfClasses);

			totalData.add(sampleNumber);
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private static void generateRandomSamplesForClasses() {
	//
	// try {
	// samples = new double[classes.size()][totalDataNum][numofFeatures];
	// FileWriter fw = new FileWriter(artificialDataSetsFileName);
	// BufferedWriter bw = new BufferedWriter(fw);
	// Random rand = new Random();
	// DecimalFormat df = new DecimalFormat("0.00");
	//
	// StringBuilder sb = new StringBuilder();
	//
	// for (State state : classes) {
	// System.out.println(state.getNum());
	// for (int i = 0; i < totalDataNum; i++) {
	// sb.append(state.getNum());
	// for (int j = 0; j < numofFeatures; j++) {
	// double estimateProb = Double.valueOf(df.format(rand.nextDouble()));
	// sb.append(',');
	// sb.append(df.format(estimateProb));
	// samples[state.getNum() - 1][i][j] = estimateProb;
	// }
	// sb.append('\n');
	// }
	// }
	//
	// bw.write(sb.toString());
	//
	// bw.close();
	// fw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private static void createDepedenceTree() {
		dependenceTree = new ArrayList<>();

		for (int i = 1; i <= numofFeatures; i++) {
			dependenceTree.add(new Node(i));
		}

		for (int i = 0; i < numofFeatures; i++) {
			if (i != numofFeatures - 1)
				dependenceTree.get(i).addChildren(dependenceTree.get(i + 1));

			if (i != 0) {
				dependenceTree.get(i).setParent(dependenceTree.get(i - 1));
			} else {
				dependenceTreeRoot = dependenceTree.get(i);
			}

		}

	}

	private static void generateRandomBinarySamplesForClasses(BufferedWriter bw) throws IOException {
		for (State state : classes) {
			bw.write("\n\n***********************Sample Binary Data For W" + state.getNum()
					+ "**************************\n\n");

			for (int i = 0; i < totalData.get(state.getNum() - 1); i++) {
				state.addSample(generateBinarySamplesForState(state, i, bw));
			}

		}
	}

	private static void generateBinarySamplesForClasses(BufferedWriter bw) throws IOException {

		for (State state : classes) {
			bw.write("\n\n***********************Sample Binary Data For W" + state.getNum()
					+ "**************************\n\n");

			for (int i = 0; i < totalData.get(state.getNum() - 1); i++) {
				Sample s = new Sample(numofFeatures, state.getNum());

				for (int j = 0; j < numofFeatures; j++) {
					int num = getValueAfterThresholdMechanism(samples[state.getNum() - 1][i][j], state.getNum() - 1,j);

					s.initializeFeatureWithBinaryValue(j, num);

					bw.write("   " + num + "  | ");
				}
				bw.write("\n");

				state.addSample(s);
			}
		}
	}

	private static Sample generateBinarySamplesForState(State state, int index, BufferedWriter bw) throws IOException {

		Sample s = new Sample(numofFeatures, state.getNum());

		for (Node node : dependenceTree) {
			int num = 1;
			if (node.equals(dependenceTreeRoot)) {
				if (samples[state.getNum() - 1][index][node.getNum()
						- 1] < actualProbabilities[state.getNum() - 1][node.getNum() - 1][0]) {
					num = 0;
				}
			} else {
				int dependentNodeIndex = s.getBinaryNumber(node.getParent().getNum() - 1);
				if (samples[state.getNum() - 1][index][node.getNum()
						- 1] < actualProbabilities[state.getNum() - 1][node.getNum() - 1][dependentNodeIndex]) {
					num = 0;
				}
			}

			s.initializeFeatureWithBinaryValue(node.getNum() - 1, num);

			bw.write("   " + num + "  | ");
		}

		bw.write("\n");

		return s;
	}

	private static void generateRandomProbabilityForClassesUsingDependencyTree(BufferedWriter bw) throws IOException {
		// possibility for binary is 1 or 0
		int num = 2;
		Random rand = new Random();
		actualProbabilities = new double[numOfClasses][numofFeatures][num];
		for (State s : classes) {
			bw.write("\n\n\n\n***********************Sample Data Probability For W" + s.getNum()
					+ "**************************\n");
			for (Node n : dependenceTree) {

				// actualProbabilities[numOfClasses][featureIndex][dependencyNumber]
				actualProbabilities[s.getNum() - 1][n.getNum() - 1][0] = rand.nextDouble();

				if (!n.equals(dependenceTreeRoot)) {
					actualProbabilities[s.getNum() - 1][n.getNum() - 1][1] = rand.nextDouble();
				}

				printDependenceTreeFeatureProbability(bw, s, n, actualProbabilities, dependenceTreeRoot);
			}
		}
	}

	private static void populateMean(State state) {
		for (int i = 0; i < numofFeatures; i++) {
			double sum = 0;
			for (int j = 0; j < totalData.get(state.getNum() - 1); j++) {
				sum += samples[state.getNum() - 1][j][i];
			}

			mean[state.getNum() - 1][i] = 1.0 * sum / totalData.get(state.getNum() - 1);
		}
	}

	private static void populateVariance(State state) {
		for (int i = 0; i < numofFeatures; i++) {
			double sum = 0;
			for (int j = 0; j < totalData.get(state.getNum() - 1); j++) {
				sum += Math.pow(samples[state.getNum() - 1][j][i] - mean[state.getNum() - 1][i], 2);
			}

			variance[state.getNum() - 1][i] = 1.0 * sum / totalData.get(state.getNum() - 1);
		}
	}

	private static double getGaussianProbabilityOfXGivenW(double x, int classNum, int featureNum) {
		double meanValue = mean[classNum][featureNum];
		double varianceValue = variance[classNum][featureNum];

		double innerBracketEval = Math.exp((-1 * Math.pow(x - meanValue, 2)) / (2 * varianceValue));
		double sqrEval = Math.sqrt(2 * Math.PI * varianceValue);

		int value = totalData.get(classNum);
		int total = 0;
		
		for (int i : totalData)
			total += i;
		return (innerBracketEval / sqrEval) * (1.0 * value / total);
	}

	private static int getValueAfterThresholdMechanism(double x, int classNum, int featureNum) {
		double maximumNumber = getGaussianProbabilityOfXGivenW(x, classNum, featureNum);
		for (State s : classes) {
			if (s.getNum() - 1 != classNum) {
				double num = getGaussianProbabilityOfXGivenW(x, s.getNum() - 1, featureNum);
				if (num > maximumNumber) {
					maximumNumber = num;
					return 1;
				}
			}
		}
		
		return 0;
	}

	private static void generateMeanAndVarianceForClasses(BufferedWriter bw) throws IOException {
		mean = new double[numOfClasses][numofFeatures];
		variance = new double[numOfClasses][numofFeatures];

		System.out.println("number of classes is " + numOfClasses);
		for (State s : classes) {
			bw.write("\n\n\n\n***********************Sample Data Probability For W" + s.getNum()
					+ "**************************\n");
			populateMean(s);
			populateVariance(s);

			print(bw, mean, s.getNum() - 1, "Mean for W" + s.getNum());
			print(bw, variance, s.getNum() - 1, "Variance for W" + s.getNum());
		}
	}

	private static void print(BufferedWriter bw, double[][] array, int i, String message) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0000");

		bw.write("\n\n*****************************" + message + "***********************\n");
		// MainClassification.printFeatures(bw);
		for (int j = 0; j < numofFeatures; j++) {
			bw.write(" " + df.format(array[i][j]) + " | ");
		}
		bw.write("\n");

	}

	public static void printDependenceTreeFeatureProbability(BufferedWriter bw, State state, Node node,
			double[][][] probabilities, Node root) throws IOException {
		DecimalFormat df = new DecimalFormat("0.00");

		if (node.equals(root)) {
			bw.write("P(X" + node.getNum() + " = 0 ) = "
					+ df.format(probabilities[state.getNum() - 1][node.getNum() - 1][0]));
			bw.write("\tP(X" + node.getNum() + " = 1 ) = "
					+ df.format(1 - probabilities[state.getNum() - 1][node.getNum() - 1][0]) + "\n\n");
		} else {

			for (int i = 0; i < 2; i++) {
				bw.write("P(X" + node.getNum() + " = 0 | X" + node.getParent().getNum() + " = " + i + " ) = "
						+ df.format(probabilities[state.getNum() - 1][node.getNum() - 1][i]));
				bw.write("\tP(X" + node.getNum() + " = 1 | X" + node.getParent().getNum() + " = " + i + " ) = "
						+ df.format(1 - probabilities[state.getNum() - 1][node.getNum() - 1][i]) + "\n\n");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to my classification program\n");

		boolean exitProgram = false;
		Scanner sc = new Scanner(System.in);

		while (!exitProgram) {
			System.out.println("Please select what you would like to do");
			printDataOptions();
			String input = sc.nextLine();

			if (input.equals("1")) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(binaryDataSetsFileName));

				readSamplesFromFile(artificialDataSetsFileName);
				createStates();
				createDepedenceTree();
				dependenceTreeRoot.print(bw);
				generateRandomProbabilityForClassesUsingDependencyTree(bw);
				generateRandomBinarySamplesForClasses(bw);

				TrainingAndTestingClassification tr = new TrainingAndTestingClassification(
						new LinkedHashSet<State>(classes), numOFCrossValidationFold, numofFeatures);

				BufferedWriter naiveBayes = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(artificialNaiveBayesFileName), "UTF-8"));

				tr.performTrainingAndTesting(naiveBayes, ClassificationUI.naivebayesianClassificationType);

				naiveBayes.close();

				BufferedWriter dependenceTreeFile = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(artificialDependenceFileName), "UTF-8"));

				tr.performTrainingAndTesting(dependenceTreeFile, ClassificationUI.dependentTreeClassificationType);

				dependenceTreeFile.close();

				BufferedWriter decisionTreeFile = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(artificialDecisionFileName), "UTF-8"));
				tr.performTrainingAndTestingForDecisionTree(decisionTreeFile);
				decisionTreeFile.close();
				bw.close();
			} else if (input.equals("2")) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(realBinaryDataSetsFileName));

				readSamplesFromFile(realDataSetsFileName);
				createStates();
				generateMeanAndVarianceForClasses(bw);
				generateBinarySamplesForClasses(bw);

				TrainingAndTestingClassification tr = new TrainingAndTestingClassification(
						new LinkedHashSet<State>(classes), numOFCrossValidationFold, numofFeatures);

				BufferedWriter naiveBayes = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(realNaiveBayesFileName), "UTF-8"));

				tr.performTrainingAndTesting(naiveBayes, ClassificationUI.naivebayesianClassificationType);

				naiveBayes.close();

				BufferedWriter dependenceTreeFile = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(realDependenceFileName), "UTF-8"));

				tr.performTrainingAndTesting(dependenceTreeFile, ClassificationUI.dependentTreeClassificationType);

				dependenceTreeFile.close();

				BufferedWriter decisionTreeFile = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(realDecisionFileName), "UTF-8"));
				tr.performTrainingAndTestingForDecisionTree(decisionTreeFile);
				decisionTreeFile.close();
				bw.close();
			} else if (input.equals("3")) {
				exitProgram = true;
			}

		}

		sc.close();
	}

}
