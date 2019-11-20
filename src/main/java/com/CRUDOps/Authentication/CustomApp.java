package com.CRUDOps.Authentication;

import org.glassfish.jersey.server.ResourceConfig;

public class CustomApp extends ResourceConfig 
{
    public CustomApp() 
    {
        packages("com.CRUDOps.Authentication");
 
        //Register AuthenticationFilter here
        register(AuthenticationFilter.class);
    }
}