package io.grimlock257.sccc.currencyapi.model;

import java.util.TreeMap;

/**
 * ExchangeRatesApiResponse
 *
 * Represents a successful response that the external API can provide
 *
 * @author Adam Watson
 */
public class ExchangeRatesApiResponse {

    private TreeMap rates;

    public TreeMap getRates() {
        return rates;
    }
}
