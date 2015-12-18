package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class BracketLeft {

    private int position;
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "<font size='4' color='#000000'>( </font>";
    }
}
