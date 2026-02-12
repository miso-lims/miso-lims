package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet_;
import uk.ac.bbsrc.tgac.miso.persistence.ProbeSetDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProbeSetDao extends HibernateSaveDao<ProbeSet> implements ProbeSetDao {

  public HibernateProbeSetDao() {
    super(ProbeSet.class);
  }

  @Override
  public ProbeSet getByName(String name) throws IOException {
    return getBy(ProbeSet_.name, name);
  }

  @Override
  public List<ProbeSet> searchByName(String name) throws IOException {
    return searchBy(name, false, ProbeSet_.name);
  }

  @Override
  public List<ProbeSet> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(ProbeSet_.probeSetId, idList);
  }

}
