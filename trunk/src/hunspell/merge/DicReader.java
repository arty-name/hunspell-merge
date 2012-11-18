// $Id$

package hunspell.merge;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

public class DicReader extends FileReader {

  private AffReader affReader;
  private HashDataVector<DicString> strings = new HashDataVector<DicString>();

  public void addString(DicString string) {
    strings.add(string);
  }

  @Override
  protected void readLine(String str) {
    str = str.trim().replace("\t", "/");

    if (!str.equals("") && (!str.matches("\\d+"))) {
      String[] strs = str.split("/");
      addString(new DicString(strs[0], strs.length == 1 ? null : affReader.findAffixes(strs[1])));
    }
  }

  public void setAffReader(AffReader affReader) {
    this.affReader = affReader;
  }

  public void saveToFile(String fileName, AffixFlag flag)
      throws IOException {
    sort();
    StringBuilder buffer = new StringBuilder();
    buffer.append(strings.size()).append(Util.LINE_BREAK);
    for (DicString str : strings.values()) {
      buffer.append(str.toString(flag)).append(Util.LINE_BREAK);
    }
    FileUtil.saveToFile(buffer.toString(), fileName, "UTF-8");
    buffer.setLength(0);
  }

  public void appendDic(DicReader dicReader) {
    for (DicString dicString : dicReader.strings.values()) {
      DicString existing = strings.get(dicString);
      if (existing != null) {
        existing.addAffixes(dicString.getAffixes());
      } else {
        addString(dicString);
      }
    }
  }

  public void sort() {
    Collections.sort(strings.values(), new Comparator<DicString>() {
      public int compare(DicString o1, DicString o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
  }

  @Override
  protected void start() {
    super.start();
    clear();
  }

  public void clear() {
    strings.clear();
  }

  public int getWordCount() {
    return strings.size();
  }

  public HashDataVector<DicString> getStrings() {
    return strings;
  }
}

