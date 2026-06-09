package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.SampleSheetDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleSheetDao extends HibernateSaveDao<SampleSheet> implements SampleSheetDao {

  public HibernateSampleSheetDao() {
    super(SampleSheet.class);
  }

  @Override
  public SampleSheet getByName(String name) throws IOException {
    return getBy(SampleSheet_.name, name);
  }

  @Override
  public List<SampleSheet> listByPlatform(PlatformType platform) throws IOException {
    return listBy(SampleSheet_.platformType, platform);
  }

}
