package app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@Component
@Path("")
public class Endpoint {
    private AtomicReference<List<Issue>> cache = new AtomicReference<>(Collections.emptyList());


    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateCache() {
        System.out.println("cache");
        cache.set(Issue.buildIssues());
        System.out.println("done");
    }

    @Path("issues")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public StreamingOutput get() {
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
