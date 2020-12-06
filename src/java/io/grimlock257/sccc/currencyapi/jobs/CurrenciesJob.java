package io.grimlock257.sccc.currencyapi.jobs;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * CurrenciesJob
 *
 * Singleton class to handle currency updater task
 *
 * @author Adam Watson
 */
public class CurrenciesJob {

    private static CurrenciesJob instance = null;

    private Timer currenciesUpdaterTimer;

    private final int CURRENCIES_INITIAL_DELAY = 0;
    private final int CURRENCIES_UPDATE_FREQUENCY = 12 * 60 * 60 * 1000;

    /**
     * CurrenciesJob constructor
     *
     * Private to enforce singleton behaviour
     */
    private CurrenciesJob() {
        setupCurrenciesUpdaterTask();
    }

    /**
     * Initiate the currencies job if not already
     */
    public static void initiate() {

        if (instance == null) {
            instance = new CurrenciesJob();
        }
    }

    /**
     * Get the instance of the CurrenciesJob singleton
     *
     * @return The instance of the CurrenciesJob
     */
    public static CurrenciesJob getInstance() {

        if (instance == null) {
            instance = new CurrenciesJob();
        }

        return instance;
    }

    /**
     * Set up currencies updater task to run every CURRENCIES_UPDATE_FREQUENCY (in seconds) to retrieve latest currencies from the remote web service
     */
    private void setupCurrenciesUpdaterTask() {
        currenciesUpdaterTimer = new Timer();

        currenciesUpdaterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("[CurrencyAPI] Updating currencies list...");

                String currencies = requestCurrencies();

                if (currencies != null) {
                    try (Writer fileWriter = new FileWriter("currencies.json")) {
                        fileWriter.write(currencies);

                        System.out.println("[CurrencyAPI] Currency list update successful");
                    } catch (IOException e) {
                        System.err.println("[CurrencyAPI] IO exception writing currencies.json: " + e.getMessage());
                    }
                } else {
                    System.err.println("[CurrencyAPI] Error retrieving updated currencies");
                }
            }
        }, CURRENCIES_INITIAL_DELAY, CURRENCIES_UPDATE_FREQUENCY);
    }

    /**
     * Forcefully cancel the timer task
     */
    public void cancel() {
        currenciesUpdaterTimer.cancel();
        currenciesUpdaterTimer.purge();
        currenciesUpdaterTimer = null;
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
            System.err.println("[CurrencyAPI] Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException connecting to URL: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("[CurrencyAPI] NPE: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("[CurrencyAPI] ClassCastException (results is likely null): " + e.getMessage());
        }

        return null;
    }
}
