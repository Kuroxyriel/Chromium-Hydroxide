package exe.xyriel.json;

import org.junit.Test;
import static org.junit.Assert.*;

public class ParserTest {
  @Test public void test() {
    JsonList vals = Parser.parseList("[[214515  \t  \t  ,true   , FALSE    ,nUlL\t,\t\t\"as\\\"gs\\tgsa\\u174Dgsa\",FaLsE]]");
    // dodac tostring do jsonlist
    
    for (JsonValue v : vals)
      System.out.println(v);
  }
}
