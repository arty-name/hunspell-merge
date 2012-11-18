// $Id$

package hunspell.merge;

import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AffixAlias extends HashData {

  private String parent = "";
  private Vector<String> aliasStrings = new Vector<String>();
  private HashDataVector<Affix> aliases = new HashDataVector<Affix>();

  private static Pattern aliasPattern = Pattern.compile("AF (.*) # (\\w+)");

  public void readLine(String str) {
    parent = parseAliasName(str, parent);
    String[] names = parseAliasNames(str, "").split(",");
    Collections.addAll(aliasStrings, names);
  }

  public String parseAliasName(String str, String defaultName) {
    Matcher matcher = aliasPattern.matcher(str);
    if (matcher.matches()) {
      return matcher.group(2);
    }

    return defaultName;
  }

  public String parseAliasNames(String str, String defaultName) {
    Matcher matcher = aliasPattern.matcher(str);
    if (matcher.matches()) {
      return matcher.group(1);
    }

    return defaultName;
  }

  public void addAliasAffix(Affix affix) {
    aliases.add(affix);
  }

  public Vector<String> getAliasStrings() {
    return aliasStrings;
  }

  public HashDataVector<Affix> getAliases() {
    return aliases;
  }

  @Override
  public String getHashString() {
    return parent;
  }
}
