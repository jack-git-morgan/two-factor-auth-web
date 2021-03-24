package com.mycompany.two.factor.auth.web;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * @author jackw
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    /**
     * @return
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.mycompany.two.factor.auth.web.api.class);
    }
}
