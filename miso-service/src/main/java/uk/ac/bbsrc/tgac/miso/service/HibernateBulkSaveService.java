package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.impl.util.HibernateSessionManager;

@Transactional(rollbackFor = Exception.class)
public interface HibernateBulkSaveService<T extends Identifiable> extends BulkSaveService<T> {

  public List<T> listByIdList(List<Long> ids) throws IOException;

  public HibernateSessionManager getHibernateSessionManager();

  @Override
  public default List<T> bulkCreate(List<T> items) throws IOException {
    List<Long> savedIds = new ArrayList<>();
    Map<Integer, List<ValidationError>> errorsByRow = new HashMap<>();
    for (int i = 0; i < items.size(); i++) {
      try {
        savedIds.add(create(items.get(i)));
      } catch (ValidationException validationException) {
        errorsByRow.put(i, validationException.getErrors());
      }
    }
    if (errorsByRow.isEmpty()) {
      getHibernateSessionManager().flushAndClear();
      return listByIdList(savedIds);
    } else {
      throw new BulkValidationException(errorsByRow);
    }
  }

  @Override
  public default List<T> bulkUpdate(List<T> items) throws IOException {
    List<Long> savedIds = new ArrayList<>();
    Map<Integer, List<ValidationError>> errorsByRow = new HashMap<>();
    for (int i = 0; i < items.size(); i++) {
      try {
        savedIds.add(update(items.get(i)));
      } catch (ValidationException validationException) {
        errorsByRow.put(i, validationException.getErrors());
      }
    }
    if (errorsByRow.isEmpty()) {
      getHibernateSessionManager().flushAndClear();
      return listByIdList(savedIds);
    } else {
      throw new BulkValidationException(errorsByRow);
    }
  }

}
