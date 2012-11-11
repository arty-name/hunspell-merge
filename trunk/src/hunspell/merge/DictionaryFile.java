// $Id$

package hunspell.merge;

import java.io.File;
import java.nio.charset.Charset;

public class DictionaryFile extends File {

  private String nameNoExt;
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
    nameNoExt = FileUtil.changeFileExt(getName(), "");
    affName = type.isArchive() ? ZipUtil.containsFile(getPath(), ".aff") : getPath();

    affFile = extractAffFile();
    // Detect charset
    affReader.setChartSetReader(true);
    affReader.readFile(affFile, Charset.defaultCharset());
    affReader.setChartSetReader(false);

    dicName = type.isArchive() ? ZipUtil.containsFile(getPath(), ".dic") : getPath();
    dicFile = extractDicFile();
    dicReader.setAffReader(affReader);

  }

  public boolean isValid() {
    return !type.isArchive() || (dicFile.exists());
  }

  public String getNameNoExt() {
    return nameNoExt;
  }

  public File extractDicFile() {
    if (!type.isArchive())
      return this;

    File result = new File(FileUtil.tempFolder + dicName);
    if (!result.exists())
      ZipUtil.unzip(getPath(), FileUtil.tempFolder, ".dic");

    return result;
  }

  public File extractAffFile() {
    if (!type.isArchive())
      return new File(FileUtil.changeFileExt(getPath(), ".aff"));

    File result = new File(FileUtil.tempFolder + affName);
    if (!result.exists())
      ZipUtil.unzip(getPath(), FileUtil.tempFolder, ".aff");

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
