package koward;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by oppa on 26.02.2016.
 */
public class TestFiles {

  public static void main(String[] args) {

    File test = new File("C:\\Users\\oppa\\Desktop\\m2converter\\1");

    File[] testList = test.listFiles();

    ArrayList<String> list = new ArrayList<>();

    if (test != null) {
      for (File file : testList) {
        if (file.isFile() && file.getName().toLowerCase().contains(".m2")) {
          System.out.println(file.getName());
          list.add(file.getName());
        }
      }
    }

  }
}
