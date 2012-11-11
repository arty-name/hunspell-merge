// $Id$

package hunspell.merge;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Affix {

  public String type = "";
  public String name = "";
  public String cross = "";
  private static Pattern header = Pattern.compile("(\\w\\w\\w) (\\w+) *([Y|N])* *\\d+");
  public Vector<String> lines = new Vector<String>();

  public boolean isReplace() {
    return type.equals("REP");
  }

  public static boolean isValid(String str) {
    return str.startsWith("SFX") ||
        str.startsWith("PFX") ||
        str.startsWith("REP");
  }

  public static boolean isGroupHeader(String str) {
    return isValid(str) && header.matcher(str).matches();
  }

  public void readLine(String str) {
    str = str.trim();
    if (str.equals(""))
      return;
    Matcher matcher = header.matcher(str);
    if (matcher.matches()) {
      type = matcher.group(1);

      if (isReplace()) {
        name = "REP";
      } else {
        if (matcher.groupCount() > 1)
          name = matcher.group(2);

        if (matcher.groupCount() > 2)
          cross = matcher.group(3);
      }
    } else if (isValid(str)) {

      lines.add(str);
    }
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(type);

    if (!isReplace()) {
      if (!(name == null) && (!name.equals("")))
        buffer.append(" ").append(name);

      if (!(cross == null) && (!cross.equals("")))
        buffer.append(" ").append(cross);
    }

    buffer.append(" ").append(lines.size()).append(Util.LINE_BREAK);

    for (String line : lines) {
      buffer.append(line).append(Util.LINE_BREAK);
    }

    return buffer.toString();
  }

  public void setName(String newName) {

    Vector<String> newLines = new Vector<String>();
    for (String line : lines) {
      newLines.add(line.replace(type + " " + name, type + " " + newName));
    }

    lines = newLines;
    name = newName;
  }

  public void appendAffixes(Affix affix) {
    for (String line : affix.lines) {
      lines.add(line);
    }
  }
}
