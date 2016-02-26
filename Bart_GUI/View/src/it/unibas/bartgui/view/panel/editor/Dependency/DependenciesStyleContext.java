package it.unibas.bartgui.view.panel.editor.Dependency;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DependenciesStyleContext extends DefaultStyledDocument   {
    
    private final SimpleAttributeSet attribute;
    private final SimpleAttributeSet attributeAlias;
    private final SimpleAttributeSet operator;
    private final SimpleAttributeSet number;
    private final SimpleAttributeSet strAtt;
    private final SimpleAttributeSet normal;   
    private final SimpleAttributeSet comment;
    
    private final Color colorAttAlias;
    private final Color colorstrAtt;
    private final Color colorOperator;
    private final Color colorNumber;

    public DependenciesStyleContext() {
        colorAttAlias = new Color(133, 0, 159);
        colorstrAtt = new Color(179, 89, 0);
        colorOperator = new Color(0, 135, 34);
        colorNumber = new Color(204, 0, 0);
        
        attribute = new SimpleAttributeSet();
        attribute.addAttribute(StyleConstants.Foreground, Color.BLUE);
        attribute.addAttribute(StyleConstants.Italic, false);
        attribute.addAttribute(StyleConstants.Bold, false);
        attribute.addAttribute(StyleConstants.FontSize, new Integer(15));
        
        attributeAlias = new SimpleAttributeSet();
        attributeAlias.addAttribute(StyleConstants.Foreground, colorAttAlias);
        attributeAlias.addAttribute(StyleConstants.Italic, true);
        attributeAlias.addAttribute(StyleConstants.Bold, false);
        attributeAlias.addAttribute(StyleConstants.FontSize, new Integer(15));
        
        operator = new SimpleAttributeSet();
        operator.addAttribute(StyleConstants.Foreground, colorOperator);
        operator.addAttribute(StyleConstants.Italic, false);
        operator.addAttribute(StyleConstants.Bold, true);
        operator.addAttribute(StyleConstants.FontSize, new Integer(15));
        
        normal = new SimpleAttributeSet();
        normal.addAttribute(StyleConstants.Foreground, Color.BLACK);
        normal.addAttribute(StyleConstants.Italic, false);
        normal.addAttribute(StyleConstants.Bold, false);
        normal.addAttribute(StyleConstants.FontSize, new Integer(15));
        
        number = new SimpleAttributeSet();
        number.addAttribute(StyleConstants.Foreground, colorNumber);
        number.addAttribute(StyleConstants.Italic, false);
        number.addAttribute(StyleConstants.Bold, true);
        number.addAttribute(StyleConstants.FontSize, new Integer(15));
       
        strAtt = new SimpleAttributeSet();
        strAtt.addAttribute(StyleConstants.Foreground, colorstrAtt);
        strAtt.addAttribute(StyleConstants.Italic, false);
        strAtt.addAttribute(StyleConstants.Bold, true);
        strAtt.addAttribute(StyleConstants.FontSize, new Integer(15));
        
        comment = new SimpleAttributeSet();
        comment.addAttribute(StyleConstants.Foreground, Color.DARK_GRAY);
        comment.addAttribute(StyleConstants.Italic, false);
        comment.addAttribute(StyleConstants.Bold, false);
        comment.addAttribute(StyleConstants.FontSize, new Integer(15));
    
    }
    
    
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
        String text = getText(0, getLength());
        int before = findLastNonWordChar(text, offs);
        if (before < 0) before = 0;
        int after = findFirstNonWordChar(text, offs + str.length());
        int wordL = before;
        int wordR = before;
        while (wordR <= after) {
            if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {  
                if (text.substring(wordL, wordR).matches("(\\W)*(\\\")")) {
                    setCharacterAttributes(wordL, wordR - wordL, strAtt, /*false*/true);
                }else if (text.substring(wordL, wordR).matches("(\\W)*(\\d+)")) {
                    setCharacterAttributes(wordL, wordR - wordL, number, /*false*/true);
                }else if (text.substring(wordL, wordR).matches("(\\W)*(\\+|\\-|\\<|\\>|\\=|\\!)")) {
                    setCharacterAttributes(wordL, wordR - wordL, operator, /*false*/true);
                }else if (text.substring(wordL, wordR).matches("(\\W)*(\\$((\\w)*))"))  {
                    setCharacterAttributes(wordL, wordR - wordL, attributeAlias, /*false*/true);
                }else{
                    setCharacterAttributes(wordL, wordR - wordL, normal, /*false*/true);
                }
                try{
                    if (text.substring(wordL, (wordR+1)).matches("(\\W)*(\\w)*(\\:)")) {
                        setCharacterAttributes(wordL, (wordR+1) - wordL, attribute, /*false*/true);
                    }
                }catch(Exception ex){}
                try{
                    if (text.substring(wordL, (wordR+1)).matches("(\\W)*(\\\")(.*)(\\\")")) {
                        setCharacterAttributes(wordL, (wordR+1) - wordL, strAtt, /*false*/true);
                    }
                }catch(Exception ex){}
                wordL = wordR;
            }	
            wordR++;
        }
        findComment(text);
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        
        String text = getText(0, getLength());
        int before = findLastNonWordChar(text, offs);
        if (before < 0) before = 0;
        int after = findFirstNonWordChar(text, offs);
        if (text.substring(before, after).matches("(\\W)*(\\\")")) {
            setCharacterAttributes(before, after - before, strAtt, /*false*/true);      
        }else if (text.substring(before, after).matches("(\\W)*(\\d+)")) {
            setCharacterAttributes(before, after - before, number, /*false*/true);
        }else if (text.substring(before, after).matches("(\\W)*(\\+|\\-|\\<|\\>|\\=|\\!)")) {
            setCharacterAttributes(before, after - before, operator, /*false*/true);
        } else if (text.substring(before, after).matches("(\\W)*(\\$((\\w)*))"))  {
            setCharacterAttributes(before, after - before, attributeAlias, /*false*/true);
        }else{
            setCharacterAttributes(before, after - before, normal, /*false*/true);
        }
        try{
            if (text.substring(before, (after+1)).matches("(\\W)*(\\w)*(\\:)")) {
                setCharacterAttributes(before, (after+1) - before, attribute, /*false*/true);
            }
        }catch(Exception ex){}
        try{
            if (text.substring(before, (after+1)).matches("(\\W)*(\\\")(.*)(\\\")")) {
                setCharacterAttributes(before, (after+1) - before, strAtt, /*false*/true);
            }
        }catch(Exception ex){}
        findComment(text);
    }
    
    private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
    
    private void findComment(String text)   {
        String line[] = text.split("\\n");
        int offs = 0;
        for(String l : line)   {          
            if(l.matches("(\\s)*(//)(.*)"))   {
                setCharacterAttributes(offs, l.length(), comment, /*false*/true);
            }
            offs = offs+l.length()+1;
        }
        
    }
}
