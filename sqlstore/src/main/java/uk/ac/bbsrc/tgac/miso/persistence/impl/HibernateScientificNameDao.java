package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.ScientificNameDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateScientificNameDao extends HibernateSaveDao<ScientificName> implements ScientificNameDao {

  public HibernateScientificNameDao() {
    super(ScientificName.class);
  }

  @Override
  public ScientificName getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsageBySamples(ScientificName scientificName) throws IOException {
    return getUsageBy(SampleImpl.class, "scientificName", scientificName);
  }

  @Override
  public long getUsageByReferenceGenomes(ScientificName scientificName) throws IOException {
    return getUsageBy(ReferenceGenomeImpl.class, "defaultScientificName", scientificName);
  }

}
