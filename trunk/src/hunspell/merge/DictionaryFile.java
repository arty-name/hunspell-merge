// $Id$

package hunspell.merge;

import java.io.File;
import java.nio.charset.Charset;

public class DictionaryFile extends File {

  private String laguageID;
  private String affName;
  private String dicName;
  private DictionaryType type;
  private final File affFile;
  private final File dicFile;
  public AffReader affReader = new AffReader();
  public DicReader dicReader = new DicReader();

  public DictionaryFile(File file) {
    super(file.getPath());
    type = DictionaryType.parse(file.getName());
    affName = new File(type.isArchive() ? ZipUtil.containsFile(getPath(), ".aff") : getPath()).getName();

    FileUtil.createFolder(getTempFolder());

    affFile = extractAffFile();
    // Detect charset
    affReader.setChartSetReader(true);
    affReader.readFile(affFile, Charset.forName("UTF-8"));
    affReader.setChartSetReader(false);

    dicName = new File(type.isArchive() ? ZipUtil.containsFile(getPath(), ".dic") : getPath()).getName();
    dicFile = extractDicFile();
    dicReader.setAffReader(affReader);

    if (isValid())
    laguageID = FileUtil.changeFileExt(dicName, "");
  }

  public boolean isValid() {
    return (dicFile.exists());
  }

  public String getLaguageID() {
    return laguageID;
  }

  private String getTempFolder(){
    return FileUtil.makePath(FileUtil.tempFolder, getName());
  }

  public File extractDicFile() {
    if (!type.isArchive())
      return this;

    File result = new File(getTempFolder() + dicName);
    if (!result.exists())
      ZipUtil.unzip(getPath(), getTempFolder(), ".dic");

    return result;
  }

  public File extractAffFile() {
    if (!type.isArchive())
      return new File(FileUtil.changeFileExt(getPath(), ".aff"));

    File result = new File(getTempFolder() + affName);
    if (!result.exists())
      ZipUtil.unzip(getPath(), getTempFolder(), ".aff");

    return result;
  }

  public Charset getCharset() {
    return affReader.getCharset();
  }

  public DictionaryType getType() {
    return type;
  }

  public void readFiles(){
    affReader.readFile(affFile, getCharset());
    dicReader.readFile(dicFile, getCharset());
  }
}
