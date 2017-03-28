package src;

public class Feature {

	private double probabilityOfDependentIsZero;
	private double probabilityOfDependentIsOne;
	
	public double getProbabilityOfDependentIsZero() {
		return probabilityOfDependentIsZero;
	}

	public void setProbability(double probability) {
		this.probabilityOfDependentIsZero = probability;
	}

	public double getProbabilityOfDependentIsOne() {
		return probabilityOfDependentIsOne;
	}

	public void setProbabilityOfDependentIsOne(double probabilityOfDependentIsOne) {
		this.probabilityOfDependentIsOne = probabilityOfDependentIsOne;
	}
	
	private double getProbability(int index, int dependentIndex){
		if(dependentIndex == -1) {
			if(index == 0)
				return probabilityOfDependentIsZero;
			else 
				return 1 - probabilityOfDependentIsOne;
		} else {
			if (dependentIndex == 0) {
				if(index == 0)
					return probabilityOfDependentIsZero;
				return 1 - probabilityOfDependentIsZero;
			} else {
				if(index == 0)
					return probabilityOfDependentIsOne;
				return 1 - probabilityOfDependentIsOne;
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
