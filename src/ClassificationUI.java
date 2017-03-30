package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class ClassificationUI {
	public static int numOfClasses = 4;
	public static int numofFeatures = 10;
	private static int totalDataNum = 2000;
	public static int numOFCrossValidationFold = 5;

	public static List<State> classes;

	// file names
	private static final String artificialDataSetsFileName = "decimalValueDataSets.txt";
	private static final String binaryDataSetsFileName = "binaryDataSets.txt";
	private static final String artificialNaiveBayesFileName = "artificialNaiveBayes.txt";
	private static final String artificialDependenceFileName = "artificialDependenceTree.txt";

	
	//tree
	private static List<Node> dependenceTree;
	private static Node dependenceTreeRoot;

	//actual probabilities based on dependency tree
	private static double[][][] actualProbabilities;
	
	//decimal samples for classes
	private static double[][][] samples;
	
	// classification types
	public static String naivebayesianClassificationType = "naive";
	public static String dependentTreeClassificationType = "dependentTree";

	
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
		for (int i = 1; i <= numOfClasses; i++) {
			State s = new State(i, numofFeatures, totalDataNum / (totalDataNum * numOfClasses * 1.0));
			classes.add(s);
		}
	}

	private static void generateRandomSamplesForClasses() {

		try {
			samples = new double[classes.size()][totalDataNum][numofFeatures];
			FileWriter fw = new FileWriter(artificialDataSetsFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			Random rand = new Random();
			DecimalFormat df = new DecimalFormat("0.00");

			for (State state : classes) {
				bw.write("\n\n\n\n\n***********************Sample Data For W" + state.getNum()
						+ "**************************\n\n");
				printFeatures(bw);
				for (int i = 0; i < totalDataNum; i++) {
					Sample s = new Sample(numofFeatures, state.getNum());
					for (int j = 0; j < numofFeatures; j++) {
						double estimateProb = Double.valueOf(df.format(rand.nextDouble()));
						bw.write(" " + df.format(estimateProb) + " | ");
						samples[state.getNum()-1][i][j] = estimateProb;
					}
					bw.write("\n");
				}
			}

			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	  private static void createDepedenceTree() {
		  dependenceTree = new ArrayList<>();
		  
		  for (int i=1; i<=numofFeatures; i++) {
			  dependenceTree.add(new Node(i));
		  }
		  
		  for (int i=0; i<numofFeatures; i++) {
			  if(i != numofFeatures - 1)
				  dependenceTree.get(i).addChildren(dependenceTree.get(i+1));
			  
			  if (i != 0) {
				  dependenceTree.get(i).setParent(dependenceTree.get(i-1));
			  } else {
				  dependenceTreeRoot = dependenceTree.get(i);
			  }
				  
		  }
		  
	  }
	  
	  private static void generateRandomBinarySamplesForClasses(BufferedWriter bw) throws IOException {
		  for (State state : classes) {
				bw.write(
						"\n\n***********************Sample Binary Data For W" + state.getNum() + "**************************\n\n");
			  for (int i=0; i<totalDataNum; i++) {
				  state.addSample(generateBinarySamplesForState(state, i, bw));
			  }
			  
		  }
	  }
	  
	  private static Sample generateBinarySamplesForState (State state, int index, BufferedWriter bw) throws IOException {
		  
		  Sample s = new Sample(numofFeatures, state.getNum());
		  for(Node node : dependenceTree) {
			  int num = 1;
			  if (node.equals(dependenceTreeRoot)) {
				  if (samples[state.getNum()-1][index][node.getNum()-1] < actualProbabilities[state.getNum()-1][node.getNum()-1][0]){
					  num = 0;
				  }
			  } else {
				  int dependentNodeIndex = s.getBinaryNumber(node.getParent().getNum() - 1);
				  if (samples[state.getNum()-1][index][node.getNum()-1] < actualProbabilities[state.getNum()-1][node.getNum()-1][dependentNodeIndex]) {
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
		  //possibility for binary is 1 or 0
		  int num = 2;
		  Random rand = new Random();
		  actualProbabilities = new double[numOfClasses][numofFeatures][num];
		  for(State s : classes) {
			  bw.write("\n\n\n\n***********************Sample Data Probability For W" + s.getNum()
				+ "**************************\n");
			  for(Node n : dependenceTree) {
				  
				  //actualProbabilities[numOfClasses][featureIndex][dependencyNumber]
				  actualProbabilities[s.getNum() - 1][n.getNum()-1][0] = rand.nextDouble();
				  
				  if(!n.equals(dependenceTreeRoot)) {
					  actualProbabilities[s.getNum() - 1][n.getNum()-1][1] = rand.nextDouble();
				  }
				  
				  printDependenceTreeFeatureProbability(bw, s, n, actualProbabilities, dependenceTreeRoot);
			  }
		  }
	  }
	  
	  public static void printDependenceTreeFeatureProbability(BufferedWriter bw, State state, Node node, double[][][] probabilities, Node root) throws IOException {
			DecimalFormat df = new DecimalFormat("0.00");

			
			if (node.equals(root)) {
				bw.write("P(X" + node.getNum() + " = 0 ) = "
						+ df.format(probabilities[state.getNum() - 1][node.getNum()-1][0]));
				bw.write("\tP(X" + node.getNum() + " = 1 ) = "
						+ df.format(1 - probabilities[state.getNum() - 1][node.getNum()-1][0]) + "\n\n");
			} else {

				for (int i = 0; i < 2; i++) {
					bw.write("P(X" + node.getNum() + " = 0 | X" + node.getParent().getNum() + " = " + i + " ) = "
							+ df.format(probabilities[state.getNum()-1][node.getNum()-1][i]));
					bw.write("\tP(X" + node.getNum() + " = 1 | X" + node.getParent().getNum() + " = " + i + " ) = "
							+ df.format(1 - probabilities[state.getNum()-1][node.getNum()-1][i])+ "\n\n");
				}
			}
		}
	  
	  
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to my classification program\n");

		boolean exitProgram = false;
		FileWriter fw = new FileWriter(binaryDataSetsFileName);

		BufferedWriter bw = new BufferedWriter(fw);
		
		while (!exitProgram) {
			System.out.println("Please select what you would like to do");
			printDataOptions();
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();

			if (input.equals("1")) {
				createStates();
				generateRandomSamplesForClasses();
				createDepedenceTree();
				dependenceTreeRoot.print(bw);
				generateRandomProbabilityForClassesUsingDependencyTree(bw);
				generateRandomBinarySamplesForClasses(bw);

				TrainingAndTestingClassification tr = new TrainingAndTestingClassification(new LinkedHashSet(classes),
						totalDataNum, numOFCrossValidationFold, numofFeatures);
				
				BufferedWriter naiveBayes = new BufferedWriter
					    (new OutputStreamWriter(new FileOutputStream(artificialNaiveBayesFileName),"UTF-8"));
				
				tr.performTrainingAndTesting(naiveBayes, ClassificationUI.naivebayesianClassificationType);
				
				naiveBayes.close();

				BufferedWriter dependenceTreeFile = new BufferedWriter
					    (new OutputStreamWriter(new FileOutputStream(artificialDependenceFileName),"UTF-8"));
				
				
				tr.performTrainingAndTesting(dependenceTreeFile, ClassificationUI.dependentTreeClassificationType);
				
				dependenceTreeFile.close();
			} else if (input.equals("2")) {
				createStates();
			} else if (input.equals("3")) {
				exitProgram = true;
			}
			
			
		}
		
		bw.close();
		fw.close();
		
	}

}
