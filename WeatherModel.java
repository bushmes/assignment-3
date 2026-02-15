import java.util.Random;

public class WeatherModel {
    private static final Random rand = Randomizer.getRandom();

    private Weather currentWeather = Weather.SUNNY;

    // chance to change each step
    private static final double CHANGE_PROBABILITY = 0.05;

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void updateWeather() {
        if(rand.nextDouble() <= CHANGE_PROBABILITY) {
            // simple random change
            Weather[] values = Weather.values();
            currentWeather = values[rand.nextInt(values.length)];
        }
    }
}
