package io.grimlock257.sccc.currencyapi.paths;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Logo
 *
 * Currencies path of the API, will request currencies from an external RestAPI
 *
 * @author Adam Watson
 */
@Path("currencies")
public class Currencies {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        String currencies = requestCurrencies();

        if (currencies != null) {
            return currencies;
        } else {
            return "{ \"status\": \"error\" }";
        }
    }

    /**
     * Request currencies from the free currency API
     *
     * @return HashMap of currency code to currency name
     */
    private String requestCurrencies() {
        try {
            // Request components
            String baseUrl = "https://free.currconv.com/api/v7/currencies";
            String apiKey = "your-api-key";
            String apiQueryParam = "?apiKey=" + apiKey;

            // Create URL object
            URL url = new URL(baseUrl + apiQueryParam);

            // Create HTTP connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // If the response was not a 200, throw an error
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }

            // Retrieve the connection input stream and store as a JsonObject
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonObject jsonObject = jsonReader.readObject();
            JsonObject jsonResults = jsonObject.getJsonObject("results");

            // TreeMap<String, String> to store currencyCode : currency name in an ordered format
            Map<String, String> currencyMap = new TreeMap<>();

            // Iterate over the resutls object, and add the key and currencyName field to the currencyMap
            for (String key : jsonResults.keySet()) {
                String value = jsonResults.getJsonObject(key).getString("currencyName");

                currencyMap.put(key, value);
            }

            // Using GSON to convert our Map<String, String> to a JSON representation
            Gson gson = new Gson();
            String currencyMapJson = gson.toJson(currencyMap);

            return currencyMapJson;
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException connecting to URL: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("NPE: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("ClassCastException (results is likely null): " + e.getMessage());
        }

        return null;
    }
}
