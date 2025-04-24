package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily_;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryIndexFamilyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibraryIndexFamilyDao extends HibernateSaveDao<LibraryIndexFamily>
    implements LibraryIndexFamilyDao {

  public HibernateLibraryIndexFamilyDao() {
    super(LibraryIndexFamily.class);
  }

  @Override
  public LibraryIndexFamily getByName(String name) throws IOException {
    return getBy(LibraryIndexFamily_.NAME, name);
  }

  @Override
  public long getUsage(LibraryIndexFamily indexFamily) throws IOException {
    LongQueryBuilder<LibraryImpl> builder = new LongQueryBuilder<>(currentSession(), LibraryImpl.class);
    Join<LibraryImpl, LibraryIndex> indexJoin = builder.getJoin(builder.getRoot(), LibraryImpl_.index1);
    Join<LibraryIndex, LibraryIndexFamily> indexFamilyJoin = builder.getJoin(indexJoin, LibraryIndex_.family);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(indexFamilyJoin.get(LibraryIndexFamily_.INDEX_FAMILY_ID),
            indexFamily.getId()));
    return builder.getCount();
  }

}
