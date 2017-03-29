package src;

import java.util.ArrayList;
import java.util.List;

public class Sample {

	private int[] binaryFeatures;
	private double[] features;
	private int classNumber;
	
	public Sample(int numOfFeatures, int classNumber){
		binaryFeatures = new int[numOfFeatures];
		features = new double[numOfFeatures];
		this.classNumber = classNumber;
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

	public int getClassNumber() {
		return classNumber;
	}

	public void setClassNumber(int classNumber) {
		this.classNumber = classNumber;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
