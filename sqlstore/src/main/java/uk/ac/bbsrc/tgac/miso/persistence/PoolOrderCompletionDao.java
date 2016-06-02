package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.HibernateException;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;

public interface PoolOrderCompletionDao {

  public Collection<PoolOrderCompletion> getForPool(Long poolId) throws HibernateException, IOException;

  public Collection<PoolOrderCompletion> list() throws HibernateException, IOException;
}
