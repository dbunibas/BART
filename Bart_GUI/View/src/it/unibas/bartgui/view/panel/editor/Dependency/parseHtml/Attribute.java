package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class Attribute {

    private int position;
    private String openTag;
    private String closeTag;
    
    private int fontSize = 4;
    private String fontcolor = "#0000FF";
    
    private String value;

    public Attribute(String value) {
        this.value = value;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the openTag
     */
    public String getOpenTag() {
        return openTag = "<font size='4' color='#0000FF'>";
    }

    /**
     * @return the closeTag
     */
    public String getCloseTag() {
        return closeTag=" </font>";
    }

    @Override
    public String toString() {
        return getOpenTag()+value+getCloseTag();
    }

    /**
     * @return the fontSize
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return the fontcolor
     */
    public String getFontcolor() {
        return fontcolor;
    }

    /**
     * @param fontcolor the fontcolor to set
     */
    public void setFontcolor(String fontcolor) {
        this.fontcolor = fontcolor;
    }

    
    
}
