package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DuplicateBarcodes;
import uk.ac.bbsrc.tgac.miso.core.store.DuplicateBarcodesStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDuplicateBarcodesDao extends BaseHibernateDao<DuplicateBarcodes> implements DuplicateBarcodesStore {

  public HibernateDuplicateBarcodesDao() {
    super(DuplicateBarcodes.class);
  }

  @Override
  public Collection<DuplicateBarcodes> listAll() throws IOException {
    Criteria criteria = createCriteria();
    @SuppressWarnings("unchecked")
    List<DuplicateBarcodes> results = criteria.list();
    return results;
  }

}
