package ma.devoxx.langchain4j.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@ApplicationScoped
@RegisterRestClient(baseUri = "https://alphafold.ebi.ac.uk/api")
public interface AlphaFoldProxy {

    @GET
    @Path("/prediction/{qualifier}")
    @Produces(MediaType.APPLICATION_JSON)
    List<AlphaFoldResponse> getPrediction(@PathParam("qualifier") String uniProt, @QueryParam("key") String apiKey);
}
