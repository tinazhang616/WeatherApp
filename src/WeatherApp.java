import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//retreive weather data from API -this backend logic will fetch the latest weaher
//data from the external API and return it.The GUI will
//display this data to the USER
public class WeatherApp {
    //fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        //get location coordinates using the getlocation API
        JSONArray locationData = getLocationData(locationName);
        //extract latitude and longitude data
        JSONObject location =(JSONObject) locationData.get(0);
        double latitude=(double) location.get("latitude");
        double longitude=(double) location.get("longitude");

        String urlString ="https://api.open-meteo.com/v1/forecast?latitude="+
                latitude+"&longitude="+longitude+
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,soil_temperature_0cm&timezone=America%2FLos_Angeles";
        try{
            //call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);
            //check for response status
            //200-connection success
            if(conn.getResponseCode()!=200){
                System.out.println("Error: Could not connect to API");
                return null;
            }
            //store resulting json data;
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read and store into the string builder
                resultJson.append(scanner.nextLine());
            }
            //close scanner and disconnect url
            scanner.close();
            conn.disconnect();
            //parse through the data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            //retreive hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            //get current time data,use index to find current time
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexofCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double)  temperatureData.get(index);

            //get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));
            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);
            //get windspeed
            JSONArray windspeedData =(JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //organize temperature/weather condition/humidity/windspeed and use in the front end
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature",temperature);
            weatherData.put("weather_condition",weatherCondition);
            weatherData.put("humidity",humidity);
            weatherData.put("windspeed",windspeed);

            return weatherData;


        }catch(Exception e){
            e.printStackTrace();
        }



        return null;
        
    }
//convert the weather code to something more readble
    private static String convertWeatherCode(long weathercode) {
        String weatherCodition="";
        if(weathercode == 0L){
            //clear
            weatherCodition="Clear";
        }else if(weathercode>0L && weathercode<-3L){
            //cloudy
            weatherCodition="Cloudy";
        }else if((weathercode>=51L&&weathercode<=67L)||
                (weathercode>=80L&&weathercode<=99L)){
            //rain
            weatherCodition="Rain";
        }else if(weathercode>=71L&&weathercode<=77L){
            //snow
            weatherCodition="Snow";
        }
        return  weatherCodition;
    }

    private static int findIndexofCurrentTime(JSONArray time) {
        String currentTime = getCurrentTime();
        for(int i=0;i<time.size();i++){
            String arrTime = (String) time.get(i);
            if(arrTime.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return -1;
    }

    public static String getCurrentTime() {
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        //format the time according API (2023-12-01T00:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':'00");
        //format and print current time
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    //retreive geographic coordinates for give location name
    public static JSONArray getLocationData(String locationName) {
        //replace any whitespace in location name to +to adhere to API's request format;
        locationName = locationName.replaceAll(" ","+");
        //build API url with location parameter
        String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+
                locationName+"&count=10&language=en&format=json";
        try{
            HttpURLConnection conn = fetchApiResponse(urlString);
            //check response status, 200 means successful connection
            if(conn.getResponseCode()!=200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                //store API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scan = new Scanner(conn.getInputStream());
                //read and store the resulting json data into the string builder
                while(scan.hasNext()){
                    resultJson.append(scan.nextLine());
                }
                //close scan and close url connection
                scan.close();
                conn.disconnect();
                //parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                //get the list of location data of the API generated from the location name
                JSONArray locationData= (JSONArray) resultsJsonObj.get("results");
                return locationData;

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set request method to get
            conn.setRequestMethod("GET");
            //connect to the API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
