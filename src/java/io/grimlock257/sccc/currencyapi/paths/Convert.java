package io.grimlock257.sccc.currencyapi.paths;

import com.google.gson.Gson;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(
            @QueryParam("baseCurrency") String baseCurrency,
            @QueryParam("targetCurrency") String targetCurrency,
            @QueryParam("value") Double value
    ) {
        Double exchangeRate = getExchangeRate(baseCurrency, targetCurrency);

        if (exchangeRate != -1 && value != null) {
            Double convertedValue = value * exchangeRate;

            return "{ \"status\": \"success\", \"value\": " + convertedValue + " }";
        } else {
            return "{ \"status\": \"error\" }";
        }
    }

    private double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            String exchangeRates = new String(Files.readAllBytes(Paths.get("exchangeRates.json")));

            Gson gson = new Gson();
            Map<String, Double> exchangeRatesMap = gson.fromJson(exchangeRates, TreeMap.class);

            double baseCurrencyRate = exchangeRatesMap.get(baseCurrency);
            double targetCurrencyRate = exchangeRatesMap.get(targetCurrency);

            return targetCurrencyRate / baseCurrencyRate;
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException while trying to read exchangeRates.json: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("[CurrencyAPI] NPE, likely currency key could not be found in exchange rate map: " + e.getMessage());
        }

        return -1;
    }
}
