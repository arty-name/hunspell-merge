// $Id$

package hunspell.merge;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

  private static void copyInputStream(InputStream in, OutputStream out)
      throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0) { out.write(buffer, 0, len); }

    in.close();
    out.close();
  }

  public static String containsFile(String zipFileName, String ext) {
    try {
      ZipFile zipFile = new ZipFile(zipFileName);
      try {
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = (ZipEntry) entries.nextElement();

          if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(ext))
            return entry.getName();
        }
      } finally {
        zipFile.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean unzip(String zipFileName, String unZipFolder, String... ext) {
    boolean result = false;
    try {
      ZipFile zipFile = new ZipFile(zipFileName);
      try {
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = (ZipEntry) entries.nextElement();

          boolean isValidExt = true;

          // Check for extension
          if (ext.length != 0) {
            isValidExt = false;
            for (String e : ext) {
              if (entry.getName().toLowerCase().endsWith(e)) {
                isValidExt = true;
                break;
              }
            }
          }

          if (isValidExt) {
            String outFileName = FileUtil.addDelimeter(unZipFolder) + new File(entry.getName()).getName();
            FileUtil.delete(outFileName);
            copyInputStream(zipFile.getInputStream(entry),
                new BufferedOutputStream(new FileOutputStream(outFileName)));
            result = true;
          }
        }
      } finally {
        zipFile.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}
