package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDetailedQcStatusDao extends HibernateSaveDao<DetailedQcStatus> implements DetailedQcStatusDao {

  public HibernateDetailedQcStatusDao() {
    super(DetailedQcStatus.class, DetailedQcStatusImpl.class);
  }

  @Override
  public DetailedQcStatus getByDescription(String description) {
    return getBy(DetailedQcStatusImpl_.DESCRIPTION, description);
  }

  @Override
  public long getUsageBySamples(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(SampleImpl.class, SampleImpl_.DETAILED_QC_STATUS, detailedQcStatus);
  }

  @Override
  public long getUsageByLibraries(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(LibraryImpl.class, LibraryImpl_.DETAILED_QC_STATUS, detailedQcStatus);
  }

  @Override
  public long getUsageByLibraryAliquots(DetailedQcStatus detailedQcStatus) {
    return getUsageBy(LibraryAliquot.class, LibraryAliquot_.DETAILED_QC_STATUS, detailedQcStatus);
  }

  @Override
  public List<DetailedQcStatus> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(DetailedQcStatusImpl_.DETAILED_QC_STATUS_ID, ids);
  }

}
