package pl.zzpj.solid.dip.weathertracker.solution;

public class Phone implements WeatherAlertGenerator {
    @Override
    public String generateWeatherAlert(String weatherConditions) {
        return "It is " + weatherConditions;
    }
}
