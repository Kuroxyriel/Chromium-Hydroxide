package exe.xyriel.json;

public class JsonValue {
  private final JsonType type;
  private final Object obj;

  public JsonValue(boolean val) {
    type = JsonType.BOOL;
    obj = val;
  }
  public JsonValue() {
    type = JsonType.NULL;
    obj = null;
  }
  public JsonValue(String str) {
    type = JsonType.STR;
    obj = str;
  }
  public JsonValue(JsonList list) {
    type = JsonType.LIST;
    obj = list;
  }
  public JsonValue(JsonMap map) {
    type = JsonType.MAP;
    obj = map;
  }
  public JsonValue(float num) {
    type = JsonType.NUM;
    obj = num;
  }
  public JsonValue(int num) {
    type = JsonType.NUM;
    obj = num;
  }

  public boolean getBool() { return (boolean)obj; }
  public String getString() { return (String)obj; }
  public JsonList getList() { return (JsonList)obj; }
  public JsonMap getMap() { return (JsonMap)obj; }
  public int getInt() { return (int)obj; }
  public float getFloat() { return (float)obj; }

  @Override
  public String toString() {
    if (type == JsonType.STR) return "("+type.toString()+", \""+(String)obj+"\")";
    else return "("+type.toString()+", "+(obj==null?"null":obj.toString())+")";
  }
}
