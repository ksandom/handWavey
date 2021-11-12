package hello;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import hello.Greeter;


public class GreeterJUnit {
  @Test
  public void testGreeting() {
      Greeter g = new Greeter();
      assertEquals(g.sayHello(), "Hello world!");
  }
}
