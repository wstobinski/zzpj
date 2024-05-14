package pl.zzpj.solid.ocp.usa.solution;

public class GeorgiaFineCalculator implements SpeedLimitFineCalculator {
    private static final int GA_MAX_SPEED = 65;

    @Override
    public double calculateFine(int speed) {
        if (speed > GA_MAX_SPEED) {
            // Implementacja obliczeń...
            return (speed - GA_MAX_SPEED) * 20;  // przykładowe obliczenie
        }
        return 0.0;
    }
}
