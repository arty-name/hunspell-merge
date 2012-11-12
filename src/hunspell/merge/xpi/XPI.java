// $Id$

package hunspell.merge.xpi;

import hunspell.merge.FileUtil;
import hunspell.merge.ZipUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class XPI {

  private static String readResource(String resourceName)
      throws IOException {
    StringBuilder sb = new StringBuilder();

    BufferedReader br;
    br = new BufferedReader(new InputStreamReader((XPI.class).getResourceAsStream(resourceName)));
    for (int c = br.read(); c != -1; c = br.read()) {
      sb.append((char) c);
    }

    return sb.toString();
  }

  private static void saveResource(String resourceName, String fileName, String description)
      throws IOException {
    String resource = readResource(resourceName);
    resource = resource.replace("[%Description%]", description).replace("[%FileName%]", fileName);
    FileUtil.saveToFile(resource, FileUtil.tempFolder + resourceName, null);
  }

  public static void createXPI(String fileName, String description)
      throws IOException {
    saveResource("install.rdf", fileName, description);
    saveResource("install.js", fileName, description);

    ZipUtil.zipFolder(FileUtil.outputFolder + fileName + ".xpi", FileUtil.tempFolder);
  }
}
