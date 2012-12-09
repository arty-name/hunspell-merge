// $Id$

package hunspell.merge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AffReader extends FileReader {

  private AffixFlag flag = AffixFlag.SINGLE;
  private String tryStr = "";
  private Charset charset = Charset.defaultCharset();
  private Pattern patternSet = Pattern.compile(".*SET (.*) *");
  private Pattern patternTry = Pattern.compile("TRY (.*) *");
  private Pattern patternFlag = Pattern.compile("FLAG (.*) *");
  private HashDataVector<Affix> affixes = new HashDataVector<Affix>();
  private HashDataVector<AffixAlias> aliases = new HashDataVector<AffixAlias>();

  private Affix headerAffix = null;
  private boolean chartSetReader = false;

  public void readLine(String str) {
    if (str.trim().equals("")) {
      return;
    }

    Matcher matcher = patternSet.matcher(str);
    if (matcher.matches()) {
      charset = Charset.forName(matcher.group(1).trim());
      setAbort(chartSetReader);
      return;
    }

    if (chartSetReader) {
      return;
    }

    matcher = patternTry.matcher(str);
    if (matcher.matches()) {
      tryStr = matcher.group(1).trim();
      return;
    }

    matcher = patternFlag.matcher(str);
    if (matcher.matches()) {
      flag = AffixFlag.parse(matcher.group(1).trim());
      return;
    }

    if (AffixType.isAffixHeader(str)) {
      headerAffix = new Affix();
      headerAffix.readLine(str, true);
      if (!headerAffix.getType().isAlias()) {
        affixes.add(headerAffix);
      }
    } else if ((headerAffix != null) && AffixType.isValid(str)) {
      if (headerAffix.getType().isAlias()) {
        AffixAlias alias = new AffixAlias();
        alias.readLine(str);
        aliases.add(alias);
      } else {
        headerAffix.readLine(str, false);
      }
    }
  }

  @Override
  protected void end() {
    super.end();
    processAliases();
  }

  private void processAliases() {
    for (AffixAlias alias : aliases.values()) {
      for (String aliasStr : alias.getAliasStrings()) {
        Affix affix = affixes.get(aliasStr);
        if (affix != null) {
          alias.addAliasAffix(affix);
        }
      }
    }
  }

  @Override
  protected void start() {
    super.start();
    clear();
    tryStr = "";
  }

  public Charset getCharset() {
    return charset;
  }

  public void setChartSetReader(boolean chartSetReader) {
    this.chartSetReader = chartSetReader;
  }

  public HashDataVector<Affix> findAffixes(String value) {
    HashDataVector<Affix> result = new HashDataVector<Affix>();
    Vector<String> affixStrings = new Vector<String>();

    // Split affixes depending on affix type
    switch (flag) {
      case SINGLE:
        for (char c : value.toCharArray()) {
          affixStrings.add(String.valueOf(c));
        }
        break;
      case LONG:
        String aff = "";
        for (char c : value.toCharArray()) {
          aff += String.valueOf(c);
          if (aff.length() == 2) {
            affixStrings.add(aff);
            aff = "";
          }
        }
        break;
      case NUMBER:
        Collections.addAll(affixStrings, value.split(","));
        break;
    }

    for (String affixStr : affixStrings) {
      if (aliases.contains(affixStr)) {
        result.addAll(aliases.get(affixStr).getAliases());
      } else if (affixes.contains(affixStr)) {
        result.add(affixes.get(affixStr));
      }
    }

    return result;
  }

  public void renameAffixes(AffixFlag flag) {

    switch (flag) {
      case NUMBER:
        int index = 1;
        for (Affix affix : affixes.values()) {
          if (affix.getType().isNamedAffix()) {
            affix.setName(String.valueOf(index));
            index++;
          }
        }
        break;
    }
  }

  public void saveToFile(String fileName, AffixFlag flag)
      throws IOException {
    sort();
    renameAffixes(flag);
    StringBuilder buffer = new StringBuilder();
    buffer.append("SET UTF-8").append(Util.LINE_BREAK);
    buffer.append("TRY ").append(Util.sortString(tryStr)).append(Util.LINE_BREAK);
    if (flag != AffixFlag.SINGLE)
      buffer.append("FLAG ").append(flag.getName()).append(Util.LINE_BREAK);

    buffer.append(Util.LINE_BREAK);

    for (Affix affix : affixes.values()) {
      buffer.append(affix.toString()).append(Util.LINE_BREAK);
    }

    FileUtil.saveToFile(buffer.toString(), fileName, "UTF-8");
  }

  public void appendAff(AffReader affReader) {
    tryStr = Util.sortString(Util.removeDuplicateChars(tryStr + affReader.tryStr));

    for (Affix affix : affReader.affixes.values()) {
      if (!affix.getType().isNamedAffix() && !affix.getType().isAlias()) {
        Affix existing = affixes.get(affix.getName());
        if (existing == null) {
          affixes.add(affix);
        } else {
          existing.appendValues(affix);
        }
      } else {
        affixes.add(affix);
      }
    }
  }

  public void removeUnusedAffixes(DicReader outDic) {

// Check for duplicates
//    for (int i = 0; i < affixes.size(); i++) {
//      Affix affix = affixes.elementAt(i);
//      if (affix.getType().isNamedAffix())
//        for (String line : affix.getLines().values) {
//          for (int j = i + 1; j < affixes.size(); j++) {
//            Affix affix2 = affixes.elementAt(j);
//            affix2.removeDuplicate(line);
//          }
//        }
//    }

    for (int i = affixes.size() - 1; i >= 0; i--) {
      Affix affix = affixes.elementAt(i);
      if (!affix.getType().isNamedAffix()) {
        continue;
      }

      boolean hasAffix = false;

      if (affix.hasLines()) {
        for (DicString string : outDic.getStrings().values()) {
          if (!string.hasAffixes()) {
            continue;
          }
          if (string.hasAffix(affix)) {
            hasAffix = true;
            break;
          }
        }
      }
      if (!hasAffix) {
        affixes.remove(affix);
      }
    }
  }

  public void clear() {
    affixes.clear();
    affixes.clear();
  }

  public int getAffCount() {
    return affixes.size();
  }

  public int getAliasCount() {
    return aliases.size();
  }

  public void sort() {
    Collections.sort(affixes.values(), new Comparator<Affix>() {
      public int compare(Affix affix1, Affix affix2) {
        int result = affix1.getType().getName().compareTo(affix2.getType().getName());
        if (result == 0) {
          result = affix1.getName().compareTo(affix2.getName());
        }

        return result;
      }
    });
  }
}
