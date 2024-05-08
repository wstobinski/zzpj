package pl.zzpj.solid.dip.weathertracker.solution;


public class WeatherTracker {
    String currentConditions;
    WeatherAlertGenerator weatherAlertGenerator;

    public WeatherTracker(WeatherAlertGenerator weatherAlertGenerator) {
       this.weatherAlertGenerator = weatherAlertGenerator;
    }

    public void setCurrentConditions(String weatherDescription) {
        this.currentConditions = weatherDescription;
        this.weatherAlertGenerator.generateWeatherAlert(this.currentConditions);
    }
}
