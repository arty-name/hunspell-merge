// $Id$

package hunspell.merge;

import java.util.Vector;

public class DicString {

  private String value;
  private Vector<Affix> affixes;

  public DicString(String value, Vector<Affix> affixes) {
    this.value = value;
    this.affixes = affixes;
  }

  private String getAffixes(AffixFlag flag) {
    if (!hasAffixes())
      return "";

    String result = "";
    for (Affix affix : affixes) {
      result += ((!result.equals("") && (flag == AffixFlag.NUMBER)) ? "," : "") + affix.name;
    }
    return result.equals("") ? "" : "/" + result;
  }

  public String toString(AffixFlag flag) {
    return value + getAffixes(flag);
  }

  public String getValue() {
    return value;
  }

  public boolean hasAffixes() {
    return (affixes != null) && !affixes.isEmpty();
  }

  public boolean hasAffix(Affix affix) {
    return affixes.contains(affix);
  }
}
