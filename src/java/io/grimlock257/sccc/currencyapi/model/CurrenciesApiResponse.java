package io.grimlock257.sccc.currencyapi.model;

import java.util.TreeMap;

/**
 * CurrenciesApiResponse
 *
 * Represents a successful response that the external API can provide
 *
 * @author Adam Watson
 */
public class CurrenciesApiResponse {

    private TreeMap<String, CurrencyInfoApiResponse> results;

    public TreeMap<String, CurrencyInfoApiResponse> getResults() {
        return results;
    }
}
