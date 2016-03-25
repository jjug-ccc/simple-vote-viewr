package app;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;

public class Issue {

    private final String title;
    private final Integer number;

    public Issue(String title, Integer number) {
        this.title = title;
        this.number = number;
    }

    public String getHtml(Client client) {
        String url = "https://github.com/jjug-ccc/call-for-paper-2016spring/issues/" + number;
        return client.target(url).request().get(String.class);
    }

    public void writeJson(JsonGenerator gen) {
        gen.write("title", title);
        gen.write("url", "https://github.com/jjug-ccc/call-for-paper-2016spring/issues/" + number);
    }
}
