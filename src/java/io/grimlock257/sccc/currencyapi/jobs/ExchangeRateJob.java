package io.grimlock257.sccc.currencyapi.jobs;

import com.google.gson.Gson;
import io.grimlock257.sccc.currencyapi.model.ExchangeRatesApiResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ExchangeRateJob
 *
 * Singleton class to handle exchange rate updater task
 *
 * @author Adam Watson
 */
public class ExchangeRateJob {

    private static ExchangeRateJob instance = null;

    private Timer exchangeRateUpdaterTimer;

    private final int EXCHANGE_RATE_UPDATE_INITIAL_DELAY = 0;
    private final int EXCHANGE_RATE_UPDATE_FREQUENCY = 60 * 60 * 1000;

    private final Gson gson = new Gson();

    /**
     * ExchangeRateJob constructor
     *
     * Private to enforce singleton behaviour
     */
    private ExchangeRateJob() {
        setupCurrenciesUpdaterTask();
    }

    /**
     * Initiate the currencies job if not already
     */
    public static void initiate() {

        if (instance == null) {
            instance = new ExchangeRateJob();
        }
    }

    /**
     * Get the instance of the ExchangeRateJob singleton
     *
     * @return The instance of the ExchangeRateJob
     */
    public static ExchangeRateJob getInstance() {

        if (instance == null) {
            instance = new ExchangeRateJob();
        }

        return instance;
    }

    /**
     * Set up exchange rate updater task to run every EXCHANGE_RATE_UPDATE_FREQUENCY (in seconds) to retrieve latest exchange rates from the remote web service
     */
    private void setupCurrenciesUpdaterTask() {
        exchangeRateUpdaterTimer = new Timer();

        exchangeRateUpdaterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateExchangeRates();
            }
        }, EXCHANGE_RATE_UPDATE_INITIAL_DELAY, EXCHANGE_RATE_UPDATE_FREQUENCY);
    }

    /**
     * Forcefully cancel the timer task
     */
    public void cancel() {
        exchangeRateUpdaterTimer.cancel();
        exchangeRateUpdaterTimer.purge();
        exchangeRateUpdaterTimer = null;
    }

    /**
     * Request exchange rates from the external API and store the result
     */
    public void updateExchangeRates() {
        System.out.println("[CurrencyAPI] Updating exchange rates list...");

        String exchangeRates = requestExchangeRate();

        if (exchangeRates != null) {
            // Create the folder if it doesn't already exist
            File file = new File("./sharesBrokering/currency/exchangeRates.json");
            file.getParentFile().mkdirs();

            try (Writer fileWriter = new FileWriter(file)) {
                fileWriter.write(exchangeRates);

                System.out.println("[CurrencyAPI] Exchange rate list update successful");
            } catch (IOException e) {
                System.err.println("[CurrencyAPI] IO exception writing exchangeRates.json: " + e.getMessage());
            }
        } else {
            System.err.println("[CurrencyAPI] Error retrieving updated exchange rates");
        }
    }

    /**
     * Request exchange rates from the free currency API (has a base of USD)
     *
     * @return HashMap of currency code to rate against USD
     */
    private String requestExchangeRate() {
        try {
            // Request components
            String baseUrl = "https://openexchangerates.org/api/latest.json";
            String apiKey = "your-api-key";
            String apiQueryParam = "?app_id=" + apiKey;

            // Create URL object
            URL url = new URL(baseUrl + apiQueryParam);

            // Create HTTP connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // If the response was not a 200, throw an error
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }

            // Deserialise the JSON response and extract the "rates" field for return
            ExchangeRatesApiResponse exchangeRatesApiResponse = gson.fromJson(new InputStreamReader(conn.getInputStream()), ExchangeRatesApiResponse.class);

            return gson.toJson(exchangeRatesApiResponse.getRates());
        } catch (MalformedURLException e) {
            System.err.println("[CurrencyAPI] Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException connecting to URL: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("[CurrencyAPI] NPE: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("[CurrencyAPI] ClassCastException (rates is likely null): " + e.getMessage());
        }

        return null;
    }
}
