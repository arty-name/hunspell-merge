// $Id$

package hunspell.merge;

public enum AffixFlag {
  SINGLE(""),
  LONG("LONG"),
  NUMBER("NUMBER");

  private String name;

  AffixFlag(String name) {
    this.name = name;
  }

  public static AffixFlag parse(String value) {
    for (AffixFlag t : AffixFlag.values()) {
      if (t.compare(value))
        return t;
    }
    return SINGLE;
  }

  private boolean compare(String name) {
    return this.name.equalsIgnoreCase(name);
  }

  public String getName() {
    return name;
  }
}
