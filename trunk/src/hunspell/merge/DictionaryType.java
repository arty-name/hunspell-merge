// $Id$

package hunspell.merge;

public enum DictionaryType {

  PLAIN("Dictionary", ".dic"),
  ZIP("ZIP", ".zip"),
  OXT("OpenOffice", ".oxt"),
  XPI("XPInstall", ".xpi");
  private String name;

  private String ext;

  private DictionaryType(String name, String ext) {
    this.name = name;
    this.ext = ext;
  }

  private boolean compare(String name) {
    return name.toLowerCase().endsWith(ext);
  }

  public static DictionaryType parse(String value) {
    for (DictionaryType t : DictionaryType.values()) {
      if (t.compare(value))
        return t;
    }
    return PLAIN;
  }

  public String getName() {
    return name;
  }

  public boolean in(DictionaryType... types) {
    for (DictionaryType type : types) {
      if (this == type)
        return true;
    }
    return false;
  }

  public boolean isArchive() {
    return this != PLAIN;
  }

  public static String[] getAllExts() {
    String[] result = new String[DictionaryType.values().length];

    for (int i = 0; i < DictionaryType.values().length; i++) {
      DictionaryType dictionaryType = DictionaryType.values()[i];
      result[i] = dictionaryType.ext;
    }
    return result;
  }

}
