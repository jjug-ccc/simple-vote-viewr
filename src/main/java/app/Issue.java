package app;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Issue {

    private final String title;
    private final Integer number;
    private final Integer reaction;

    public Issue(String title, Integer number, Integer reaction) {
        this.title = title;
        this.number = number;
        this.reaction = reaction;
    }

    public String getHtml(Client client) {
        String url = "https://github.com/jjug-ccc/call-for-paper-2016spring/issues/" + number;
        return client.target(url).request().header(HttpHeaders.USER_AGENT, "voteviewer")
                .get(String.class);
    }

    public void writeJson(JsonGenerator gen) {
        gen.writeStartObject();
        gen.write("title", title);
        gen.write("url", "https://github.com/jjug-ccc/call-for-paper-2016spring/issues/" + number);
        if (reaction != null) {
            gen.write("count", reaction);
        } else {
            gen.writeNull("count");
        }
        gen.writeEnd();
    }

    public static List<Issue> buildIssues() {
        Client client = ClientBuilder.newClient();

        List<Issue> issues = IntStream.rangeClosed(1, 40).mapToObj(number -> {
            String url = "https://github.com/jjug-ccc/call-for-paper-2016spring/issues/" + number;
            String html = client.target(url).request().header(HttpHeaders.USER_AGENT, "voteviewer")
                    .get(String.class);
            Document doc = Jsoup.parse(html);
            String title = doc.select("span.js-issue-title").text();
            Elements elements = doc.select("button.reaction-summary-item");
            Integer reaction = null;
            for (Element element : elements) {
                Elements emoji = element.select("g-emoji");
                if (Objects.equals(emoji.attr("alias"), "+1")) {
                    emoji.remove();
                    reaction = Integer.parseInt(element.text());
                }
            }
            return new Issue(title, number, reaction);
        }).collect(Collectors.toList());

        return issues;
    }
}
