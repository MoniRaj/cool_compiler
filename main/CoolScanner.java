/* The following code was generated by JFlex 1.4.3 on 6/4/13 5:08 PM */

/**
 * CoolScanner.java
 * 
 * This is a scanner generated by JFlex
 * for the COOL programming language.
 * 
 * @author: Paul Elliott
 * @date: 4/11/13
 */

package main;

import beaver.Symbol;
import beaver.Scanner;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 6/4/13 5:08 PM from the specification file
 * <tt>scanner.flex</tt>
 */
class CoolScanner extends Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int STRING = 2;
  public static final int YYINITIAL = 0;
  public static final int TRADITIONALCOMMENT = 6;
  public static final int MULTISTRING = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3, 3
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\11\1\5\1\2\1\0\1\5\1\1\16\11\4\0\1\5\1\51"+
    "\1\13\1\0\1\11\3\0\1\47\1\52\1\4\1\63\1\57\1\64"+
    "\1\61\1\3\1\6\11\7\1\53\1\55\1\62\1\56\1\60\2\0"+
    "\22\10\1\33\7\10\1\0\1\14\2\0\1\11\1\0\1\22\1\15"+
    "\1\24\1\26\1\35\1\21\1\42\1\25\1\30\1\40\1\41\1\31"+
    "\1\34\1\17\1\27\1\36\1\44\1\20\1\23\1\16\1\45\1\43"+
    "\1\46\1\12\1\32\1\37\1\50\1\0\1\54\1\0\41\11\2\0"+
    "\4\11\4\0\1\11\2\0\1\11\7\0\1\11\4\0\1\11\5\0"+
    "\27\11\1\0\37\11\1\0\u013f\11\31\0\162\11\4\0\14\11\16\0"+
    "\5\11\11\0\1\11\21\0\130\11\5\0\23\11\12\0\1\11\13\0"+
    "\1\11\1\0\3\11\1\0\1\11\1\0\24\11\1\0\54\11\1\0"+
    "\46\11\1\0\5\11\4\0\202\11\1\0\4\11\3\0\105\11\1\0"+
    "\46\11\2\0\2\11\6\0\20\11\41\0\46\11\2\0\1\11\7\0"+
    "\47\11\11\0\21\11\1\0\27\11\1\0\3\11\1\0\1\11\1\0"+
    "\2\11\1\0\1\11\13\0\33\11\5\0\3\11\15\0\4\11\14\0"+
    "\6\11\13\0\32\11\5\0\31\11\7\0\12\11\4\0\146\11\1\0"+
    "\11\11\1\0\12\11\1\0\23\11\2\0\1\11\17\0\74\11\2\0"+
    "\3\11\60\0\62\11\u014f\0\71\11\2\0\22\11\2\0\5\11\3\0"+
    "\14\11\2\0\12\11\21\0\3\11\1\0\10\11\2\0\2\11\2\0"+
    "\26\11\1\0\7\11\1\0\1\11\3\0\4\11\2\0\11\11\2\0"+
    "\2\11\2\0\3\11\11\0\1\11\4\0\2\11\1\0\5\11\2\0"+
    "\16\11\15\0\3\11\1\0\6\11\4\0\2\11\2\0\26\11\1\0"+
    "\7\11\1\0\2\11\1\0\2\11\1\0\2\11\2\0\1\11\1\0"+
    "\5\11\4\0\2\11\2\0\3\11\13\0\4\11\1\0\1\11\7\0"+
    "\17\11\14\0\3\11\1\0\11\11\1\0\3\11\1\0\26\11\1\0"+
    "\7\11\1\0\2\11\1\0\5\11\2\0\12\11\1\0\3\11\1\0"+
    "\3\11\2\0\1\11\17\0\4\11\2\0\12\11\1\0\1\11\17\0"+
    "\3\11\1\0\10\11\2\0\2\11\2\0\26\11\1\0\7\11\1\0"+
    "\2\11\1\0\5\11\2\0\10\11\3\0\2\11\2\0\3\11\10\0"+
    "\2\11\4\0\2\11\1\0\3\11\4\0\12\11\1\0\1\11\20\0"+
    "\2\11\1\0\6\11\3\0\3\11\1\0\4\11\3\0\2\11\1\0"+
    "\1\11\1\0\2\11\3\0\2\11\3\0\3\11\3\0\10\11\1\0"+
    "\3\11\4\0\5\11\3\0\3\11\1\0\4\11\11\0\1\11\17\0"+
    "\11\11\11\0\1\11\7\0\3\11\1\0\10\11\1\0\3\11\1\0"+
    "\27\11\1\0\12\11\1\0\5\11\4\0\7\11\1\0\3\11\1\0"+
    "\4\11\7\0\2\11\11\0\2\11\4\0\12\11\22\0\2\11\1\0"+
    "\10\11\1\0\3\11\1\0\27\11\1\0\12\11\1\0\5\11\2\0"+
    "\11\11\1\0\3\11\1\0\4\11\7\0\2\11\7\0\1\11\1\0"+
    "\2\11\4\0\12\11\22\0\2\11\1\0\10\11\1\0\3\11\1\0"+
    "\27\11\1\0\20\11\4\0\6\11\2\0\3\11\1\0\4\11\11\0"+
    "\1\11\10\0\2\11\4\0\12\11\22\0\2\11\1\0\22\11\3\0"+
    "\30\11\1\0\11\11\1\0\1\11\2\0\7\11\3\0\1\11\4\0"+
    "\6\11\1\0\1\11\1\0\10\11\22\0\2\11\15\0\72\11\4\0"+
    "\20\11\1\0\12\11\47\0\2\11\1\0\1\11\2\0\2\11\1\0"+
    "\1\11\2\0\1\11\6\0\4\11\1\0\7\11\1\0\3\11\1\0"+
    "\1\11\1\0\1\11\2\0\2\11\1\0\15\11\1\0\3\11\2\0"+
    "\5\11\1\0\1\11\1\0\6\11\2\0\12\11\2\0\2\11\42\0"+
    "\1\11\27\0\2\11\6\0\12\11\13\0\1\11\1\0\1\11\1\0"+
    "\1\11\4\0\12\11\1\0\42\11\6\0\24\11\1\0\6\11\4\0"+
    "\10\11\1\0\44\11\11\0\1\11\71\0\42\11\1\0\5\11\1\0"+
    "\2\11\1\0\7\11\3\0\4\11\6\0\12\11\6\0\12\11\106\0"+
    "\46\11\12\0\51\11\7\0\132\11\5\0\104\11\5\0\122\11\6\0"+
    "\7\11\1\0\77\11\1\0\1\11\1\0\4\11\2\0\7\11\1\0"+
    "\1\11\1\0\4\11\2\0\47\11\1\0\1\11\1\0\4\11\2\0"+
    "\37\11\1\0\1\11\1\0\4\11\2\0\7\11\1\0\1\11\1\0"+
    "\4\11\2\0\7\11\1\0\7\11\1\0\27\11\1\0\37\11\1\0"+
    "\1\11\1\0\4\11\2\0\7\11\1\0\47\11\1\0\23\11\16\0"+
    "\11\11\56\0\125\11\14\0\u026c\11\2\0\10\11\12\0\32\11\5\0"+
    "\113\11\3\0\3\11\17\0\15\11\1\0\7\11\13\0\25\11\13\0"+
    "\24\11\14\0\15\11\1\0\3\11\1\0\2\11\14\0\124\11\3\0"+
    "\1\11\3\0\3\11\2\0\12\11\41\0\3\11\2\0\12\11\6\0"+
    "\130\11\10\0\52\11\126\0\35\11\3\0\14\11\4\0\14\11\12\0"+
    "\50\11\2\0\5\11\u038b\0\154\11\224\0\234\11\4\0\132\11\6\0"+
    "\26\11\2\0\6\11\2\0\46\11\2\0\6\11\2\0\10\11\1\0"+
    "\1\11\1\0\1\11\1\0\1\11\1\0\37\11\2\0\65\11\1\0"+
    "\7\11\1\0\1\11\3\0\3\11\1\0\7\11\3\0\4\11\2\0"+
    "\6\11\4\0\15\11\5\0\3\11\1\0\7\11\17\0\4\11\32\0"+
    "\5\11\20\0\2\11\23\0\1\11\13\0\4\11\6\0\6\11\1\0"+
    "\1\11\15\0\1\11\40\0\22\11\36\0\15\11\4\0\1\11\3\0"+
    "\6\11\27\0\1\11\4\0\1\11\2\0\12\11\1\0\1\11\3\0"+
    "\5\11\6\0\1\11\1\0\1\11\1\0\1\11\1\0\4\11\1\0"+
    "\3\11\1\0\7\11\3\0\3\11\5\0\5\11\26\0\44\11\u0e81\0"+
    "\3\11\31\0\17\11\1\0\5\11\2\0\5\11\4\0\126\11\2\0"+
    "\2\11\2\0\3\11\1\0\137\11\5\0\50\11\4\0\136\11\21\0"+
    "\30\11\70\0\20\11\u0200\0\u19b6\11\112\0\u51a6\11\132\0\u048d\11\u0773\0"+
    "\u2ba4\11\u215c\0\u012e\11\2\0\73\11\225\0\7\11\14\0\5\11\5\0"+
    "\14\11\1\0\15\11\1\0\5\11\1\0\1\11\1\0\2\11\1\0"+
    "\2\11\1\0\154\11\41\0\u016b\11\22\0\100\11\2\0\66\11\50\0"+
    "\15\11\3\0\20\11\20\0\4\11\17\0\2\11\30\0\3\11\31\0"+
    "\1\11\6\0\5\11\1\0\207\11\2\0\1\11\4\0\1\11\13\0"+
    "\12\11\7\0\32\11\4\0\1\11\1\0\32\11\12\0\132\11\3\0"+
    "\6\11\2\0\6\11\2\0\6\11\2\0\3\11\3\0\2\11\3\0"+
    "\2\11\22\0\3\11\4\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\4\0\1\1\2\2\1\3\1\4\2\5\1\6\1\7"+
    "\1\10\21\7\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26"+
    "\2\27\1\30\1\0\2\26\4\31\1\2\1\32\1\0"+
    "\17\7\1\33\3\7\1\34\13\7\1\35\1\36\1\37"+
    "\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
    "\1\50\1\0\1\51\1\52\6\7\1\53\4\7\1\33"+
    "\7\7\1\54\13\7\1\55\3\7\1\56\1\7\1\57"+
    "\1\7\1\60\7\7\1\61\10\7\1\62\10\7\1\33"+
    "\1\7\1\63\1\64\2\7\1\65\4\7\1\66\1\67"+
    "\6\7\1\70\1\71";

  private static int [] zzUnpackAction() {
    int [] result = new int[190];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\65\0\152\0\237\0\324\0\u0109\0\324\0\u013e"+
    "\0\324\0\324\0\u0173\0\u01a8\0\u01dd\0\u0212\0\u0247\0\u027c"+
    "\0\u02b1\0\u02e6\0\u031b\0\u0350\0\u0385\0\u03ba\0\u03ef\0\u0424"+
    "\0\u0459\0\u048e\0\u04c3\0\u04f8\0\u052d\0\u0562\0\u0597\0\324"+
    "\0\324\0\324\0\324\0\324\0\324\0\324\0\u05cc\0\324"+
    "\0\324\0\u0601\0\324\0\324\0\u0636\0\u066b\0\324\0\324"+
    "\0\u06a0\0\u06d5\0\u070a\0\324\0\u073f\0\u0774\0\u07a9\0\u07de"+
    "\0\324\0\u0813\0\u0848\0\u087d\0\u08b2\0\u08e7\0\u091c\0\u0951"+
    "\0\u0986\0\u09bb\0\u09f0\0\u0a25\0\u0a5a\0\u0a8f\0\u0ac4\0\u0af9"+
    "\0\u0b2e\0\u01dd\0\u0b63\0\u0b98\0\u0bcd\0\u01dd\0\u0c02\0\u0c37"+
    "\0\u0c6c\0\u0ca1\0\u0cd6\0\u0d0b\0\u0d40\0\u0d75\0\u0daa\0\u0ddf"+
    "\0\u0e14\0\324\0\324\0\324\0\324\0\324\0\324\0\324"+
    "\0\324\0\324\0\324\0\324\0\324\0\u0e49\0\324\0\324"+
    "\0\u0e7e\0\u0eb3\0\u0ee8\0\u0f1d\0\u0f52\0\u0f87\0\u01dd\0\u0fbc"+
    "\0\u0ff1\0\u1026\0\u105b\0\u1090\0\u10c5\0\u10fa\0\u112f\0\u1164"+
    "\0\u1199\0\u11ce\0\u1203\0\u01dd\0\u1238\0\u126d\0\u12a2\0\u12d7"+
    "\0\u130c\0\u1341\0\u1376\0\u13ab\0\u13e0\0\u1415\0\u144a\0\u01dd"+
    "\0\u147f\0\u14b4\0\u14e9\0\u01dd\0\u151e\0\u01dd\0\u1553\0\u01dd"+
    "\0\u1588\0\u15bd\0\u15f2\0\u1627\0\u165c\0\u1691\0\u16c6\0\u01dd"+
    "\0\u16fb\0\u1730\0\u1765\0\u179a\0\u17cf\0\u1804\0\u1839\0\u186e"+
    "\0\u01dd\0\u18a3\0\u18d8\0\u190d\0\u1942\0\u1977\0\u19ac\0\u19e1"+
    "\0\u1a16\0\u1a4b\0\u1a80\0\u01dd\0\u01dd\0\u1ab5\0\u1aea\0\u01dd"+
    "\0\u1b1f\0\u1b54\0\u1b89\0\u1bbe\0\u01dd\0\u01dd\0\u1bf3\0\u1c28"+
    "\0\u1c5d\0\u1c92\0\u1cc7\0\u1cfc\0\u01dd\0\u01dd";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[190];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\5\1\6\1\7\1\10\1\11\1\7\1\12\1\13"+
    "\1\14\1\5\1\15\1\16\1\5\1\15\1\17\1\20"+
    "\1\21\1\22\1\23\1\24\1\25\1\15\1\26\1\27"+
    "\1\30\1\31\1\32\1\14\1\33\1\34\1\35\4\15"+
    "\1\36\2\15\1\37\1\40\1\41\1\42\1\43\1\44"+
    "\1\45\1\46\1\47\1\50\1\5\1\51\1\52\1\53"+
    "\1\54\1\55\1\56\1\57\10\55\1\60\1\61\50\55"+
    "\13\62\1\63\51\62\1\64\1\65\2\64\1\66\7\64"+
    "\1\67\50\64\67\0\1\7\65\0\1\70\1\71\66\0"+
    "\2\13\63\0\5\14\2\0\32\14\24\0\5\15\2\0"+
    "\32\15\31\0\1\72\57\0\5\15\2\0\3\15\1\73"+
    "\4\15\1\74\4\15\1\75\14\15\24\0\5\15\2\0"+
    "\5\15\1\76\12\15\1\77\7\15\1\100\1\15\24\0"+
    "\5\15\2\0\20\15\1\101\11\15\24\0\5\15\2\0"+
    "\5\15\1\102\4\15\1\103\1\104\16\15\24\0\5\15"+
    "\2\0\1\105\31\15\24\0\5\15\2\0\20\15\1\106"+
    "\7\15\1\107\1\15\24\0\5\15\2\0\5\15\1\110"+
    "\6\15\1\111\15\15\24\0\5\15\2\0\12\15\1\112"+
    "\5\15\1\113\11\15\24\0\5\15\2\0\1\114\25\15"+
    "\1\115\3\15\24\0\5\15\2\0\4\15\1\116\12\15"+
    "\1\117\12\15\24\0\5\15\2\0\5\15\1\120\24\15"+
    "\24\0\5\15\2\0\13\15\1\121\16\15\24\0\5\15"+
    "\2\0\5\15\1\122\24\15\24\0\4\15\1\123\2\0"+
    "\14\15\1\124\15\15\24\0\5\15\2\0\3\15\1\125"+
    "\1\15\1\126\24\15\24\0\5\15\2\0\5\15\1\127"+
    "\24\15\24\0\5\15\2\0\10\15\1\130\2\15\1\131"+
    "\16\15\74\0\1\132\1\0\1\133\62\0\1\134\6\0"+
    "\1\55\2\0\10\55\2\0\50\55\2\0\1\57\62\0"+
    "\2\135\1\0\3\135\1\136\4\135\1\137\1\140\1\141"+
    "\1\142\1\143\1\144\1\145\43\135\13\62\1\0\51\62"+
    "\13\0\1\146\53\0\1\64\65\0\1\147\67\0\1\64"+
    "\4\0\7\64\43\0\1\70\1\6\1\7\62\70\13\0"+
    "\1\150\57\0\5\15\2\0\5\15\1\151\7\15\1\112"+
    "\12\15\1\152\1\15\24\0\5\15\2\0\3\15\1\153"+
    "\7\15\1\154\16\15\24\0\5\15\2\0\21\15\1\155"+
    "\10\15\24\0\5\15\2\0\1\15\1\156\30\15\24\0"+
    "\5\15\2\0\31\15\1\157\24\0\5\15\2\0\14\15"+
    "\1\160\15\15\24\0\5\15\2\0\1\15\1\161\25\15"+
    "\1\162\2\15\24\0\5\15\2\0\14\15\1\163\15\15"+
    "\24\0\5\15\2\0\3\15\1\164\26\15\24\0\5\15"+
    "\2\0\2\15\1\165\27\15\24\0\5\15\2\0\6\15"+
    "\1\166\23\15\24\0\5\15\2\0\5\15\1\167\24\15"+
    "\24\0\5\15\2\0\21\15\1\170\10\15\24\0\5\15"+
    "\2\0\1\15\1\171\4\15\1\172\23\15\24\0\5\15"+
    "\2\0\5\15\1\173\24\15\24\0\5\15\2\0\4\15"+
    "\1\174\25\15\24\0\5\15\2\0\23\15\1\175\6\15"+
    "\24\0\5\15\2\0\20\15\1\176\11\15\24\0\5\15"+
    "\2\0\21\15\1\177\10\15\24\0\5\15\2\0\22\15"+
    "\1\200\7\15\24\0\5\15\2\0\20\15\1\201\11\15"+
    "\24\0\5\15\2\0\1\15\1\202\30\15\24\0\5\15"+
    "\2\0\1\15\1\203\30\15\24\0\5\15\2\0\6\15"+
    "\1\204\23\15\24\0\5\15\2\0\12\15\1\205\1\206"+
    "\16\15\24\0\5\15\2\0\7\15\1\207\22\15\24\0"+
    "\5\15\2\0\3\15\1\210\10\15\1\112\15\15\24\0"+
    "\5\15\2\0\13\15\1\211\16\15\24\0\5\15\2\0"+
    "\1\15\1\212\30\15\31\0\1\60\57\0\5\15\2\0"+
    "\13\15\1\213\16\15\24\0\5\15\2\0\20\15\1\214"+
    "\11\15\24\0\5\15\2\0\12\15\1\215\17\15\24\0"+
    "\5\15\2\0\6\15\1\216\23\15\24\0\5\15\2\0"+
    "\20\15\1\112\11\15\24\0\5\15\2\0\13\15\1\217"+
    "\16\15\24\0\5\15\2\0\14\15\1\220\15\15\24\0"+
    "\5\15\2\0\30\15\1\221\1\15\24\0\5\15\2\0"+
    "\30\15\1\222\1\15\24\0\5\15\2\0\6\15\1\152"+
    "\23\15\24\0\5\15\2\0\16\15\1\223\13\15\24\0"+
    "\5\15\2\0\5\15\1\224\24\15\24\0\5\15\2\0"+
    "\1\15\1\225\30\15\24\0\5\15\2\0\14\15\1\226"+
    "\15\15\24\0\5\15\2\0\20\15\1\227\11\15\24\0"+
    "\5\15\2\0\7\15\1\212\22\15\24\0\5\15\2\0"+
    "\20\15\1\230\11\15\24\0\5\15\2\0\6\15\1\231"+
    "\23\15\24\0\5\15\2\0\20\15\1\232\11\15\24\0"+
    "\5\15\2\0\3\15\1\233\26\15\24\0\5\15\2\0"+
    "\12\15\1\234\1\15\1\235\15\15\24\0\5\15\2\0"+
    "\15\15\1\112\14\15\24\0\5\15\2\0\14\15\1\236"+
    "\15\15\24\0\5\15\2\0\7\15\1\237\22\15\24\0"+
    "\5\15\2\0\20\15\1\240\11\15\24\0\5\15\2\0"+
    "\20\15\1\241\11\15\24\0\5\15\2\0\1\15\1\242"+
    "\30\15\24\0\5\15\2\0\26\15\1\243\3\15\24\0"+
    "\5\15\2\0\24\15\1\244\5\15\24\0\5\15\2\0"+
    "\14\15\1\245\15\15\24\0\5\15\2\0\10\15\1\112"+
    "\21\15\24\0\5\15\2\0\1\15\1\112\30\15\24\0"+
    "\5\15\2\0\31\15\1\112\24\0\5\15\2\0\26\15"+
    "\1\246\3\15\24\0\5\15\2\0\3\15\1\247\26\15"+
    "\24\0\5\15\2\0\13\15\1\250\16\15\24\0\5\15"+
    "\2\0\12\15\1\251\17\15\24\0\5\15\2\0\14\15"+
    "\1\252\15\15\24\0\5\15\2\0\3\15\1\253\26\15"+
    "\24\0\5\15\2\0\20\15\1\236\11\15\24\0\5\15"+
    "\2\0\3\15\1\254\26\15\24\0\5\15\2\0\6\15"+
    "\1\255\23\15\24\0\5\15\2\0\7\15\1\213\22\15"+
    "\24\0\5\15\2\0\3\15\1\256\26\15\24\0\5\15"+
    "\2\0\3\15\1\213\26\15\24\0\5\15\2\0\13\15"+
    "\1\257\16\15\24\0\5\15\2\0\11\15\1\112\20\15"+
    "\24\0\5\15\2\0\10\15\1\260\21\15\24\0\5\15"+
    "\2\0\2\15\1\261\27\15\24\0\5\15\2\0\20\15"+
    "\1\262\11\15\24\0\5\15\2\0\5\15\1\263\24\15"+
    "\24\0\5\15\2\0\5\15\1\264\24\15\24\0\5\15"+
    "\2\0\20\15\1\265\11\15\24\0\5\15\2\0\20\15"+
    "\1\266\11\15\24\0\5\15\2\0\2\15\1\112\27\15"+
    "\24\0\5\15\2\0\3\15\1\267\26\15\24\0\5\15"+
    "\2\0\17\15\1\155\12\15\24\0\5\15\2\0\14\15"+
    "\1\200\15\15\24\0\5\15\2\0\5\15\1\232\24\15"+
    "\24\0\5\15\2\0\13\15\1\270\16\15\24\0\5\15"+
    "\2\0\7\15\1\151\22\15\24\0\5\15\2\0\11\15"+
    "\1\271\20\15\24\0\5\15\2\0\7\15\1\272\22\15"+
    "\24\0\5\15\2\0\1\15\1\155\30\15\24\0\5\15"+
    "\2\0\25\15\1\155\4\15\24\0\5\15\2\0\20\15"+
    "\1\273\11\15\24\0\5\15\2\0\11\15\1\274\20\15"+
    "\24\0\5\15\2\0\6\15\1\275\23\15\24\0\5\15"+
    "\2\0\1\15\1\226\30\15\24\0\5\15\2\0\6\15"+
    "\1\112\23\15\24\0\5\15\2\0\20\15\1\276\11\15"+
    "\16\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[7473];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\4\0\1\11\1\1\1\11\1\1\2\11\25\1\7\11"+
    "\1\1\2\11\1\1\2\11\2\1\2\11\1\0\2\1"+
    "\1\11\4\1\1\11\1\0\37\1\14\11\1\0\2\11"+
    "\126\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[190];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
    StringBuffer string = new StringBuffer(128);

    private Symbol token(short id)
    {
        return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());
    }
    
    private Symbol token(short id, Object value)
    {
        return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
    }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  CoolScanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  CoolScanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1648) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Symbol nextToken() throws java.io.IOException, Scanner.Exception {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 2: 
          { /* ignore */
          }
        case 58: break;
        case 25: 
          { /* do nothing */
          }
        case 59: break;
        case 32: 
          { yybegin(YYINITIAL); new ErrorReport(yyline + 1, yycolumn + 1, "Illegal escape sequence \""+yytext()+"\"", string.toString());
          }
        case 60: break;
        case 1: 
          { new ErrorReport(yyline + 1, yycolumn + 1, "Unknown symbol     found.", yytext());
          }
        case 61: break;
        case 40: 
          { string.append( '\f' );
          }
        case 62: break;
        case 6: 
          { return token(Terminals.TYPE);
          }
        case 63: break;
        case 18: 
          { return token(Terminals.DOT);
          }
        case 64: break;
        case 49: 
          { return token(Terminals.CASE);
          }
        case 65: break;
        case 19: 
          { return token(Terminals.LT);
          }
        case 66: break;
        case 50: 
          { return token(Terminals.ELSE);
          }
        case 67: break;
        case 30: 
          { return token(Terminals.ARROW);
          }
        case 68: break;
        case 36: 
          { string.append( '\b' );
          }
        case 69: break;
        case 47: 
          { return token(Terminals.THIS);
          }
        case 70: break;
        case 37: 
          { string.append( '\t' );
          }
        case 71: break;
        case 9: 
          { return token(Terminals.LPAREN);
          }
        case 72: break;
        case 4: 
          { return token(Terminals.TIMES);
          }
        case 73: break;
        case 13: 
          { return token(Terminals.COLON);
          }
        case 74: break;
        case 14: 
          { return token(Terminals.RBRACE);
          }
        case 75: break;
        case 15: 
          { return token(Terminals.SEMI);
          }
        case 76: break;
        case 54: 
          { return token(Terminals.WHILE);
          }
        case 77: break;
        case 24: 
          { yybegin(YYINITIAL); return token(Terminals.STRING, string.toString());
          }
        case 78: break;
        case 11: 
          { return token(Terminals.NOT);
          }
        case 79: break;
        case 41: 
          { yybegin(YYINITIAL);
          }
        case 80: break;
        case 5: 
          { return token(Terminals.INTEGER);
          }
        case 81: break;
        case 3: 
          { return token(Terminals.DIV);
          }
        case 82: break;
        case 46: 
          { return token(Terminals.BOOLEAN);
          }
        case 83: break;
        case 28: 
          { return token(Terminals.IF);
          }
        case 84: break;
        case 21: 
          { return token(Terminals.MINUS);
          }
        case 85: break;
        case 23: 
          { yybegin(YYINITIAL); new ErrorReport(yyline + 1, yycolumn + 1, "Unterminated string at end of line", string.toString());
          }
        case 86: break;
        case 44: 
          { return token(Terminals.DEF);
          }
        case 87: break;
        case 48: 
          { return token(Terminals.NULL);
          }
        case 88: break;
        case 55: 
          { return token(Terminals.NATIVE);
          }
        case 89: break;
        case 57: 
          { return token(Terminals.OVERRIDE);
          }
        case 90: break;
        case 45: 
          { return token(Terminals.VAR);
          }
        case 91: break;
        case 53: 
          { return token(Terminals.MATCH);
          }
        case 92: break;
        case 34: 
          { string.append( '\"' );
          }
        case 93: break;
        case 12: 
          { return token(Terminals.RPAREN);
          }
        case 94: break;
        case 17: 
          { return token(Terminals.COMMA);
          }
        case 95: break;
        case 51: 
          { return token(Terminals.SUPER);
          }
        case 96: break;
        case 7: 
          { return token(Terminals.ID);
          }
        case 97: break;
        case 39: 
          { string.append( '\r' );
          }
        case 98: break;
        case 26: 
          { yybegin(TRADITIONALCOMMENT);
          }
        case 99: break;
        case 8: 
          { yybegin(STRING); string.setLength(0);
          }
        case 100: break;
        case 35: 
          { string.append( '\\' );
          }
        case 101: break;
        case 52: 
          { return token(Terminals.CLASS);
          }
        case 102: break;
        case 42: 
          { yybegin(MULTISTRING); string.setLength(0);
          }
        case 103: break;
        case 10: 
          { return token(Terminals.LBRACE);
          }
        case 104: break;
        case 33: 
          { string.append( '\0' );
          }
        case 105: break;
        case 31: 
          { return token(Terminals.LE);
          }
        case 106: break;
        case 38: 
          { string.append( '\n' );
          }
        case 107: break;
        case 27: 
          { new ErrorReport(yyline + 1, yycolumn + 1, "Illegal keyword", yytext());
          }
        case 108: break;
        case 43: 
          { return token(Terminals.NEW);
          }
        case 109: break;
        case 16: 
          { return token(Terminals.ASSIGN);
          }
        case 110: break;
        case 29: 
          { return token(Terminals.EQUALS);
          }
        case 111: break;
        case 22: 
          { string.append( yytext() );
          }
        case 112: break;
        case 56: 
          { return token(Terminals.EXTENDS);
          }
        case 113: break;
        case 20: 
          { return token(Terminals.PLUS);
          }
        case 114: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            switch (zzLexicalState) {
            case STRING: {
              yybegin(YYINITIAL); new ErrorReport(yyline + 1, yycolumn     + 1, "Unterminated string at end of file", string.toString());
            }
            case 191: break;
            case TRADITIONALCOMMENT: {
              yybegin(YYINITIAL); new ErrorReport(yyline + 1, yycolumn + 1, "Comment ended by end of file", "");
            }
            case 192: break;
            case MULTISTRING: {
              yybegin(YYINITIAL); new ErrorReport(yyline + 1, yycolumn     + 1, "Unterminated string at end of file", string.toString());
            }
            case 193: break;
            default:
              {     return token(Terminals.EOF, "end-of-file");
 }
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
