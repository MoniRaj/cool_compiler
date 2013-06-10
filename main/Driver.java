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
package main;
import typecheck.*;
import ast.*;
import beaver.*;
import java.io.*;

public class Driver {
    public static void main(String[] argv) 
      throws IOException, CoolParser.Exception,
               Environment.EnvironmentException, TreeWalker.TypeCheckException {
        boolean showCode = false;
        try {
            if ( argv.length == 0 )
                throw new Error( "Usage: java Driver <space-delimited list of programs>" );
            System.out.println( "Collating all source files into main.cool" );
            File collated_srcfile = new File( "main.cool" );
            FileWriter output = new FileWriter( collated_srcfile );
            for( int i = 0; i < argv.length; i++ )
            {
                BufferedReader br = new BufferedReader( new FileReader( argv[i] ) );
                String line;
                while ( ( line = br.readLine() ) != null )
                {
                    output.write(line + "\n");
                }
                br.close();
            }
            output.close();
            CoolParser parser = new CoolParser();
            CoolScanner scanner = new CoolScanner( new FileInputStream( "main.cool" ) );
            Program program = (Program) parser.parse( scanner );
            System.out.println( "Parsed: main.cool with no errors" );
            TreeWalker walker = new TreeWalker( program, true );
            program.accept( walker );
            boolean type_safe = walker.isTypeSafe();
            if (type_safe) {
                System.out.println("Type checking successful.");
                System.out.println("======================================");
                //Run Code Generation
                System.err.println("Beginning code generation...");
                final CodeGenerator codeGenerator = new CodeGenerator(
                        walker.getEnvironment(), true);
                final String code = codeGenerator.generateCode();
                System.err.println("Done generating code\n\n");
                FileWriter fw = new FileWriter("main.ll");
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(code);
                bw.close();
                if (showCode) {
                    //PE fix this maybe..probably not
                    System.err.println(code);
                }
            }
            else {
                System.err.println("Compilation failed: type check errors");
            }
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "
                    + exception.toString() );
            exception.printStackTrace();
        }
    }
}
