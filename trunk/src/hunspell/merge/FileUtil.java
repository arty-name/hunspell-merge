// $Id$

package hunspell.merge;

import java.io.*;

public class FileUtil {

  public static String tempFolder;
  public static String currentFolder;
  public static String dictionaryFolder;
  public static String outputFolder;

  static {
    currentFolder = addDelimeter(System.getProperty("user.dir"));
    tempFolder = addDelimeter(System.getProperty("java.io.tmpdir"));
    dictionaryFolder = currentFolder + addDelimeter("dictionaries");
    outputFolder = currentFolder + addDelimeter("output");
  }

  public static void saveToFile(String buffer, String outputFileName, String outputEncoding)
      throws IOException {

      FileOutputStream outputStream = new FileOutputStream(outputFileName);
      Writer out;
      if (outputEncoding == null)
        out = new OutputStreamWriter(outputStream);
      else
        out = new OutputStreamWriter(outputStream, outputEncoding);
      out.write(buffer);
      out.close();
  }

  public static boolean createFolder(String folderName) {
    return fileExists(folderName) || new File(folderName).mkdirs();
  }

  public static void deleteFolder(String folderName, boolean deleteFolder) {
    deleteRecursive(new File(folderName), deleteFolder);
  }

  public static boolean deleteRecursive(File folderName, boolean deleteFolder) {

    if (!folderName.exists())
      return true;
    boolean ret = true;
    if (folderName.isDirectory()) {
      for (File f : folderName.listFiles()) {
        ret = ret && deleteRecursive(f, true);
      }
    }
    return ret && (!deleteFolder ||  folderName.delete());
  }

  public static boolean delete(String fileName) {
    return !fileExists(fileName) || new File(fileName).delete();
  }

  public static boolean fileExists(String fileName) {
    return new File(fileName).exists();
  }

  public static String changeFileExt(String originalName, String newExtension) {
    int lastDot = originalName.lastIndexOf(".");
    if (lastDot != -1) {
      return originalName.substring(0, lastDot) + newExtension;
    } else {
      return originalName + newExtension;
    }
  }

  public static String addDelimeter(String path) {
    return path.endsWith(pathDelimeter()) ? path : path + pathDelimeter();
  }

  public static String pathDelimeter() {
    return System.getProperty("file.separator");
  }

  public static File[] findFiles(String folderName, final String... ext) {
    File folder = new File(folderName);
    return folder.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        for (String e : ext) {
          if (filename.toLowerCase().endsWith(e))
            return true;
        }
        return false;
      }
    });
  }

  public static String getOutputXPIFolder(){
    return outputFolder + addDelimeter("xpi");
  }

  public static String validateFileName(String s) {
    return s.replaceAll("[\\:/<>|?*\"'_]", "-");
  }

}
