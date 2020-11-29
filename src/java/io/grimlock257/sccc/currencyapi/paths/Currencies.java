package io.grimlock257.sccc.currencyapi.paths;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        return "{ \"success\": true }";
    }
}
