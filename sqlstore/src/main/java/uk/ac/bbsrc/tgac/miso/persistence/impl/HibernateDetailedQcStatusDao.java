package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDetailedQcStatusDao extends HibernateSaveDao<DetailedQcStatus> implements DetailedQcStatusDao {

  public HibernateDetailedQcStatusDao() {
    super(DetailedQcStatus.class, DetailedQcStatusImpl.class);
  }

  @Override
  public DetailedQcStatus getByDescription(String description) {
    return getBy("description", description);
  }

  @Override
  public long getUsageBySamples(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(detailedQcStatus, SampleImpl.class);
  }

  @Override
  public long getUsageByLibraries(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(detailedQcStatus, LibraryImpl.class);
  }

  @Override
  public long getUsageByLibraryAliquots(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(detailedQcStatus, LibraryAliquot.class);
  }

  @Override
  public List<DetailedQcStatus> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("detailedQcStatusId", ids);
  }

  private long getUsageBy(DetailedQcStatus detailedQcStatus, Class<?> user) {
    return (long) currentSession().createCriteria(user)
        .add(Restrictions.eq("detailedQcStatus", detailedQcStatus))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
