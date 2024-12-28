package ma.devoxx.langchain4j.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class StaticResource {

    @GET
    public Response serveHomePage() {
        return Response.temporaryRedirect(java.net.URI.create("/index_state_machine.html")).build();
    }
}