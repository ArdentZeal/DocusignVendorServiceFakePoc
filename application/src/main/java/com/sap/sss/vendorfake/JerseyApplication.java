package com.sap.sss.vendorfake;


import com.sap.sss.vendorfake.interceptors.GsonInterceptor;
import com.sap.sss.vendorfake.routers.EnvelopeRouter;
import com.sap.sss.vendorfake.routers.OAuthRouter;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;




@ApplicationPath("/api/v1")
public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        super();

        this.register(EnvelopeRouter.class);
        this.register(OAuthRouter.class);
        this.register(GsonInterceptor.class);
    }
}
