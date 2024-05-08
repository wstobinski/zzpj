package pl.zzpj.solid.dip.weathertracker.solution;


public class Emailer implements WeatherAlertGenerator {
    @Override
    public String generateWeatherAlert(String weatherConditions) {
        return "It is " + weatherConditions;
    }
}
