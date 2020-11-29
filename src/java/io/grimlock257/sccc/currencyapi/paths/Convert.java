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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Logo
 *
 * Convert path of the API, will request exchange rate from an external RestAPI, and then convert the provided value into the new currency and return the new value
 *
 * @author Adam Watson
 */
@Path("convert")
public class Convert {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(
            @QueryParam("baseCurrency") String baseCurrency,
            @QueryParam("targetCurrency") String targetCurrency,
            @QueryParam("value") Double value
    ) {
        Double exchangeRate = requestExchangeRate(baseCurrency, targetCurrency);

        if (exchangeRate != null && value != null) {
            Double convertedValue = value * exchangeRate;

            return "{ \"status\": \"success\", \"value\": " + convertedValue + " }";
        } else {
            return "{ \"status\": \"error\" }";
        }
    }

    /**
     * Request exchange rate from the free currency API
     *
     * @return HashMap of currency code to currency name
     */
    private Double requestExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            // If either currency option is null, throw an NPE
            if (baseCurrency == null || targetCurrency == null) {
                throw new NullPointerException();
            }

            // Request components
            String baseUrl = "https://free.currconv.com/api/v7/convert";
            String apiKey = "your-api-key";
            String apiQueryParam = "?apiKey=" + apiKey;
            String compactQueryParam = "&compact=ultra";
            String currencyConversionString = (baseCurrency + "_" + targetCurrency).toUpperCase();
            String conversationQueryParam = "&q=" + currencyConversionString;

            // Create URL object
            URL url = new URL(baseUrl + apiQueryParam + compactQueryParam + conversationQueryParam);

            // Create HTTP connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // If the response was not a 200, throw an error
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }

            // Retrieve the connection input stream and store as a JsonObject
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonObject jsonObject = jsonReader.readObject();

            // Extract the exchangeRate from the response as a double
            Double exchangeRate = jsonObject.getJsonNumber(currencyConversionString).doubleValue();

            return exchangeRate;
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
