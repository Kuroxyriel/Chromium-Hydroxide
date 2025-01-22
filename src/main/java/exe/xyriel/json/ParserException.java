package exe.xyriel.json;

public class ParserException extends RuntimeException {
  public ParserException() { super(); }
  public ParserException(String msg) { super(msg); }
  public ParserException(String msg, Throwable ex) { super(msg, ex); }
}
