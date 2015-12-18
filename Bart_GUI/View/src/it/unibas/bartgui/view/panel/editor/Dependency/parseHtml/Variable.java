package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class Variable {

    private int position;
    private String openTag;
    private String closeTag;
    private String color;
    
    
    private String value;

    public Variable(String value,String color) {
        
        this.value = value;
        this.color = color;
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
        return openTag = "<font size='4' color='"+color+"'><em> ";
    }

    /**
     * @return the closeTag
     */
    public String getCloseTag() {
        return closeTag="</em></font>";
    }

    @Override
    public String toString() {
        return getOpenTag()+" "+getValue()+getCloseTag();
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }


    
    
}
