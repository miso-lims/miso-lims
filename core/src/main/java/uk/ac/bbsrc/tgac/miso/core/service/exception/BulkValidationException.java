package uk.ac.bbsrc.tgac.miso.core.service.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Map<Integer, List<ValidationError>> errorsByRow;

  public BulkValidationException(Map<Integer, List<ValidationError>> errorsByRow) {
    super("Validation failed");
    this.errorsByRow = errorsByRow;
  }

  public Map<Integer, List<ValidationError>> getErrorsByRow() {
    return errorsByRow;
  }

  public Map<Integer, Map<String, List<String>>> getErrorsByRowAndField() {
    Map<Integer, Map<String, List<String>>> map = new HashMap<>();
    for (Integer row : errorsByRow.keySet()) {
      map.put(row, new HashMap<>());
      for (ValidationError error : errorsByRow.get(row)) {
        if (!map.get(row).containsKey(error.getProperty())) {
          map.get(row).put(error.getProperty(), new ArrayList<>());
        }
        map.get(row).get(error.getProperty()).add(error.getMessage());
      }
    }
    return map;
  }

}
