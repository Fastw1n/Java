package markup;

import java.util.List;

public class Strikeout extends MarkAbst {
    public Strikeout(List<MarkAbst> elements) {
        super.list = elements;
    }
    public StringBuilder toMarkdown(StringBuilder string) {
        super.forward = "~";
        super.end = "~";
        return super.toMarkdown(string);

    }
    public StringBuilder toBBCode(StringBuilder string) {
        super.forward = "[s]";
        super.end = "[/s]";
        return super.toBBCode(string);

    }

}
