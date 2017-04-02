package src;

import java.util.ArrayList;
import java.util.List;

public class State {
	private int num;
	private int featureLength;	
	private List<Sample> samples;
	private double[] actualProbabilities;
	
	public State (int num, int featureLength) {
		this.num = num;
		this.featureLength = featureLength;
		samples = new ArrayList<>();
		actualProbabilities = new double[featureLength];
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
