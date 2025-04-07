package markup;
import java.util.ArrayList;
import java.util.List;

public class Paragraph {

    private List<MarkAbst> list = new ArrayList<>();

    public Paragraph(List<MarkAbst> lst) {
        list = lst;
    }

    public void toMarkdown(StringBuilder str) {
        for (MarkAbst elem : list) {
            elem.toMarkdown(str);
        }

    }

    public void toBBCode(StringBuilder str) {
        for (MarkAbst elem : list) {
            elem.toBBCode(str);
        }
    }
}