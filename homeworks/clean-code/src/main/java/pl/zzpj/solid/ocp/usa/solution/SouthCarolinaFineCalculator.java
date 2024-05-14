package pl.zzpj.solid.ocp.usa.solution;

public class SouthCarolinaFineCalculator implements SpeedLimitFineCalculator {
    private static final int SC_MAX_SPEED = 60;

    @Override
    public double calculateFine(int speed) {
        if (speed > SC_MAX_SPEED) {
            // Implementacja obliczeń...
            return (speed - SC_MAX_SPEED) * 10;  // przykładowe obliczenie
        }
        return 0.0;
    }
}
