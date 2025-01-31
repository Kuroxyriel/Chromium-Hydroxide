package exe.xyriel.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {
  public static JsonValue parse(String json) {
    json = json.strip();
    if (json.charAt(0) == '[') return getList(json,1).val();
    else if (json.charAt(0) == '{') return getMap(json,1).val();
    else throw new ParserException("Not a list or map");
  }

  private static Tuple getMap(String json, int pos) {
    JsonMap map = new JsonMap();
    for (int i = pos; i < json.length(); i++) {
      char ch = json.charAt(i);
      if (Character.isWhitespace(ch)) {}

      else if (ch == '}')
        return new Tuple(i+1, new JsonValue(map));

      else if (ch == '"') {
        Tuple ret = getString(json, i+1);
        String key = ret.val().getString();
        if (map.containsKey(key))
          throw new ParserException("Duplicate key at "+String.valueOf(i));
        i = findBreak(json, ret.pos(), ':')+2;

        while (Character.isWhitespace(json.charAt(i))) i++;

        ret = getMember(json, i, '}');
        i = ret.pos();
        map.put(key, ret.val());
      }
    }
    throw new ParserException("Unexpected end of string");
  }

  private static Tuple getList(String json, int pos) {
    JsonList vals = new JsonList();
    for (int i = pos; i < json.length(); i++) {
      if (Character.isWhitespace(json.charAt(i))) {}
      else if (json.charAt(i) == ']') {
        return new Tuple(i+1, new JsonValue(vals));
      } else {
        Tuple ret = getMember(json, i, ']');
        i = ret.pos();
        vals.add(ret.val());
      }
    }
    throw new ParserException("Unexpected end of string");
  }

  private static Tuple getMember(String json, int i, char sch) {
    char ch = json.charAt(i);
    switch (ch) {
     case 'T':
     case 't':
      if (!json.substring(i,i+4).toLowerCase().equals("true"))
        throw new ParserException("Invalid literal at "+String.valueOf(i));
      return new Tuple(findBreak(json, i+4, sch), new JsonValue(true));

     case 'F':
     case 'f':
      if (!json.substring(i,i+5).toLowerCase().equals("false"))
        throw new ParserException("Invalid literal at "+String.valueOf(i));
      return new Tuple(findBreak(json, i+5, sch), new JsonValue(false));

     case 'N':
     case 'n':
      if (!json.substring(i,i+4).toLowerCase().equals("null"))
        throw new ParserException("Invalid literal at "+String.valueOf(i));
      return new Tuple(findBreak(json, i+4, sch), new JsonValue());

     case '"':
      Tuple ret1 = getString(json, i+1);
      return new Tuple(findBreak(json, ret1.pos(), sch), ret1.val());
     case '[':
      Tuple ret2 = getList(json, i+1);
      return new Tuple(findBreak(json, ret2.pos(), sch), ret2.val());
     case '{':
      Tuple ret3 = getMap(json, i+1);
      return new Tuple(findBreak(json, ret3.pos(), sch), ret3.val());

     case '0':
     case '1':
     case '2':
     case '3':
     case '4':
     case '5':
     case '6':
     case '7':
     case '8':
     case '9':
      Tuple ret4 = getNumber(json, i);
      return new Tuple(findBreak(json, ret4.pos(), sch), ret4.val());
    }
    throw new ParserException("Invalid character '"+ch+"' at position "+String.valueOf(i));
  }

  private static Tuple getNumber(String json, int start) {
    String floatstr = "";
    int i = start;
    char ch = json.charAt(i);
    boolean dot = false;
    while (Character.isDigit(ch) || ch == '.') {
      if (ch == '.')
        if (dot) throw new ParserException("Second dot in a number literal at "+String.valueOf(start));
        else dot = true;
      floatstr += ch;
      i++;
      ch = json.charAt(i);
    }

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

  private static int findBreak(String json, int start, char sch) {
    int i = start;
    char ch;
    for (
      ch = json.charAt(i++);
      ch != ',' && ch != sch;
      ch = json.charAt(i++)
    ) if (!Character.isWhitespace(ch))
      throw new ParserException("Invalid character '"+ch+"' at "+String.valueOf(i)+" while looking for item break (additional search char: '"+sch+"')");
    return i-(ch==sch?2:1);
  }

  private record Tuple(int pos, JsonValue val) {}
}
