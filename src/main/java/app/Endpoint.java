package app;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("")
public class Endpoint {

    //    @GET
    //    @Produces(MediaType.TEXT_HTML)
    //    public String index() {
    //        return null;
    //    }

    @Path("issues")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput get() {
        Client client = ClientBuilder.newClient();

        List<Issue> issues = new ArrayList<>();
        Link link = Link
                .fromUri("https://api.github.com/repos/jjug-ccc/call-for-paper-2016spring/issues")
                .build();
        do {
            Response resp = client.target(link).request()
                    .header(HttpHeaders.USER_AGENT, "voteviewer").get();
            String json = resp.readEntity(String.class);
            try (JsonReader reader = Json.createReader(new StringReader(json))) {
                JsonArray array = reader.readArray();
                for (JsonValue element : array) {
                    JsonObject object = (JsonObject) element;
                    String title = object.getString("title");
                    int number = object.getInt("number");
                    issues.add(new Issue(title, number));
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(json, e);
            }
            link = resp.getLink("next");
        } while (link != null);

        List<Reaction> reactions = issues.stream().map(issue -> {
            String html = issue.getHtml(client);
            Integer count = Reaction.findReaction(html);
            return new Reaction(issue, count);
        }).collect(Collectors.toList());

        return out -> {
            try (JsonGenerator gen = Json.createGenerator(out)) {
                gen.writeStartArray();
                for (Reaction reaction : reactions) {
                    gen.writeStartObject();
                    reaction.writeJson(gen);
                    gen.writeEnd();
                }
                gen.writeEnd();
            }
        };
    }
}
