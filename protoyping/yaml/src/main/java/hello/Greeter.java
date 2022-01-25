package hello;

import org.yaml.snakeyaml.*;
import java.io.InputStream;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Greeter {
  public void loadYaml() {
    Yaml yaml = new Yaml();
    String fileNameIn = "dataIn.yaml";
    String fileNameOut = "dataOut.yaml";
    
//     InputStream inputStream = this.getClass()
//         .getClassLoader()
//         .getResourceAsStream(fileNameIn);
    
    
    try {
        File initialFile = new File(fileNameIn);
        InputStream inputStream = new FileInputStream(initialFile);
        
//         String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//         System.out.println(text);
        
        Map<String, Object> obj = yaml.load(inputStream);
        System.out.println(obj);
        
        //getThing(obj, "items")).getThing(get("thing1")).put("value", "donkey");
        String aString = (String) getThing(getThing(obj, "items"), "thing1").get("description");
        System.out.println(aString);
        getThing(getThing(obj, "items"), "thing1").put("value", "donkey");
        
        getThing(getThing(getThing(getThing(getThing(obj, "groups"), "group1"), "groups"), "group1a"), "items")
            .put("aTestItem", newItem("a", "b", "Some \"letters\"."));
        getThing(getThing(getThing(obj, "groups"), "group1"), "groups")
            .put("aTestGroup", newGroup());
        
        Yaml yamlOut = new Yaml();
        FileWriter writer = new FileWriter(fileNameOut);
        yamlOut.dump(obj, writer);
        
    } catch (IOException e) {
        e.printStackTrace();
        return ;
    }
  }
  
  public Map getThing(Map obj, String name ) {
    System.out.println(name);
    return (Map) obj.get(name);
  }
  
  public Map newItem(String value, String defaultValue, String description) {
    Map result = new HashMap();
    
    result.put("value", value);
    result.put("defaultValue", defaultValue);
    result.put("description", description);
    
    return result;
  }
  
  public Map newGroup() {
    Map result = new HashMap();
    
    result.put("items", new HashMap());
    result.put("groups", new HashMap());
    
    return result;
  }
}
