package io.grimlock257.sccc.currencyapi.paths;

import com.google.gson.Gson;
import io.grimlock257.sccc.currencyapi.jobs.CurrenciesJob;
import io.grimlock257.sccc.currencyapi.model.CurrenciesResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
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

    private final Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        TreeMap currencies = getCurrencies();

        if (currencies != null) {
            return gson.toJson(new CurrenciesResponse(currencies));
        } else {
            return gson.toJson(new CurrenciesResponse());
        }
    }

    /**
     * Retrieve currencies from local JSON file
     */
    private TreeMap getCurrencies() {
        TreeMap currencies = null;

        // Try read the currencies file, if fail, attempt a manual update
        try {
            currencies = gson.fromJson(new String(Files.readAllBytes(Paths.get("./sharesBrokering/currency/currencies.json"))), TreeMap.class);
        } catch (IOException e) {
            System.err.println("[CurrencyAPI] IOException while trying to read currencies.json: " + e.getMessage());
            System.err.println("[CurrencyAPI] Attemptting to initiate manual request for currencies...");

            CurrenciesJob.getInstance().updateCurrencies();
        }

        return currencies;
    }
}
