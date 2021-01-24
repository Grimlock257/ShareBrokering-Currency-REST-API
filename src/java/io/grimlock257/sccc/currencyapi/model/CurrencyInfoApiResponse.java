package io.grimlock257.sccc.currencyapi.model;

/**
 * CurrenciesApiResponse
 *
 * Represents a successful response that the external API can provide
 *
 * @author Adam Watson
 */
public class CurrencyInfoApiResponse {

    private String currencyName;
    private String id;

    public String getCurrencyName() {
        return currencyName;
    }

    public String getId() {
        return id;
    }
}
