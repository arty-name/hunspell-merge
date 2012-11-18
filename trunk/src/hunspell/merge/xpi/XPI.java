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

  private static void saveResource(String tempFolder, String resourceName, String fileName, String description)
      throws IOException {
    String resource = readResource(resourceName);
    resource = resource.replace("[%Description%]", description).replace("[%FileName%]", fileName);
    FileUtil.saveToFile(resource, tempFolder + resourceName, null);
  }

  public static void createXPI(String tempFolder, String xpiFileName, String dicFileName, String description)
      throws IOException {
    saveResource(tempFolder, "install.rdf", xpiFileName, description);
    saveResource(tempFolder, "install.js", xpiFileName, description);
    String dicFolder = FileUtil.makePath(tempFolder, "dictionaries");
    FileUtil.createFolder(dicFolder);
    FileUtil.renameFile(tempFolder + dicFileName + ".dic", dicFolder + dicFileName + ".dic");
    FileUtil.renameFile(tempFolder + dicFileName + ".aff", dicFolder + dicFileName + ".aff");

    ZipUtil.zipFolder(FileUtil.outputFolder + xpiFileName + ".xpi", tempFolder);
  }
}
