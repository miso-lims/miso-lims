package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingControlTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencingControlTypeDao extends HibernateSaveDao<SequencingControlType> implements SequencingControlTypeDao {

  public HibernateSequencingControlTypeDao() {
    super(SequencingControlType.class);
  }

}
