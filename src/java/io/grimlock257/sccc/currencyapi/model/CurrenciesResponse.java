package io.grimlock257.sccc.currencyapi.model;

import java.util.TreeMap;

/**
 * CurrenciesResponse
 *
 * Represents a response that this web service can provide
 *
 * @author Adam Watson
 */
public class CurrenciesResponse {

    private final boolean success;
    private TreeMap currencies;

    /**
     * Create a CurrenciesResponse with a success result of false
     */
    public CurrenciesResponse() {
        this.success = false;
        this.currencies = null;
    }

    /**
     * Create a CurrenciesResponse with a success result of true containing the currencies TreeMap
     *
     * @param currencies The currencies map
     */
    public CurrenciesResponse(TreeMap currencies) {
        this.success = true;
        this.currencies = currencies;
    }
}
