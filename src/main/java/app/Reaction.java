package app;

import java.util.Objects;

import javax.json.stream.JsonGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Reaction {

    private final Issue issue;
    private final Integer count;

    public Reaction(Issue issue, Integer count) {
        this.issue = issue;
        this.count = count;
    }

    public void writeJson(JsonGenerator gen) {
        issue.writeJson(gen);
        if (count != null) {
            gen.write("count", count);
        } else {
            gen.writeNull("count");
        }
    }

    public static Integer findReaction(String html) {
        Elements elements = Jsoup.parse(html).select("button.reaction-summary-item");
        for (Element element : elements) {
            Elements emoji = element.select("g-emoji");
            if (Objects.equals(emoji.attr("alias"), "+1")) {
                emoji.remove();
                return Integer.parseInt(element.text());
            }
        }
        return null;
    }
}
