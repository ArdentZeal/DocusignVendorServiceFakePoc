package com.sap.sss.vendorfake.routers;

import com.sap.sss.vendorfake.docusign.DocusignService;
import com.sap.sss.vendorfake.models.SapEnvelope;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class EnvelopeRouter {

    @POST
    @Path("envelopes")
    public Response createEnvelope(SapEnvelope envelope)
    {
        try {
            DocusignService.getInstance().createEnvelope(envelope).execute();
            return Response.status(Response.Status.OK).entity(envelope).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }
}