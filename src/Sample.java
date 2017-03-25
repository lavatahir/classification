import java.util.ArrayList;
import java.util.List;

public class Sample {

	private double[] binaryFeatures;
	private double[] features;
	public Sample(int numOfFeatures){
		binaryFeatures = new double[numOfFeatures];
		features = new double[numOfFeatures];
	}
	
	public void initializeFeatureWithBinaryValue(int index, double num) {
		binaryFeatures[index] = num;
	}
	
	public void initializeFeatureWithDecimalValue(int index, double num) {
		features[index] = num;
	}
	
	public double getProbability(int index) {
		return features[index];
	}
	
	public double getProbabilityForBinaryFeature(int index) {
		return binaryFeatures[index];
	}

	public double[] getBinaryFeatures() {
		return binaryFeatures;
	}

	public void setBinaryFeatures(double[] features) {
		this.binaryFeatures = features;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
