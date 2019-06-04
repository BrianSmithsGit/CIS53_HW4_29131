package edu.deanza.cis53_hw4_29131;

public class listItem {
    // Holds the day of the week
    private String mDay;

    // Holds the weather data
    private String mWeather;

    public listItem(String day, String weather)
    {
        mDay = day;
        mWeather = weather;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public String getmWeather() {
        return mWeather;
    }

    public void setmWeather(String mWeather) {
        this.mWeather = mWeather;
    }
}
