package uk.ac.bbsrc.tgac.miso.core.test;

import java.util.Collection;
import java.util.Collections;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;

public class MockFormTestIndexService implements IndexService {
  private static IndexFamily TRUSEQ = new IndexFamily();

  static {
    TRUSEQ.setName("TruSeq Single Index");
    TRUSEQ.setPlatformType(PlatformType.ILLUMINA);
    Index index = new Index();
    index.setId(1);
    index.setName("Index 1");
    index.setPosition(1);
    index.setSequence("AAAAAA");
    index.setFamily(TRUSEQ);
    TRUSEQ.setIndices(Collections.singletonList(index));
  }

  @Override
  public IndexFamily getIndexFamilyByName(String strategyName) {
    return strategyName.equals(TRUSEQ.getName()) ? TRUSEQ : null;
  }

  @Override
  public Collection<IndexFamily> getIndexFamilies() {
    return Collections.singleton(TRUSEQ);
  }

  @Override
  public Collection<IndexFamily> getIndexFamiliesByPlatform(PlatformType platformType) {
    if (platformType == TRUSEQ.getPlatformType()) {
      return Collections.singleton(TRUSEQ);
    } else {
      return Collections.emptySet();
    }
  }

  @Override
  public Index getIndexById(long id) {
    return null;
  }

  @Override
  public Collection<Index> listAllIndices(PlatformType platformType) {
    if (platformType == TRUSEQ.getPlatformType()) {
      return TRUSEQ.getIndices();
    } else {
      return Collections.emptySet();
    }
  }
}
