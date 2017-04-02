package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BayesNetworkClassifier extends Classification{

	private int numOfFeatures;
	private int numOfClasses;
	private DependenceTree dependenceTree;

	//trainingEstimates[numOfClasses][featureIndex][dependencyNumber]
	private double[][][] trainingEstimates;

	public BayesNetworkClassifier(int numOfClasses, int numOfFeatures, DependenceTree tree) {
		this.numOfFeatures = numOfFeatures;
		this.numOfClasses = numOfClasses;
		this.trainingEstimates = new double[numOfClasses][numOfFeatures][2];
		this.dependenceTree = tree;
	}
	
	@Override
	public void trainSamples(BufferedWriter bw, List<Sample> trainingSamples,
			State state) {
		for (Node node : dependenceTree.getDependencyTreeNodes()) {
			assignProbabilityOfFeatureInSamples(trainingSamples, state, node);
			try {
				ClassificationUI.printDependenceTreeFeatureProbability(bw, state, node, trainingEstimates, dependenceTree.getRoot());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
	}

	
	private void assignProbabilityOfFeatureInSamples(List<Sample> samples, State state, Node node) {
		
		int parentSizeIsZero = 0;
		int childrenSizeIsZeroWhenParentSizeIsOne = 0;
		int childrenSizeIsZeroWhenParentSizeIsZero = 0;

		int featureIndex = node.getNum() - 1;
		Node parent = node.getParent();
        
		for (Sample sample : samples) {
			if(parent == null) {
				if(sample.getBinaryNumber(featureIndex) == 0)
					childrenSizeIsZeroWhenParentSizeIsZero++;
			} else {
				if(sample.getBinaryNumber(parent.getNum() - 1) == 0)
					parentSizeIsZero++;
				
				if(sample.getBinaryNumber(parent.getNum() - 1) == 0 && sample.getBinaryNumber(node.getNum() - 1) == 0)
					childrenSizeIsZeroWhenParentSizeIsZero++;
				else if(sample.getBinaryNumber(parent.getNum() - 1) == 1 && sample.getBinaryNumber(node.getNum() - 1) == 0)
					childrenSizeIsZeroWhenParentSizeIsOne++;
			}
		}
		
		if (parent == null) {
			if(samples.size() == 0) 
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][0] = 0;
			else
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][0] = 1.0 * childrenSizeIsZeroWhenParentSizeIsZero / samples.size();
		} else {
			if ((samples.size()- parentSizeIsZero) == 0)
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][1] = 0;
			else
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][1] = 1.0 * childrenSizeIsZeroWhenParentSizeIsOne / (samples.size()- parentSizeIsZero);
			
			if (parentSizeIsZero == 0 )
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][0] = 0;
			else
				trainingEstimates[state.getNum() - 1][node.getNum() - 1][0] = 1.0 * childrenSizeIsZeroWhenParentSizeIsZero / parentSizeIsZero;
		}
	}
	
	protected double getProbabilityOfWGivenX(Sample sample, State state) {
		// TODO Auto-generated method stub
		double probability = 1.0;
		
		 for(Node node : dependenceTree.getDependencyTreeNodes()) {
			  if (node.equals(dependenceTree.getRoot())) {
				  if(sample.getBinaryNumber(node.getNum() - 1) == 0)
					  	 probability *= trainingEstimates[state.getNum()-1][node.getNum()-1][0];
				  else {
					  probability *= 1 - trainingEstimates[state.getNum()-1][node.getNum()-1][0];
				  }
			  } else {
				  int dependentNodeIndex = sample.getBinaryNumber(node.getParent().getNum() - 1);
				  if(dependentNodeIndex == 0 && sample.getBinaryNumber(node.getNum() - 1) == 0) {
					  	 probability *= trainingEstimates[state.getNum()-1][node.getNum()-1][0];
				  } else if (dependentNodeIndex == 1 && sample.getBinaryNumber(node.getNum() - 1) == 0) {
					  	 probability *= trainingEstimates[state.getNum()-1][node.getNum()-1][1];
				  } else if (dependentNodeIndex == 0 && sample.getBinaryNumber(node.getNum() - 1) == 1) {
					  	 probability *= 1 - trainingEstimates[state.getNum()-1][node.getNum()-1][0];
				  } else {
					  	 probability *= 1 - trainingEstimates[state.getNum()-1][node.getNum()-1][1];
				  }
			  }
		  }
		
		return probability;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
