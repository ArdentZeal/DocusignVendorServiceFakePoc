package com.sap.sss.vendorfake.routers;

import com.sap.sss.vendorfake.datastore.InMemoryDataStore;
import com.sap.sss.vendorfake.docusign.DocusignService;
import com.sap.sss.vendorfake.models.SapConnectedUsers;
import com.sap.sss.vendorfake.models.SapEnvelope;
import com.sap.sss.vendorfake.models.SapSetupNewTenantPayload;
import com.sap.sss.vendorfake.utiilities.CommonUtility;

import javax.ws.rs.HeaderParam;
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
    public Response createEnvelope(@HeaderParam("X-SAP-AA-UserId") String sapUserId, @HeaderParam("X-SAP-AA-TenantId") String sapTenantId, @HeaderParam("X-SAP-AA-Signature") String signature, SapEnvelope envelope)
    {
        Response authenticationResponse = CommonUtility.peformAndCheckAllAuthentication(sapUserId, sapTenantId, signature);
        if(authenticationResponse != null) {
            return authenticationResponse;
        }

        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);

        try {
            DocusignService.getInstance().createEnvelope(sapConnectedUsers.getDocusignAccessToken(), sapConnectedUsers.getDocusignAccountId(), envelope).execute();
            return Response.status(Response.Status.OK).entity(envelope).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }
}