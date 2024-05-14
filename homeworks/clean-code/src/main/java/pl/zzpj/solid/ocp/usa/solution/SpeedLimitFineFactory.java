package pl.zzpj.solid.ocp.usa.solution;

import java.util.HashMap;
import java.util.Map;

public class SpeedLimitFineFactory {
    private static final Map<String, SpeedLimitFineCalculator> calculators = new HashMap<>();

    static {
        calculators.put("SC", new SouthCarolinaFineCalculator());
        calculators.put("AL", new AlabamaFineCalculator());
        calculators.put("GA", new GeorgiaFineCalculator());
    }

    public static SpeedLimitFineCalculator getCalculator(String stateCode) {
        return calculators.get(stateCode);
    }
}
