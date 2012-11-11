// $Id$

package hunspell.merge;

import java.util.Arrays;

public class Util {

  public final static String LINE_BREAK = System.getProperty("line.separator");

  public static String removeDuplicateChars(String s) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      String si = s.substring(i, i + 1);
      if (result.indexOf(si) == -1) {
        result.append(si);
      }
    }
    return result.toString();
  }

  public static String sortString(String txt) {
    char[] chars = txt.toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}
