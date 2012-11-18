// $Id$

package hunspell.merge;

public enum AffixType {

  UNKNOWN(null),
  SFX("SFX"),
  PFX("PFX"),
  REP("REP"),
  AM("AM"),
  AF("AF");

  private String name;

  AffixType(String name) {
    this.name = name;
  }

  public static AffixType parseType(String value) {
    if (value != null) {
      for (AffixType t : AffixType.values()) {
        if (t.compare(value.trim())) {
          return t;
        }
      }
    }
    return UNKNOWN;
  }

  public static boolean isAffixHeader(String str) {
    return isValid(str) && str.matches("(\\w+) (\\w*) *([Y|N])* *\\d+");
  }

  private boolean compare(String name) {
    return (this.name != null) && (name != null) && name.startsWith(this.name);
  }

  public static boolean isValid(String value) {
    return parseType(value) != UNKNOWN;
  }

  public boolean isNamedAffix() {
    return in(PFX, SFX);
  }

  public boolean in(AffixType... types) {
    for (AffixType type : types) {
      if (this == type) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "AffixType{" +
        "name='" + name + '\'' +
        '}';
  }

  public boolean isCrossAffix() {
    return in(PFX, SFX);
  }

  public String getName() {
    return name;
  }

  public boolean isAlias() {
    return in(AF);
  }

}


