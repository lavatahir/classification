package src;

import java.util.Random;

public class Feature {

	private int dependentFeatureIndex;
	private int num;

	private double[][] dependentFeaturesProbability;

	public Feature(int num, int dependentFeatureIndex) {
		this.dependentFeatureIndex = dependentFeatureIndex;
		this.num = num;
		assignProbability();
	}

	private void assignProbability() {
		dependentFeaturesProbability = new double[2][2];
		Random random = new Random();
		if (dependentFeatureIndex != -1) {
			for (int i = 0; i < 2; i++) {
				dependentFeaturesProbability[i][0] = random.nextDouble();
				dependentFeaturesProbability[i][1] = 1 - dependentFeaturesProbability[i][0];
			}
		} else {
			dependentFeaturesProbability[0][0] = random.nextDouble();
			dependentFeaturesProbability[0][1] = 1 - dependentFeaturesProbability[0][0];
		}

	}

	public double getProbability(int index, int dependentIndex) {
		if (dependentIndex == -1) {
			if(index == 0)
				return dependentFeaturesProbability[0][0];
			else
				return dependentFeaturesProbability[0][1];

		}
		
		return dependentFeaturesProbability[dependentIndex][index];
	}

	public int getDependentFeatureIndex() {
		return dependentFeatureIndex;
	}

	public void setDependentFeatureIndex(int dependentFeatureIndex) {
		this.dependentFeatureIndex = dependentFeatureIndex;
	}

	public double[][] getDependentFeaturesProbability() {
		return dependentFeaturesProbability;
	}

	public void setDependentFeaturesProbability(double[][] dependentFeaturesProbability) {
		this.dependentFeaturesProbability = dependentFeaturesProbability;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
