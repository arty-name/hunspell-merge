// $Id$

package hunspell.merge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Affix extends HashData {

  private AffixType type = AffixType.UNKNOWN;
  private String name = "";
  private String crossFlag = "";
  private HashVector<String> lines = new HashVector<String>();

  private static Pattern namePattern = Pattern.compile("\\w+ (\\w+).*");
  private static Pattern crossPattern = Pattern.compile("\\w+ \\w+ ([Y|N]).*");

  public void readLine(String str, boolean isHeader) {
    str = str.trim();
    if (str.equals("")) {
      return;
    }

    if (isHeader) {
      type = AffixType.parseType(str);
      if (type.isNamedAffix()) {
        name = parseName(str, type.getName());
      } else {
        name = type.getName();
      }
      if (type.isCrossAffix()) {
        crossFlag = parseCrossFlag(str);
      }
    } else {
      String value = str.replace(type.name() + " " + (type.isNamedAffix() ? name + " " : ""), "");
      lines.add(value, value);
    }
  }

  public String parseName(String str, String defaultName) {
    Matcher matcher = namePattern.matcher(str);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return defaultName;
  }

  public String parseCrossFlag(String str) {
    Matcher matcher = crossPattern.matcher(str);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return "";
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder();

    String prefix = type.getName();

    if (type.isNamedAffix()) {
      if (!(name == null) && (!name.equals(""))) {
        prefix += " " + name;
      }

      buffer.append(prefix);

      if (!(crossFlag == null) && (!crossFlag.equals(""))) {
        buffer.append(" ").append(crossFlag);
      }
    } else {
      buffer.append(prefix);
    }

    buffer.append(" ").append(lines.size()).append(Util.LINE_BREAK);

    for (String value : lines.values) {
      buffer.append(prefix).append(" ").append(value).append(Util.LINE_BREAK);
    }

    return buffer.toString();
  }

  public void setName(String newName) {
    name = newName;
  }

  public String getName() {
    return name;
  }

  public AffixType getType() {
    return type;
  }

  public void appendValues(Affix affix) {
    for (String value : affix.lines.values) {
      if (!lines.contains(value)) {
        lines.add(value, value);
      }
    }
  }

  @Override
  public String getHashString() {
    return name;
  }

  public boolean hasLines() {
    return !lines.isEmpty();
  }

  public HashVector<String> getLines() {
    return lines;
  }

  public void removeDuplicate(String line) {
    if (lines.contains(line)) {
      lines.remove(line);
    }
  }
}
