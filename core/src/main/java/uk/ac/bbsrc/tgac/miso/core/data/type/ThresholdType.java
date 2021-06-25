package uk.ac.bbsrc.tgac.miso.core.data.type;

public enum ThresholdType {

  GT(">", true, false), //
  GE("&GreaterEqual;", true, false), //
  LT("<", false, true), //
  LE("&leq;", false, true), //
  BETWEEN("range", true, true), //
  BOOLEAN("pass/fail", false, false);

  private final String sign;
  private final boolean lowerBound;
  private final boolean upperBound;
  
  private ThresholdType(String sign, boolean lowerBound, boolean upperBound) {
    this.sign = sign;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }
  
  public String getSign() {
    return sign;
  }

  public boolean hasLowerBound() {
    return lowerBound;
  }
  
  public boolean hasUpperBound() {
    return upperBound;
  }
  
}
