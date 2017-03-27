package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public abstract class Classification {

	
	public abstract void classify(BufferedWriter bw, List<Sample> trainingSamples, List<Sample> testingSamples , State state,  int foldNum);
	public abstract void printConfusionMatrix(BufferedWriter bw, String string) throws IOException;
	
	public abstract void printAccuracy(BufferedWriter bw) throws IOException;
	public void assignAccuracy(int i, int j) {
		// TODO Auto-generated method stub
		
	}

}
