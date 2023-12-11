import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGui(){
        //setup the gui title
        super("Weather App");

        //configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set the size of the gui
        setSize(450,650);

        //load the gui at the center of screen
        setLocationRelativeTo(null);

        //make the layout manager null to manually position the components within the gui
        setLayout(null);

        //prevent resize of the gui
        setResizable(false);

        addGuiComponent();

    }

    private void addGuiComponent() {
        //search field
        JTextField searchTextField=new JTextField();
        //set the location and size of the search
        searchTextField.setBounds(15,15,351,45);
        //change the font style and size
        searchTextField.setFont(new Font("Dialog",Font.PLAIN,24));

        add(searchTextField);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText= new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog",Font.BOLD,48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionDesc=new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //weather humidify description and Image
        JLabel humidityImage= new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        //windspeed image and desc
        JLabel windspeedImage= new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        JLabel windspeedDesc = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedDesc.setBounds(310,500,85,55);
        windspeedDesc.setFont(new Font("Dialog",Font.PLAIN,16));
        add(windspeedDesc);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        //change cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();
                //valid input - length greater than 0 after whitespace removed
                if(userInput.replaceAll(" ","").length()<=0){
                    return;
                }
                //retreive data from backend
                weatherData = WeatherApp.getWeatherData(userInput);
                //update GUI

                //update image according the weather condirion
                String weatherCondtion = (String) weatherData.get("weather_condition");
                switch (weatherCondtion){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                }
                //update weather condition text
                weatherConditionDesc.setText(weatherCondtion);
                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature+" C");
                //update humidity
                long humidity =(long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> "+humidity+"%</html>");
                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                windspeedDesc.setText("<html><b>Windspeed</b> "+windspeed+"km/h</html>");


            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath) {
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}
