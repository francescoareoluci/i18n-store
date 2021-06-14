package it.unifi.ing.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/search")
public class SearchEndpoint {

    @GET
    @Path("/test")
    public Response test()
    {
        return Response.ok().entity("Service online").build();
    }
}
