package markup;
import java.util.ArrayList;
import java.util.List;

public abstract class MarkAbst implements Interface {
    protected String text = "";
    protected String forward = "";
    protected String end = "";
    protected List<MarkAbst> list = new ArrayList<>();
    public StringBuilder toMarkdown(StringBuilder string) {
        string.append(forward);
        for (MarkAbst i: list) {
            string = i.toMarkdown(string);
        }
        string.append(end);
        return string;
    }

    public StringBuilder toBBCode(StringBuilder string) {
        string.append(forward);
        for (MarkAbst i: list) {
            string = i.toBBCode(string);
        }
        string.append(end);
        return string;
   }
}
