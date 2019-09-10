package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissuePieceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.TissuePieceType;
import uk.ac.bbsrc.tgac.miso.persistence.TissuePieceTypeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateTissuePieceTypeDao extends HibernateSaveDao<TissuePieceType> implements TissuePieceTypeDao {

  public HibernateTissuePieceTypeDao() {
    super(TissuePieceType.class);
  }

  @Override
  public long getUsage(TissuePieceType type) throws IOException {
    return getUsageBy(SampleTissuePieceImpl.class, "tissuePieceType", type);
  }

  @Override
  public TissuePieceType getByName(String name) {
    return getBy("name", name);
  }

}
