// $Id$

package hunspell.merge.xpi;

import hunspell.merge.FileUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class XPI {

  private static String readResource(String fileName) {
    StringBuilder sb = new StringBuilder();

    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader((XPI.class).getResourceAsStream(fileName)));
      for (int c = br.read(); c != -1; c = br.read()) {
        sb.append((char) c);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public static void createXPI(String folder, String fileName, String description) {
    String rdf = readResource("install.rdf");
    FileUtil.saveToFile(rdf, folder + "install.rdf", null);
  }
}
