package it.unibas.bartgui.controlegt;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.openide.ErrorManager;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class OutputWindow {
    private final static PrintStream stout = System.out;
    private final static PrintStream sterr = System.err;

    
    public static void openOutputWindowStream(final OutputWriter writer,final OutputWriter writerErr)   {
        try{           
            System.setOut(new PrintStream(new FilterOutputStream(new ByteArrayOutputStream()){
                        @Override
                        public void write(byte[] b, int off, int len) throws IOException {
                            String s = new String(b,off,len);
                            writer.print(s);
                        }

                        @Override
                        public void write(byte[] b) throws IOException {
                        String s = new String(b);
                        writer.print(s);
                        }
            }));
            System.setErr(new PrintStream(new FilterOutputStream(new ByteArrayOutputStream()){
                        @Override
                        public void write(byte[] b, int off, int len) throws IOException {
                            String s = new String(b,off,len);
                            writerErr.print(s);
                        }

                        @Override
                        public void write(byte[] b) throws IOException {
                        String s = new String(b);
                        writerErr.print(s);
                        }
            }));
        }catch(Exception ex)   {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public static void closeOutputWindowStream(final OutputWriter writer,final OutputWriter writerErr)   {
        try{
            writer.close();
            writerErr.close();
            System.setOut(stout);
            System.setErr(sterr);
        }catch(Exception ex)   {
            ErrorManager.getDefault().notify(ex);
        }
    }
}
