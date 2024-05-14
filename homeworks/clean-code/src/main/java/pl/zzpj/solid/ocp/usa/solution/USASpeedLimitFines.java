package pl.zzpj.solid.ocp.usa.solution;

public class USASpeedLimitFines {

	public double calculateSpeedLimitFine(String stateCode, int speed) {
		SpeedLimitFineCalculator calculator = SpeedLimitFineFactory.getCalculator(stateCode);
		if (calculator != null) {
			return calculator.calculateFine(speed);
		}
		throw new IllegalArgumentException("No fine calculator available for state: " + stateCode);
	}

}
