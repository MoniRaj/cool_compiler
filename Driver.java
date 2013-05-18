/**
 * Driver.java
 *
 * The main Driver for the Scanner portion of the Cool compiler.
 * Takes one argument: <program> a Cool program to scan.
 * Compile via: javac Driver.java Scanner.java
 * Run via: java Driver <program>
 *
 * @author: Paul Elliott
 * @date: 4/11/13
 */

import ast.*;
import beaver.*;
import java.io.*;

public class Driver {
    public static void main(String[] argv) 
      throws IOException, CoolParser.Exception {
        try {
            if ( argv.length != 1 )
                throw new Error( "Usage: java Driver <program>" );
            System.out.println( "Lexing: "+argv[ 0 ] );
            CoolParser parser = new CoolParser();
            CoolScanner scanner = new CoolScanner( new FileInputStream( argv[ 0 ] ) );
            Program program = (Program) parser.parse( scanner );
            System.out.println( "Parsed: "+argv[ 0 ]+" with no errors" );
            TreeWalker walker = new TreeWalker();
            program.accept(walker);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "
                    + exception.toString() );
            exception.printStackTrace();
        }
    }
}
