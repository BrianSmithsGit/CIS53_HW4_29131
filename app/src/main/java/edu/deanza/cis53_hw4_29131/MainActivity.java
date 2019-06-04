package edu.deanza.cis53_hw4_29131;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.deanza.cis53_hw4_29131.MainActivity.APP_TAG;

/*
    Program gets JSON object from internet, attempts to parse but gets JSON exception.
    Exception may be because I store the JSON in a String array but the array is
    of size 1. It is an array that holds a single String... Might been to split up each
    JSON object into a string. {"xxx":"xxx"}, == 1 string.



 */

public class MainActivity extends AppCompatActivity {

    public static String APP_TAG = "CIS53_HW5_29131";
    private ListView listView;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview_forecast);

        ArrayList<listItem> myList = new ArrayList<>();

        myList.add(new listItem("Monday", "Cloudy, 72"));
        myList.add(new listItem("Tuesday", "Sunny, 82"));
        myList.add(new listItem("Wednesday", "Partly Cloudy, 72"));
        myList.add(new listItem("Thursday", "Raining, 72"));
        myList.add(new listItem("Friday", "Thunderstorm, 72"));

        adapter = new CustomAdapter(this, myList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItem item = (listItem) listView.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Clicked on " + item.getmWeather(), Toast.LENGTH_SHORT).show();

                // TODO: Fix this shit
                // Intent / start activity makes the screen go black after attempting to launch.... fix later!
                //Intent intent = new Intent(view.getContext(), DetailActivity.class);
                //intent.putExtra("day", item.getmDay());
                //intent.putExtra("weather", item.getmWeather());
                //startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh)
        {
            Log.d(APP_TAG, "onOptionsItemSelected Refresh");
            String city = "San Jose,US";
            String days = "5";
            System.out.print("Item Selected.");
            Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
            new fetchWeatherTask().execute(city,days);
            return true;
        }
        else if(item.getItemId() == R.id.action_settings)
        {
            Log.d(APP_TAG, "onOptionsItemSelected Settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // API Key = 71a0d73c32a07c7a2359c554bd21f947
    class fetchWeatherTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... data) {

            Log.d(APP_TAG, "doInBackground called");
            // Sketchy conversion of String to String array!!!!
            // Necessary for return types.
            String[] weatherData = new String[] {getWeatherData(data[0], data[1])};
            try {
                weatherData = getWeatherDataFromJson(weatherData[0], 5);
            } catch (JSONException e)
            {
                Log.d(APP_TAG, "Json exception in doInBackground");
            }
            Log.d(APP_TAG, "weather data array = " + weatherData[0]);
            // Length = 1, may be fucking up the JSON object...
            return weatherData;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null)
            {
                Log.d(APP_TAG, "onPostExecute json = " + result[0]);
                adapter.clear();
                ArrayList<listItem> myList = new ArrayList<>();

                Log.d(APP_TAG, "res size =  " + result.length);


                for(int i = 0; i < result.length; i++) {
                    Log.d(APP_TAG, "weather data array = " + result[i]);
                }

                /*myList.add(new listItem("Monday", "Cloudy, 72"));
                myList.add(new listItem("Tuesday", "Sunny, 82"));
                myList.add(new listItem("Wednesday", "Partly Cloudy, 72"));
                myList.add(new listItem("Thursday", "Raining, 72"));
                myList.add(new listItem("Friday", "Thunderstorm, 72"));
                */
                /*
                adapter.clear();
                for(String dayForecastStr : result)
                {
                    adapter.add(dayForecastStr);
                }

                */

                /*mForecastAdapter.clear();
                for(String dayForecastStr : result)
                {
                    mForecastAdapter.add(dayForecastStr);
                }
                 */

            }
        }

        String getWeatherData(String city, String days) {
            Log.d(APP_TAG, "getWeatherData called");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                // BUILD URI object
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                // Put your APIKEY
                final String APPID = "71a0d73c32a07c7a2359c554bd21f947";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(QUERY_PARAM, city)
                        .appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNITS_PARAM, "imperial")
                        .appendQueryParameter(DAYS_PARAM, days)
                        .appendQueryParameter(APPID_PARAM, APPID)
                        .build();
                URL url = new URL(builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        forecastJsonStr = null;
                    } else
                        forecastJsonStr = buffer.toString();
                }
            } catch (IOException e) {
                Log.e(APP_TAG, "HTTP IO Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(APP_TAG, "HTTP Error in closing ", e);
                    }
                }
            }

            Log.d(APP_TAG, "json string = " + forecastJsonStr);
            return forecastJsonStr;
        }



        /* JSON Parsing code
         * Updated 5/13/19 5:00PM
         *
         */
        public String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        public String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        public String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_MAIN = "main";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_DAY = "day";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // TODO Size is wrong, conversion is bad? Thinks its thursday every time but the weather changes
            // WeatherArray size is 1 or so... its fucking this up
            String[] resultStrs = new String[numDays];
            for(int i = 0; i <= weatherArray.length(); i++) {
                Log.d(APP_TAG, "getWeather called" );
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);
                Log.d(APP_TAG, "dayForecast = " + dayForecast.toString() );

                JSONObject tmp = dayForecast.getJSONObject("main");
                double h = tmp.getDouble("temp_max");
                double l = tmp.getDouble("temp_min");
                double mid = tmp.getDouble("temp");

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".

                // Date time doesnt change much...


                // description is in a child array called "weather", which is 1 element long.

                JSONObject weatherObject = (JSONObject) dayForecast.getJSONArray(OWM_WEATHER).get(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);


                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                highAndLow = formatHighLows(h, l);

                resultStrs[i] = day + " - " + description + " - " + mid + " range:" + highAndLow;
                Log.d(APP_TAG, resultStrs[i]);

            }
            Log.d(APP_TAG, "getWeatherDataFromJson: ABOUT TO RETURN");
            return resultStrs;
        }

    }

}
class CustomAdapter extends ArrayAdapter<listItem> {

    private Context mContext;
    private List<listItem> myList = new ArrayList<>();


    public CustomAdapter(Context context, ArrayList<listItem> list) {
        super (context, 0, list);
        mContext = context;
        myList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list= convertView;
        if(list == null)
        {
            list = LayoutInflater.from(mContext).inflate(R.layout.list_item_forecast, parent, false);
        }
        listItem item = myList.get(position);

        TextView day = (TextView) list.findViewById(R.id.dayText);
        day.setText(item.getmDay());

        Log.d(APP_TAG, "getView: Weather text = " + item.getmWeather());
        TextView weather = (TextView) list.findViewById(R.id.weatherText);
        weather.setText(item.getmWeather());

        return list;
    }
}