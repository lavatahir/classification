package src;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public abstract class Classification {
	public abstract void classify(BufferedWriter bw, List<Sample> trainingSamples, List<Sample> testingSamples , State state,  int foldNum);
	
	protected double getProbabilityOfWGivenX(Sample sample, State state) {
		// TODO Auto-generated method stub
		return 0;
	}

}
