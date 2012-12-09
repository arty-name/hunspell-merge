// $Id$

package hunspell.merge;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DictionaryFile extends File {

  private String laguageID;
  private String affName;
  private String dicName;
  private DictionaryType type;
  private final File affFile;
  private final File dicFile;
  public AffReader affReader = new AffReader();
  public DicReader dicReader = new DicReader();
  private NumberFormat wordFormat = new DecimalFormat("#,###");

  public DictionaryFile(File file) {
    super(file.getPath());
    type = DictionaryType.parse(file.getName());
    affName = type.isArchive() ? ZipUtil.containsFile(getPath(), ".aff") : getPath();
    if (affName != null)
      affName = new File(affName).getName();

    FileUtil.createFolder(getTempFolder());

    affFile = extractAffFile();
    if (affFile != null) {
      // Detect charset
      affReader.setChartSetReader(true);
      affReader.readFile(affFile, Charset.forName("UTF-8"));
      affReader.setChartSetReader(false);
    }

    dicName = type.isArchive() ? ZipUtil.containsFile(getPath(), ".dic") : getPath();
    if (dicName != null)
      dicName = new File(dicName).getName();
    dicFile = extractDicFile();
    dicReader.setAffReader(affReader);

    if (dicFile != null) {
      // Detect words count
      dicReader.setWordReader(true);
      dicReader.readFile(dicFile, getCharset());
      dicReader.setWordReader(false);
    }

    if (isValid())
      laguageID = FileUtil.changeFileExt(dicName, "");
  }

  public boolean isValid() {
    return (dicFile != null) && (dicFile.exists());
  }

  public String getLaguageID() {
    return laguageID;
  }

  private String getTempFolder() {
    return FileUtil.makePath(FileUtil.tempFolder, getName());
  }

  public File extractDicFile() {
    if (!type.isArchive())
      return this;

    File result = new File(getTempFolder() + dicName);
    if (!result.exists())
      try {
        ZipUtil.unzip(getPath(), getTempFolder(), ".dic");
      } catch (IOException e) {
        return null;
      }

    return result;
  }

  public File extractAffFile() {
    if (!type.isArchive())
      return new File(FileUtil.changeFileExt(getPath(), ".aff"));

    File result = new File(getTempFolder() + affName);
    if (!result.exists())
      try {
        ZipUtil.unzip(getPath(), getTempFolder(), ".aff");
      } catch (IOException e) {
        return null;
      }

    return result;
  }

  public Charset getCharset() {
    return affReader.getCharset();
  }

  public String getWordsCount() {
    return wordFormat.format(dicReader.getWordsCount());
  }

  public DictionaryType getType() {
    return type;
  }

  public void readFiles() {
    affReader.readFile(affFile, getCharset());
    dicReader.readFile(dicFile, getCharset());
  }

  public void clear() {
    affReader.clear();
    dicReader.clear();
    System.gc();
  }

  public String getSummary() {
    return getLaguageID() + " (" + getCharset() + ")";
  }
}
