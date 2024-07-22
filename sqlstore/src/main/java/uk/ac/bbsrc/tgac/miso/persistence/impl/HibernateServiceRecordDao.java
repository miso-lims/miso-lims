package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.persistence.ServiceRecordStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateServiceRecordDao extends HibernateSaveDao<ServiceRecord> implements ServiceRecordStore {

  public HibernateServiceRecordDao() {
    super(ServiceRecord.class);
  }

}
