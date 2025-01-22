package exe.xyriel.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {
  public static JsonList parseList(String json) {
    json = json.strip();
    if (json.charAt(0) == '[') return parseList(json, 1).val().getList();
    else throw new ParserException("Invalid character '"+json.charAt(0)+"' at position 0");
  }

  private static Tuple parseList(String json, int pos) {
    JsonList vals = new JsonList();
    int i;
    for (i = pos; i < json.length(); i++) {
      /*System.out.print(i);
      System.out.print(": '");
      System.out.print(json.charAt(i));
      System.out.println("'");*/
      char ch = json.charAt(i);
      switch (ch) {
       case 'T':
       case 't':
        if (json.substring(i,i+4).toLowerCase().equals("true") && (i = findBreak(json, i+4, true)) > 0)
          vals.add(new JsonValue(true));
        break;
       case 'F':
       case 'f':
        if (json.substring(i,i+5).toLowerCase().equals("false") && (i = findBreak(json, i+5, true)) > 0)
          vals.add(new JsonValue(false));
        break;
       case 'N':
       case 'n':
        if (json.substring(i,i+4).toLowerCase().equals("null") && (i = findBreak(json, i+4, true)) > 0)
          vals.add(new JsonValue());
        break;
       case '"':
        Tuple ret = getString(json, i+1);
        vals.add(ret.val());
        i = findBreak(json, ret.pos(), true);
        break;
       default:
        if (Character.isDigit(ch)) {
          Tuple ret2 = getNumber(json, i, true);
          vals.add(ret2.val());
          i = ret2.pos();
        } else if (Character.isWhitespace(ch)) {
        } else if (ch == ']') return new Tuple(i+1, new JsonValue(vals));
        else throw new ParserExcept
        break;
      }
    }
    throw new ParserException("Unexpected end of string at list");
  }

  private static Tuple getNumber(String json, int start, boolean list) {
    String floatstr = "";
    int i = start;
    char ch = json.charAt(i);
    boolean dot = false;
    while (Character.isDigit(ch) || ch == '.') {
      if (ch == '.')
        if (dot) throw new ParserException("Dot when not allowed at "+String.valueOf(start));
        else dot = true;
      floatstr += ch;
      i++;
      ch = json.charAt(i);
    }

    System.out.println(floatstr);
    try { return new Tuple(i, new JsonValue(Float.parseFloat(floatstr))); }
    catch (NumberFormatException ex) { throw new ParserException("Invalid number '"+json.substring(start,i)+"' at "+String.valueOf(start), ex); }
  }

  private static final Map<Character,Character> allowedEscapes = Map.of('"','"','\\','\\','/','/','b','\b','f','\f','n','\n','r','\r','t','\t');
  private static Tuple getString(String json, int start) {
    String retstr = "";
    int i = start;
    char ch = json.charAt(i);
    while (ch != '"') {
      if (ch == '\\') {
        i++;
        ch = json.charAt(i);
        if (allowedEscapes.containsKey(ch)) retstr += allowedEscapes.get(ch);
        else if (ch == 'u') {
          try { retstr += Character.toString(Integer.parseInt(json.substring(i+1,i+5), 16)); }
          catch (NumberFormatException ex) { throw new ParserException("invalid codepoint '"+json.substring(i-1,i+5)+"' at "+String.valueOf(i-1), ex); }
          i += 3;
        } else throw new ParserException("invalid escape sequence '\\"+ch+"'");
      } else retstr += ch;
      i++;
      ch = json.charAt(i);
    }
    return new Tuple(i+1, new JsonValue(retstr));
  }

  private static int findBreak(String json, int start, boolean list) { return findBreak(json, start, list, false); }
  private static int findBreak(String json, int start, boolean list, boolean key) {
    System.out.print(start);
    System.out.print(": '");
    System.out.print(json.charAt(start));
    System.out.println("'");

    int i = start;
    char ch = json.charAt(i);
    while (ch != ',' && ( (ch!=']' && list) || (ch!='}' && !list) )) {
      if (!Character.isWhitespace(ch)) throw new ParserException("Invalid character '"+json.charAt(0)+"' at position 0 while looking for item break ("+String.valueOf(list)+", "+String.valueOf(key)+")");
      i++;
      ch = json.charAt(i);
    }
    return ch!=','?i-1:i;
  }

  private record Tuple(int pos, JsonValue val) {}
}
