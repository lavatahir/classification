package src;

import java.util.ArrayList;
import java.util.List;

public class Sample {

	private int[] binaryFeatures;
	private double[] features;
	public Sample(int numOfFeatures){
		binaryFeatures = new int[numOfFeatures];
		features = new double[numOfFeatures];
	}
	
	public void initializeFeatureWithBinaryValue(int index, int num) {
		binaryFeatures[index] = num;
	}
	
	public void initializeFeatureWithDecimalValue(int index, double num) {
		features[index] = num;
	}
	
	public double getProbability(int index) {
		return features[index];
	}
	
	public int getBinaryNumber(int index) {
		return binaryFeatures[index];
	}

	public int[] getBinaryFeatures() {
		return binaryFeatures;
	}

	public void setBinaryFeatures(int[] features) {
		this.binaryFeatures = features;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
