package io.grimlock257.sccc.currencyapi.paths;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        String currencies = getCurrencies();

        if (currencies != null) {
            return currencies;
        } else {
            return "{ \"status\": \"error\" }";
        }
    }

    /**
     * Retrieve currencies from local JSON file
     */
    private String getCurrencies() {
        try {
            return new String(Files.readAllBytes(Paths.get("currencies.json")));
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException while trying to read currencies.json: " + e.getMessage());
        }

        return null;
    }
}
