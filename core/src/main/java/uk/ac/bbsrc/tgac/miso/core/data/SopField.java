package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

import org.apache.commons.validator.routines.BigDecimalValidator;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;

public interface SopField {

  enum FieldType {
    TEXT, NUMBER, PERCENTAGE
  }

  long getId();

  void setId(long id);

  Sop getSop();

  void setSop(Sop sop);

  String getName();

  void setName(String name);

  String getUnits();

  void setUnits(String units);

  String getFieldType();

  void setFieldType(String fieldType);

  default FieldType getFieldTypeEnum() {
    String raw = getFieldType();
    if (raw == null || raw.isEmpty()) {
      throw new IllegalStateException("SOP field type is missing");
    }
    try {
      return FieldType.valueOf(raw);
    } catch (IllegalArgumentException ex) {
      throw new IllegalStateException("Invalid SOP field type: " + raw, ex);
    }
  }

  default void setFieldTypeEnum(FieldType fieldType) {
    setFieldType(fieldType == null ? null : fieldType.name());
  }

  default boolean isValidValue(String value) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    FieldType type = getFieldTypeEnum();

    switch (type) {
      case NUMBER:
        return isBigDecimal(value);

      case PERCENTAGE:
        BigDecimal pct = parseBigDecimal(value);
        return pct != null
            && pct.compareTo(BigDecimal.ZERO) >= 0
            && pct.compareTo(new BigDecimal("100")) <= 0;

      case TEXT:
      default:
        return true;
    }
  }

  static boolean isBigDecimal(String value) {
    return parseBigDecimal(value) != null;
  }

  static BigDecimal parseBigDecimal(String value) {
    return BigDecimalValidator.getInstance().validate(value);
  }
}
