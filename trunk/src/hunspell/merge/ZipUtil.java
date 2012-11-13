// $Id$

package hunspell.merge;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
    } catch (IOException ignored) {
    }
    return null;
  }

  public static boolean unzip(String zipFileName, String unZipFolder, String... ext)
      throws IOException {
    boolean result = false;
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
            String outFileName = FileUtil.makePath(unZipFolder) + new File(entry.getName()).getName();
            FileUtil.delete(outFileName);
            copyInputStream(zipFile.getInputStream(entry),
                new BufferedOutputStream(new FileOutputStream(outFileName)));
            result = true;
          }
        }
      } finally {
        zipFile.close();
      }
    return result;
  }

  private static void zipFolderImpl(ZipOutputStream outputStream, File sourceFile, String folder)
      throws IOException {
    File[] files = sourceFile.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        zipFolderImpl(outputStream, file, folder + file.getName() + "/");
        continue;
      }

      byte[] buffer = new byte[1024];
      FileInputStream inputStream = new FileInputStream(file);
      outputStream.putNextEntry(new ZipEntry(folder + file.getName()));
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.closeEntry();
      inputStream.close();
    }
  }

  public static void zipFolder(String zipFileName, String zipFolder)
      throws IOException {
    ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
    try {
      zipFolderImpl(outputStream, new File(zipFolder), "");
    } finally {
      outputStream.close();
    }
  }
}
