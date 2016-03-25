package app;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@Path("")
public class Endpoint {

    //    @GET
    //    @Produces(MediaType.TEXT_HTML)
    //    public String index() {
    //        return null;
    //    }

    private static final AtomicReference<List<Issue>> cache = new AtomicReference<>();

    @Path("issues")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput get() {

        if (cache.get() == null) {
            cache.compareAndSet(null, Issue.buildIssues());
        }

        return out -> {
            try (JsonGenerator gen = Json.createGenerator(out)) {
                gen.writeStartArray();
                for (Issue issue : cache.get()) {
                    issue.writeJson(gen);
                }
                gen.writeEnd();
            }
        };
    }
}
