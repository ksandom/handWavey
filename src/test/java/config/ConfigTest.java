package config;

import config.Config;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ConfigTester extends Config {
  public ConfigTester(String fileName) {
    super(fileName);
  }
  
  public String getBestPath() {
    return super.getBestPath();
  }
}

public class ConfigTest {
  @Test
  public void testGreeting() {
      String fileName = "handWaveyConfigTest.yml";
      ConfigTester ct = new ConfigTester(fileName);
      
      assertNotNull(ct.getBestPath());
  }
}


