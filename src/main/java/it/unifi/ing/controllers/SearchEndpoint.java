package it.unifi.ing.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/search")
public class SearchEndpoint {
    

    @GET
    @Path("/products/{query}")
    public Response queryProducts(@PathParam("query") String query)
    {
        return Response.status(200).build();
    }

}
