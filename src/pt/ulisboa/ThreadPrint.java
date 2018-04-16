package pt.ulisboa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;


public class ThreadPrint extends PrintStream {
    private final ThreadLocal<PrintStream> current;

    public ThreadPrint(final PrintStream def) {
        super(new ByteArrayOutputStream());
        current = new ThreadLocal<PrintStream>() {
            @Override
            protected PrintStream initialValue() {
                return def;
            }
        };
    }

    public void setCurrent(PrintStream stream) {
        current.set(stream);
    }

    public void clearCurrent() {
        current.remove();
    }

    @Override
    public void flush() {
        current().flush();
    }

    @Override
    public void close() {
        current().close();
    }

    
    @Override
    public void println() {
        current().println();
    }

    @Override
    public void println(boolean x) {
        current().println(x);
    }

    @Override
    public void println(char x) {
        current().println(x);
    }

    @Override
    public void println(int x) {
        current().println(x);
    }

    @Override
    public void println(long x) {
        current().println(x);
    }

    @Override
    public void println(float x) {
        current().println(x);
    }

    @Override
    public void println(double x) {
        current().println(x);
    }

    @Override
    public void println(char[] x) {
        current().println(x);
    }

    @Override
    public void println(String x) {
        current().println(x);
    }

    @Override
    public void println(Object x) {
        current().println(x);
    }


    PrintStream current() {
        return current.get();
    }

}