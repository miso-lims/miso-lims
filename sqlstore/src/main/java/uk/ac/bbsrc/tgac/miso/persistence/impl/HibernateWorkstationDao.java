package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.WorkstationDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorkstationDao extends HibernateSaveDao<Workstation> implements WorkstationDao {

  public HibernateWorkstationDao() {
    super(Workstation.class);
  }

  @Override
  public Workstation getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(Workstation workstation) throws IOException {
    return getUsageBy(LibraryImpl.class, "workstation", workstation);
  }

}
