package io.grimlock257.sccc.currencyapi.model;

/**
 * ConvertResponse
 *
 * Represents a response that this web service can provide
 *
 * @author Adam Watson
 */
public class ConvertResponse {

    private final boolean success;
    private final Double value;

    /**
     * Create a ConvertResponse with a success result of false
     */
    public ConvertResponse() {
        this.success = false;
        this.value = null;
    }

    /**
     * Create a ConvertResponse with a success result of true containing the converted value
     *
     * @param convertedValue The converted value to return to the caller
     */
    public ConvertResponse(double convertedValue) {
        this.success = true;
        this.value = convertedValue;
    }
}
