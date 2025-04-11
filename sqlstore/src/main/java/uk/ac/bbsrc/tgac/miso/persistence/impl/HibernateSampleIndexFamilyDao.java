package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleIndexFamilyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSampleIndexFamilyDao extends HibernateSaveDao<SampleIndexFamily> implements SampleIndexFamilyDao {

  public HibernateSampleIndexFamilyDao() {
    super(SampleIndexFamily.class);
  }

  @Override
  public SampleIndexFamily getByName(String name) throws IOException {
    return getBy(SampleIndexFamily_.NAME, name);
  }

}
