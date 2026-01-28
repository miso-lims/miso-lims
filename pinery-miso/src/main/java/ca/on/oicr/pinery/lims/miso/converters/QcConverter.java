package ca.on.oicr.pinery.lims.miso.converters;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

import ca.on.oicr.pinery.api.Attribute;
import ca.on.oicr.pinery.api.Sample;
import ca.on.oicr.pinery.lims.DefaultAttribute;

public enum QcConverter {

  QUBIT(".*Qubit.*", "Qubit (ng/uL)"), //
  NANODROP("^Nanodrop$", "Nanodrop (ng/uL)"), //
  HUMAN_QPCR("^Human qPCR$", "qPCR %"), //
  RIN("RIN.*", "RIN"), //
  DV200(".*DV200.*", "DV200"), //
  PDAC("^PDAC Confirmed$", "PDAC Confirmed", "True", "False"), //
  DRAFT_CLINICAL_REPORT("^Draft Clinical Report$", "Draft Clinical Report", "Pass", "Fail"), //
  INFORMATICS_REVIEW("^Informatics Review$", "Informatics Review", "Pass", "Fail"), //
  PURITY("^Purity$", "Purity"), //
  UMI_COLLAPSED_COVERAGE("^UMI-Collapsed Coverage$", "UMI-Collapsed Coverage"), //
  TUMOUR_CONTENT("^Tumour Content$", "Tumour Content");

  private final String pattern;
  private final String attributeName;
  private final String trueLabel;
  private final String falseLabel;

  private QcConverter(String pattern, String attributeName) {
    this.pattern = pattern;
    this.attributeName = attributeName;
    this.trueLabel = null;
    this.falseLabel = null;
  }

  private QcConverter(String pattern, String attributeName, String trueLabel, String falseLabel) {
    this.pattern = pattern;
    this.attributeName = attributeName;
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }

  public static void addToSample(ResultSet rs, Sample sample) throws SQLException {
    String qcType = rs.getString("qcType");
    for (QcConverter converter : QcConverter.values()) {
      if (qcType.matches(converter.pattern)) {
        // There may be multiple QCs for a given attribute name. The query should return the most recent
        // first and all others should be ignored
        if (alreadyIncluded(converter, sample)) {
          return;
        }
        String result = converter.trueLabel == null ? extractBigDecimalString(rs) : converter.extractBooleanString(rs);
        if (result != null) {
          Attribute attr = new DefaultAttribute();
          attr.setName(converter.attributeName);
          attr.setValue(result);
          if (sample.getAttributes() == null) {
            sample.setAttributes(Sets.newHashSet());
          }
          sample.getAttributes().add(attr);
        }
        return;
      }
    }

  }

  private static boolean alreadyIncluded(QcConverter converter, Sample sample) {
    return sample.getAttributes() != null
        && sample.getAttributes().stream().anyMatch(attr -> converter.attributeName.equals(attr.getName()));
  }

  private static String extractBigDecimalString(ResultSet rs) throws SQLException {
    BigDecimal value = rs.getBigDecimal("results");
    if (!rs.wasNull()) {
      String nice = StringUtils.strip(value.toPlainString(), "0");
      if (nice.startsWith(".")) {
        nice = "0" + nice;
      }
      nice = StringUtils.strip(nice, ".");
      return nice;
    }
    return null;
  }

  private String extractBooleanString(ResultSet rs) throws SQLException {
    int value = rs.getInt("results");
    if (rs.wasNull()) {
      return null;
    }
    return value > 0 ? trueLabel : falseLabel;
  }

}
