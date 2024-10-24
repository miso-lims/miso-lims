package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign_;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryDesignDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDesignDao extends HibernateSaveDao<LibraryDesign> implements LibraryDesignDao {

  public HibernateLibraryDesignDao() {
    super(LibraryDesign.class);
  }

  @Override
  public LibraryDesign getByNameAndSampleClass(String name, SampleClass sampleClass) throws IOException {
    QueryBuilder<LibraryDesign, LibraryDesign> builder =
        new QueryBuilder<>(currentSession(), LibraryDesign.class, LibraryDesign.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryDesign_.name), name));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryDesign_.sampleClass), sampleClass));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException {
    if (sampleClass == null)
      return Collections.emptyList();

    QueryBuilder<LibraryDesign, LibraryDesign> builder =
        new QueryBuilder<>(currentSession(), LibraryDesign.class, LibraryDesign.class);
    Join<LibraryDesign, SampleClassImpl> sampleClassJoin =
        builder.getJoin(builder.getRoot(), LibraryDesign_.sampleClass);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(sampleClassJoin.get(SampleClassImpl_.sampleClassId), sampleClass.getId()));
    return builder.getResultList();
  }

  @Override
  public List<LibraryDesign> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibraryDesign_.LIBRARY_DESIGN_ID, idList);
  }

  @Override
  public long getUsage(LibraryDesign design) throws IOException {
    return getUsageBy(DetailedLibraryImpl.class, DetailedLibraryImpl_.LIBRARY_DESIGN, design);
  }

}
