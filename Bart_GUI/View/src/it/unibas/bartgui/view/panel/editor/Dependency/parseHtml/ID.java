package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ID {
    
    private int position;
    private String openTag;
    private String closeTag;
    
    private String value;

    public ID(String value) {
        this.value = value;
    }

    /**
     * @return the openTag
     */
    public String getOpenTag() {
        return openTag = "<p><font size='5' color='#0077AA'><strong><em>";
    }

    /**
     * @return the closeTag
     */
    public String getCloseTag() {
        return closeTag = "</em></strong></font></p>";
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
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

    @Override
    public String toString() {
        return getOpenTag()+value+getCloseTag();
    }
 
    
}
