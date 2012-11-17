// $Id$

package hunspell.merge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
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
  private Vector<Affix> affixes = new Vector<Affix>();
  private HashMap<String, Affix> affixHash = new HashMap<String, Affix>();
  private Affix lastAffix = null;
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

    if (Affix.isGroupHeader(str)) {
      lastAffix = new Affix();
      lastAffix.readLine(str);
      affixes.add(lastAffix);
      affixHash.put(lastAffix.name, lastAffix);
    } else if (lastAffix != null) {
      lastAffix.readLine(str);
    }
  }

  @Override
  protected void start() {
    super.start();
    tryStr = "";
  }

  public Charset getCharset() {
    return charset;
  }

  public void setChartSetReader(boolean chartSetReader) {
    this.chartSetReader = chartSetReader;
  }

  public Vector<Affix> findAffixes(String value) {
    Vector<Affix> result = new Vector<Affix>();
    Vector<String> affixes = new Vector<String>();

    // Split affixes depending on affix type
    switch (flag) {
      case SINGLE:
        for (char c : value.toCharArray()) {
          affixes.add(String.valueOf(c));
        }
        break;
      case LONG:
        String aff = "";
        for (char c : value.toCharArray()) {
          aff += String.valueOf(c);
          if (aff.length() == 2) {
            affixes.add(aff);
            aff = "";
          }
        }
        break;
      case NUMBER:
        Collections.addAll(affixes, value.split(","));
        break;
    }

    for (String affix : affixes) {
      if (affixHash.containsKey(affix)) {
        result.add(affixHash.get(affix));
      }
    }

    return result;
  }

  public void renameAffixes(AffixFlag flag) {
    switch (flag) {
      case NUMBER:
        int index = 1;
        for (Affix affix : affixes) {
          if (!affix.isReplace()) {
            affix.setName(String.valueOf(index));
            index++;
          }
        }
        break;
    }
  }

  public void saveToFile(String fileName, AffixFlag flag)
      throws IOException {
    renameAffixes(flag);
    StringBuilder buffer = new StringBuilder();
    buffer.append("SET UTF-8").append(Util.LINE_BREAK);
    buffer.append("TRY ").append(Util.sortString(tryStr)).append(Util.LINE_BREAK);
    if (flag != AffixFlag.SINGLE)
      buffer.append("FLAG ").append(flag.name()).append(Util.LINE_BREAK);
    buffer.append(Util.LINE_BREAK);

    for (Affix affix : affixes) {
      buffer.append(affix.toString()).append(Util.LINE_BREAK);
    }

    FileUtil.saveToFile(buffer.toString(), fileName, "UTF-8");
  }

  public void appendAff(AffReader affReader) {
    tryStr = Util.sortString(Util.removeDuplicateChars(tryStr + affReader.tryStr));
    for (Affix affix : affReader.affixes) {
      if (affix.isReplace()) {
        Affix replace = affixHash.get(affix.name);
        if (replace == null)
          affixes.add(affix);
        else
          replace.appendAffixes(affix);
      } else
        affixes.add(affix);
    }
  }

  public void removeUnusedAffixes(DicReader outDic) {
    for (int i = affixes.size() - 1; i >= 0; i--) {
      Affix affix = affixes.elementAt(i);
      boolean hasAffix = false;
      for (DicString string : outDic.getStrings()) {
        if (!string.hasAffixes())
          continue;
        if (string.hasAffix(affix)) {
          hasAffix = true;
          break;
        }
      }
      if (!hasAffix)
        affixes.remove(affix);
    }
  }

  public void clear() {
    affixes.clear();
    affixHash.clear();

  }

  public int getAffCount() {
    return affixes.size();
  }
}
