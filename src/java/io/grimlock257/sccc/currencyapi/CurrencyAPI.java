package io.grimlock257.sccc.currencyapi;

import io.grimlock257.sccc.currencyapi.jobs.CurrenciesJob;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Adam Watson
 */
@Singleton
@Startup
public class CurrencyAPI {

    /**
     * This method will run before the web service container starts up
     */
    @PostConstruct
    public void construct() {
        CurrenciesJob.initiate();
    }

    /**
     * This method will run before the web service shuts down
     */
    @PreDestroy
    public void destroy() {
        CurrenciesJob.getInstance().cancel();
    }
}
