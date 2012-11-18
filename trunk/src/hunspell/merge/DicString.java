// $Id$

package hunspell.merge;

public class DicString extends HashData {

  private String value;
  private final HashDataVector<Affix> affixes = new HashDataVector<Affix>();

  public DicString(String value, HashDataVector<Affix> affixes) {
    this.value = value;
    addAffixes(affixes);
  }

  private String getAffixesString(AffixFlag flag) {
    if (!hasAffixes()) {
      return "";
    }

    String result = "";
    for (Affix affix : affixes.values()) {
      if (affix.hasLines()) {
        result += ((!result.equals("") && (flag == AffixFlag.NUMBER)) ? "," : "") + affix.getName();
      }
    }
    return result.equals("") ? "" : "/" + result;
  }

  public String toString(AffixFlag flag) {
    return value + getAffixesString(flag);
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

  public HashDataVector<Affix> getAffixes() {
    return affixes;
  }

  public void addAffixes(HashDataVector<Affix> newAffixes) {
    if (newAffixes != null) {
      affixes.addAll(newAffixes);
    }
  }

  @Override
  public String getHashString() {
    return value;
  }
}
