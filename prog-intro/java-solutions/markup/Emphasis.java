package markup;

import java.util.List;

public class Emphasis extends MarkAbst {
    public Emphasis(List<MarkAbst> elements) {
        super.list = elements;
    }
    public StringBuilder toMarkdown(StringBuilder string) {
        super.forward = "*";
        super.end = "*";
        return super.toMarkdown(string);

    }
    public StringBuilder toBBCode(StringBuilder string) {
        super.forward = "[i]";
        super.end = "[/i]";
        return super.toBBCode(string);

    }

}
