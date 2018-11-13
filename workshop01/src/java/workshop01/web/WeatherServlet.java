package workshop01.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

@WebServlet(urlPatterns = {"/weather"})
public class WeatherServlet extends HttpServlet {
    
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String APPID = "36c0e40ec18be828447d21aedc4347a1";
    private Client client;
    
    //Initialize the client
    @Override
    public void init(ServletConfig config) throws ServletException {
        client = ClientBuilder.newClient();
    }
    
    //Cleanup
    @Override
    public void destroy() {
        client.close();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Form field name
        String cityName = req.getParameter("cityName");
        
        //If city name not provide,  then set default to Singapore
        if((cityName == null) || (cityName.trim().length() <= 0))
        {
            cityName = "Singapore";
        }
        
        //Create the target and set the query string
        WebTarget target = client.target(WEATHER_URL)
                .queryParam("q", cityName)
                .queryParam("appid", APPID)
                .queryParam("units", "metric");
        
        //Create invocation, expect Json as result
        Invocation.Builder inv = target.request(MediaType.APPLICATION_JSON);
        
        //Make the call using GET HTTP method
        //Result will be null if there are errors
        JsonObject result = inv.get(JsonObject.class);
        JsonArray weatherDetails = result.getJsonArray("weather");
        
        //Echo back the city name
        //Set the 200 status code
        resp.setStatus(HttpServletResponse.SC_OK);
        
        //Set the content type/media type
        //We are return text/html
        resp.setContentType(MediaType.TEXT_HTML);
        
        try (PrintWriter pw =  resp.getWriter()){
            pw.print("<h2>The weather for " + cityName.toUpperCase() + "</h2>");
            for (int i=0; i < weatherDetails.size(); i++){
                JsonObject wd = weatherDetails.getJsonObject(i);
                String main = wd.getString("main");
                String description = wd.getString("description");
                String icon = wd.getString("icon");
                pw.print("<div>");
                pw.print(main + " &dash; " + description);
                pw.printf("<img src=\"http://openweathermap.org/img/w/%s.png\">", icon);
                pw.print("</div>");
            }
        }       
    }
}
