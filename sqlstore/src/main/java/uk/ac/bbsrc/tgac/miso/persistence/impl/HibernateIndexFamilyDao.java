package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import javax.persistence.criteria.Join;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily_;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.IndexFamilyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateIndexFamilyDao extends HibernateSaveDao<IndexFamily> implements IndexFamilyDao {

  public HibernateIndexFamilyDao() {
    super(IndexFamily.class);
  }

  @Override
  public IndexFamily getByName(String name) throws IOException {
    return getBy(IndexFamily_.NAME, name);
  }

  @Override
  public long getUsage(IndexFamily indexFamily) throws IOException {
    LongQueryBuilder<LibraryImpl> builder = new LongQueryBuilder<>(currentSession(), LibraryImpl.class);
    Join<LibraryImpl, Index> indexJoin = builder.getJoin(builder.getRoot(), LibraryImpl_.index1);
    Join<Index, IndexFamily> indexFamilyJoin = builder.getJoin(indexJoin, Index_.family);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(indexFamilyJoin.get(IndexFamily_.INDEX_FAMILY_ID), indexFamily.getId()));
    return builder.getCount();
  }

}
