/**
 * ErrorReport.java
 *
 * An error reporting class for the Cool compiler.
 *
 * @author: Paul Elliott
 * @date: 4/11/13
 */
package main;
public class ErrorReport {
    
    private int line;
    private int column;
    private String message;
    private String value;

    public ErrorReport(int l, int c, String m, String v) {
        line = l;
        column = c;
        message = m;
        value = v;
        System.err.println("ERROR ("+message+") at line "+line+" col "+column+":");
        System.err.println("    "+value);
    }
}
