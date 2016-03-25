package app;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@ApplicationScoped
@Path("")
public class Endpoint {

    //    @GET
    //    @Produces(MediaType.TEXT_HTML)
    //    public String index() {
    //        return null;
    //    }

    private AtomicReference<List<Issue>> cache;

    @Resource
    private ManagedScheduledExecutorService executor;

    @PostConstruct
    public void init() {
        cache = new AtomicReference<>();
    }

    public void init(@Observes @Initialized(ApplicationScoped.class) Object context) {
        executor.scheduleAtFixedRate(() -> cache.set(Issue.buildIssues()), 0, 1, TimeUnit.HOURS);
    }

    @Path("issues")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
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
