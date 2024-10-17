package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset_;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorksetDao extends HibernateSaveDao<Workset> implements WorksetStore {

  public HibernateWorksetDao() {
    super(Workset.class);
  }

  @Override
  public Workset getByAlias(String alias) {
    return getBy(Workset_.alias, alias);
  }

  @Override
  public List<Workset> listBySample(long sampleId) {
    QueryBuilder<Workset, Workset> builder = new QueryBuilder<>(currentSession(), Workset.class, Workset.class);
    Join<Workset, WorksetSample> worksetSample = builder.getJoin(builder.getRoot(), Workset_.worksetSamples);
    Join<WorksetSample, SampleImpl> sample = builder.getJoin(worksetSample, WorksetSample_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(sample.get(SampleImpl_.sampleId), sampleId));
    return builder.getResultList();
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) {
    QueryBuilder<Workset, Workset> builder = new QueryBuilder<>(currentSession(), Workset.class, Workset.class);
    Join<Workset, WorksetLibrary> worksetLibrary = builder.getJoin(builder.getRoot(), Workset_.worksetLibraries);
    Join<WorksetLibrary, LibraryImpl> library = builder.getJoin(worksetLibrary, WorksetLibrary_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(library.get(LibraryImpl_.libraryId), libraryId));
    return builder.getResultList();
  }

  @Override
  public List<Workset> listByLibraryAliquot(long aliquotId) {
    QueryBuilder<Workset, Workset> builder = new QueryBuilder<>(currentSession(), Workset.class, Workset.class);
    Join<Workset, WorksetLibraryAliquot> worksetLibraryAliquot =
        builder.getJoin(builder.getRoot(), Workset_.worksetLibraryAliquots);
    Join<WorksetLibraryAliquot, LibraryAliquot> aliquot =
        builder.getJoin(worksetLibraryAliquot, WorksetLibraryAliquot_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(aliquot.get(LibraryAliquot_.aliquotId), aliquotId));
    return builder.getResultList();
  }

  @Override
  public Map<Long, Date> getSampleAddedTimes(long worksetId) {
    QueryBuilder<Object[], WorksetSample> builder =
        new QueryBuilder<>(currentSession(), WorksetSample.class, Object[].class);
    Join<WorksetSample, Workset> workset = builder.getJoin(builder.getRoot(), WorksetSample_.workset);
    Join<WorksetSample, SampleImpl> sample = builder.getJoin(builder.getRoot(), WorksetSample_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(workset.get(Workset_.id), worksetId));
    builder.setColumns(sample.get(SampleImpl_.sampleId), builder.getRoot().get(WorksetSample_.addedTime));
    return filterResults(builder.getResultList());
  }

  @Override
  public Map<Long, Date> getLibraryAddedTimes(long worksetId) {
    QueryBuilder<Object[], WorksetLibrary> builder =
        new QueryBuilder<>(currentSession(), WorksetLibrary.class, Object[].class);
    Join<WorksetLibrary, Workset> workset = builder.getJoin(builder.getRoot(), WorksetLibrary_.workset);
    Join<WorksetLibrary, LibraryImpl> library = builder.getJoin(builder.getRoot(), WorksetLibrary_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(workset.get(Workset_.id), worksetId));
    builder.setColumns(library.get(LibraryImpl_.libraryId), builder.getRoot().get(WorksetLibrary_.addedTime));
    return filterResults(builder.getResultList());
  }

  @Override
  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) {
    QueryBuilder<Object[], WorksetLibraryAliquot> builder =
        new QueryBuilder<>(currentSession(), WorksetLibraryAliquot.class, Object[].class);
    Join<WorksetLibraryAliquot, Workset> workset = builder.getJoin(builder.getRoot(), WorksetLibraryAliquot_.workset);
    Join<WorksetLibraryAliquot, LibraryAliquot> aliquot =
        builder.getJoin(builder.getRoot(), WorksetLibraryAliquot_.item);
    builder.addPredicate(builder.getCriteriaBuilder().equal(workset.get(Workset_.id), worksetId));
    builder.setColumns(aliquot.get(LibraryAliquot_.aliquotId), builder.getRoot().get(WorksetLibraryAliquot_.addedTime));
    return filterResults(builder.getResultList());
  }

  private Map<Long, Date> filterResults(List<Object[]> results) {
    return results.stream()
        .filter(row -> row[1] != null)
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Date) row[1]));
  }

}
