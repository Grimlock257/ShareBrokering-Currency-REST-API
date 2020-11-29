package io.grimlock257.sccc.currencyapi;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * ApplicationConfig
 *
 * Configuration for the rest web service
 *
 * @author Adam Watson
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically populated with all resources defined in the project. If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(io.grimlock257.sccc.currencyapi.Currencies.class);
    }

}
