package uk.ac.bbsrc.tgac.miso.core.data.type;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;

public enum RequiredPoolField {

  SIZE("dnaSize") {
    @Override
    public void collectValidationErrors(Pool pool, List<ValidationError> errors) {
      if (pool.getDnaSize() == null) {
        errors.add(ValidationError.forRequired("dnaSize"));
      }
    }
  };

  private final String fieldName;

  RequiredPoolField(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public abstract void collectValidationErrors(Pool pool, List<ValidationError> errors);

}
