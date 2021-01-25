package io.grimlock257.sccc.currencyapi.paths;

import com.google.gson.Gson;
import io.grimlock257.sccc.currencyapi.jobs.ExchangeRateJob;
import io.grimlock257.sccc.currencyapi.model.ConvertResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
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

    private final Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(
            @QueryParam("baseCurrency") String baseCurrency,
            @QueryParam("targetCurrency") String targetCurrency,
            @QueryParam("value") Double value
    ) {
        Double exchangeRate = getExchangeRate(baseCurrency.toUpperCase(), targetCurrency.toUpperCase());

        if (exchangeRate != -1 && value != null) {
            Double convertedValue;

            // LSE prices are listed in GBp (pence), so convert to pounds first as exchange rates are based on GBP
            if (baseCurrency.equals("GBp")) {
                convertedValue = (value / 100) * exchangeRate;
            } else {
                convertedValue = value * exchangeRate;
            }

            return gson.toJson(new ConvertResponse(convertedValue));
        } else {
            return gson.toJson(new ConvertResponse());
        }
    }

    /**
     * Retrieve exchange rate from local JSON file
     *
     * @param baseCurrency The currency to convert from
     * @param targetCurrency The currency to convert to
     * @return The exchange rate between the two currencies, or -1 in the event of error
     */
    private double getExchangeRate(String baseCurrency, String targetCurrency) {
        String exchangeRates = null;

        // Try read the exchangeRates file, if fail, attempt a manual update
        try {
            exchangeRates = new String(Files.readAllBytes(Paths.get("./sharesBrokering/currency/exchangeRates.json")));
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException while trying to read exchangeRates.json: " + e.getMessage());
            System.err.println("[CurrencyAPI] Attemptting to initiate manual request for exchange rates...");

            ExchangeRateJob.getInstance().updateExchangeRates();
        }

        // Attempt to read the map and return the converted value
        try {
            Map<String, Double> exchangeRatesMap = gson.fromJson(exchangeRates, TreeMap.class);

            double baseCurrencyRate = exchangeRatesMap.get(baseCurrency);
            double targetCurrencyRate = exchangeRatesMap.get(targetCurrency);

            return targetCurrencyRate / baseCurrencyRate;
        } catch (NullPointerException e) {
            System.err.println("[CurrencyAPI] NPE, likely currency key could not be found in exchange rate map: " + e.getMessage());
        }

        return -1;
    }
}
