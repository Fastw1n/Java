package markup;

import java.util.List;

public class Strong extends MarkAbst {
    public Strong(List<MarkAbst> elements) {
        super.list = elements;
    }
    public StringBuilder toMarkdown(StringBuilder string) {
        super.forward = "__";
        super.end = "__";
        return super.toMarkdown(string);

    }
    public StringBuilder toBBCode(StringBuilder string) {
        super.forward = "[b]";
        super.end = "[/b]";
        return super.toBBCode(string);

    }

}
