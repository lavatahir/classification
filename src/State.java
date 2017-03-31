package src;

import java.util.ArrayList;
import java.util.List;

public class State {
	private int num;
	private int featureLength;
	private double aprioriProbability;
	
	private List<Sample> samples;
	private double[] actualProbabilities;
	
	public State (int num, int featureLength, double aprioriProbability) {
		this.num = num;
		this.featureLength = featureLength;
		samples = new ArrayList<>();
		actualProbabilities = new double[featureLength];
		this.aprioriProbability = aprioriProbability;
	}
	
	public void addActualProbability(int index, double probability) {
		actualProbabilities[index] = probability;
	}
	
	public double getActualProbability(int index) {
		return actualProbabilities[index];
	}
	
	public void addSample(Sample s) {
		samples.add(s);
	}
	
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}

	public double getAprioriProbability() {
		return aprioriProbability;
	}

	public void setAprioriProbability(double aprioriProbability) {
		this.aprioriProbability = aprioriProbability;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
