package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.function.Predicate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public enum IlluminaChemistry implements Predicate<Document> {
  V2("//ReagentKitVersion=Version2"), //
  V3("//ReagentKitVersion=Version3 or //Flowcell=\"HiSeq Flow Cell v3\" or //Flowcell=\"HiSeq Flow Cell\""), //
  V4("//Flowcell=\"HiSeq Flow Cell v4\""), //
  RAPID_RUN("starts-with(//Flowcell, \"HiSeq Rapid Flow Cell\")"), //
  NS_MID("//Chemistry=\"NextSeq Mid\""), //
  NS_HIGH("//Chemistry=\"NextSeq High\""), //
  UNKNOWN("false");
  private XPathExpression expr;

  IlluminaChemistry(String expression) {
    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();
    try {
      expr = xpath.compile(expression);
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Failed to compile XPath expression: " + expression, e);
    }
  }

  @Override
  public boolean test(Document t) {
    try {
      return (Boolean) expr.evaluate(t, XPathConstants.BOOLEAN);
    } catch (XPathExpressionException e) {
      return false;
    }
  }

}
