package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClassificationUI {
	public static int numOfClasses = 4;
	public static int numofFeatures = 10;
	private static int totalDataNum = 6;
	public static int numOFCrossValidationFold = 2000;

	public static List<State> classes;

	// file names
	private static final String artificialDataSetsFileName = "decimalValueDataSets.txt";
	private static final String binaryDataSetsFileName = "binaryDataSets.txt";

	
	//tree
	private static List<Node> dependenceTree;
	private static Node dependenceTreeRoot;

	//actual probabilities based on dependency tree
	private static double[][][] actualProbabilities;
	
	//decimal samples for classes
	private static double[][][] samples;

	
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

	private static void generateRandomSamplesForClasses(String fileName) {

		try {
			samples = new double[classes.size()][totalDataNum][numofFeatures];
			FileWriter fw = new FileWriter(artificialDataSetsFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			Random rand = new Random();
			DecimalFormat df = new DecimalFormat("0.00");

			for (State state : classes) {
				bw.write("\n\n***********************Sample Data For W" + state.getNum()
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
				  if (samples[state.getNum()-1][index][node.getNum()-1] < actualProbabilities[node.getNum()-1][0][0]){
					  num = 0;
				  }
			  } else {
				  int dependentNodeIndex = s.getBinaryNumber(node.getParent().getNum() - 1);
				  if (samples[state.getNum()-1][index][node.getNum()-1] < actualProbabilities[node.getNum()-1][dependentNodeIndex][0]) {
					  num = 0;
				  }

			  }
			  
			  s.initializeFeatureWithBinaryValue(node.getNum() - 1, 1);
			  
			  bw.write("   " + num + "  | ");
		  }1
		  
		  bw.write("\n");
		  
		  return s;
	  }
	  
	  private static void generateRandomProbabilityForClassesUsingDependencyTree(BufferedWriter bw) throws IOException {
		  //possibility for binary is 1 or 0
		  int num = 2;
		  Random rand = new Random();
		  actualProbabilities = new double[numofFeatures][num][num];
		  for(State s : classes) {
			  bw.write("\n\n\n\n***********************Sample Data Probability For W" + s.getNum()
				+ "**************************\n");
			  for(Node n : dependenceTree) {
				  
				  //actualProbabilities[featureIndex][dependencyNumber][featureNumber]
				  actualProbabilities[n.getNum()-1][0][0] = rand.nextDouble();
				  actualProbabilities[n.getNum()-1][0][1] = 1 - actualProbabilities[n.getNum()-1][0][0];
				  
				  if(!n.equals(dependenceTreeRoot)) {
					  actualProbabilities[n.getNum()-1][1][0] = rand.nextDouble();
					  actualProbabilities[n.getNum()-1][1][1] = 1 - actualProbabilities[n.getNum()-1][1][0];
				  }
				  
				  printDependenceTreeFeatureProbability(bw, s, n);
			  }
		  }
	  }
	  
	  private static void printDependenceTreeFeatureProbability(BufferedWriter bw, State state, Node node) throws IOException {
			DecimalFormat df = new DecimalFormat("0.00");

			
			if (node.equals(dependenceTreeRoot)) {
				bw.write("P(X" + node.getNum() + " = 0 ) = "
						+ df.format(actualProbabilities[node.getNum()-1][0][0]));
				bw.write("\tP(X" + node.getNum() + " = 1 ) = "
						+ df.format(actualProbabilities[node.getNum()-1][0][1]) + "\n\n");
			} else {

				for (int i = 0; i < 2; i++) {
					bw.write("P(X" + node.getNum() + " = 0 | X" + node.getParent().getNum() + " = " + i + " ) = "
							+ df.format(actualProbabilities[node.getNum()-1][i][0]));
					bw.write("\tP(X" + node.getNum() + " = 1 | X" + node.getParent().getNum() + " = " + i + " ) = "
							+ df.format(actualProbabilities[node.getNum()-1][i][1])+ "\n\n");
				}
			}
		}
	  
	  
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to my classification program\n");

		boolean exitProgram = false;

		BufferedWriter bw = new BufferedWriter
			    (new OutputStreamWriter(new FileOutputStream(binaryDataSetsFileName),"UTF-8"));
		
		while (!exitProgram) {
			System.out.println("Please select what you would like to do");
			printDataOptions();
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();

			if (input.equals("1")) {
				createStates();
				generateRandomSamplesForClasses(artificialDataSetsFileName);
				createDepedenceTree();
				dependenceTreeRoot.print(bw);
				generateRandomProbabilityForClassesUsingDependencyTree(bw);
				generateRandomBinarySamplesForClasses(bw);
			} else if (input.equals("2")) {
				createStates();
			} else if (input.equals("3")) {
				exitProgram = true;
			}
		}
		
		bw.close();
	}

}
