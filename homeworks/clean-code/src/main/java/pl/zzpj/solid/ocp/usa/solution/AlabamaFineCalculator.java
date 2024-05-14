package pl.zzpj.solid.ocp.usa.solution;

public class AlabamaFineCalculator implements SpeedLimitFineCalculator {
    private static final int AL_MAX_SPEED = 70;

    @Override
    public double calculateFine(int speed) {
        if (speed > AL_MAX_SPEED) {
            // Implementacja obliczeń...
            return (speed - AL_MAX_SPEED) * 15;  // przykładowe obliczenie
        }
        return 0.0;
    }
}

