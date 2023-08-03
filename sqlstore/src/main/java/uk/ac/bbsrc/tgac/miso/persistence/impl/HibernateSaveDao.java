package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public abstract class HibernateSaveDao<T extends Identifiable> extends HibernateProviderDao<T> implements SaveDao<T> {

  public HibernateSaveDao(Class<T> resultClass, Class<? extends T> entityClass) {
    super(resultClass, entityClass);
  }

  public HibernateSaveDao(Class<T> resultClass) {
    super(resultClass);
  }

  @Override
  public long create(T object) throws IOException {
    return (long) currentSession().save(object);
  }

  @Override
  public long update(T object) throws IOException {
    currentSession().update(object);
    return object.getId();
  }

}
