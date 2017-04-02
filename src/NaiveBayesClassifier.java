package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class NaiveBayesClassifier extends Classification{
	
	//trainingEstimates[classNumber][featureIndex]
	private double[][] trainingEstimates;
	private int numOfFeatures;
	
	public NaiveBayesClassifier(int numOfClasses, int numOfFeatures) {
		this.numOfFeatures = numOfFeatures;
		this.trainingEstimates = new double[numOfClasses][numOfFeatures];
	}
	
	@Override
	public void trainSamples(BufferedWriter bw, List<Sample> trainingSamples,
			State state) {		
			for (int i=0; i<numOfFeatures; i++) {
				trainingEstimates[state.getNum() - 1][i] = getProbabilityOfFeatureInSamples(trainingSamples, i);
			}
			
			try {
				print(bw, state);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private double getProbabilityOfFeatureInSamples(List<Sample> samples, int featureIndex) {
		int count =0;
		
		for (Sample sample : samples) {
			if(sample.getBinaryNumber(featureIndex) == 0)
				count++;
		}
		
		if (samples.size() == 0)
			System.out.println("hellow");
		
		return 1.0 * count/samples.size();
	}
	
	private void print(BufferedWriter bw, State state) throws IOException {
		DecimalFormat df = new DecimalFormat("0.0000");

		bw.write("\n\n***************** Training Sample Probability For W" + state.getNum() + "***********************\n");
		ClassificationUI.printFeatures(bw);
		for (int i = 0; i < numOfFeatures; i++) {
			bw.write(" " + df.format(trainingEstimates[state.getNum() - 1][i]) + " | ");
		}
		bw.write("\n");

	}

	protected double getProbabilityOfWGivenX(Sample sample, State state) {
		// TODO Auto-generated method stub
		double probability = 1.0;
		
		for (int i=0; i<numOfFeatures; i++) {
			if(sample.getBinaryNumber(i) == 0)
				probability *= trainingEstimates[state.getNum() - 1][i];
			else 
				probability *= 1 - trainingEstimates[state.getNum() - 1][i];
		}
		
		return probability;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
