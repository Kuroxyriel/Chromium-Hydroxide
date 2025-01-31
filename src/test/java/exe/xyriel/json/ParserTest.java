package exe.xyriel.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {
  @Test public void testList() {
    System.out.println("================");
    JsonValue val = Parser.parse("[214515.4135,31515  \t  \t  ,true   , FALSE    ,nUlL\t,\t\t\"as\\\"gs\\tgsa\\u174Dgsa\",FaLsE]");
    System.out.println(val);
  }
  @Test public void testMap() {
    JsonValue val = Parser.parse("{\"obj\": \t{\"test\":NuLL}\t  ,  \t\"string\":\"tsgsag-\\\\\",\"list\":[214515.315,5215  \t  \t  ,true   , FALSE    ,nUlL\t,\t\t\"as\\\"gs\\tgsa\\u174Dgsa\",FaLsE],\"num\":41531.134}");
    System.out.println(val);
  }

  @Test public void testFromFile1() {
    System.out.println("================");
    try {
      JsonValue val = Parser.parse(Files.readString(Path.of("test1.json")));
      System.out.println(val);
    } catch (IOException ex) {}
  }
  @Test public void testFromFile2() {
    System.out.println("================");
    try {
      JsonValue val = Parser.parse(Files.readString(Path.of("test2.json")));
      System.out.println(val);
    } catch (IOException ex) {}
  }
}
