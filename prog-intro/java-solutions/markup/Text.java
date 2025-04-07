package markup;

public class Text extends MarkAbst {
    public Text(String string) {
        super.text = string;
    }
    public StringBuilder toMarkdown(StringBuilder string) {
       string.append(super.text);
       return string;

    }

    public StringBuilder toBBCode(StringBuilder string) {
        string.append(super.text);
        return string;
    }

}
