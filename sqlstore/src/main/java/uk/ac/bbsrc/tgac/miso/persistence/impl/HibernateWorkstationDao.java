package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import uk.ac.bbsrc.tgac.miso.core.data.RunSopFieldValue;
import uk.ac.bbsrc.tgac.miso.core.data.RunSopFieldValue_;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSopFieldValue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSopFieldValue_;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.SopField_;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.WorkstationDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorkstationDao extends HibernateSaveDao<Workstation> implements WorkstationDao {

  public HibernateWorkstationDao() {
    super(Workstation.class);
  }

  @Override
  public Workstation getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(Workstation workstation) throws IOException {
    return getUsageBy(LibraryImpl.class, "workstation", workstation);
  }

  @Override
  public long getUsageByRunSopFieldValues(Workstation workstation) throws IOException {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<RunSopFieldValue> root = query.from(RunSopFieldValue.class);
    Join<RunSopFieldValue, SopField> sopFieldJoin = root.join(RunSopFieldValue_.sopField);
    query.select(builder.count(root))
        .where(
            builder.equal(sopFieldJoin.get(SopField_.fieldType), SopField.FieldType.WORKSTATION),
            builder.equal(root.get(RunSopFieldValue_.value), Long.toString(workstation.getId())));
    return currentSession().createQuery(query).getSingleResult();
  }

  @Override
  public long getUsageBySampleSopFieldValues(Workstation workstation) throws IOException {
    CriteriaBuilder builder = currentSession().getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<SampleSopFieldValue> root = query.from(SampleSopFieldValue.class);
    Join<SampleSopFieldValue, SopField> sopFieldJoin = root.join(SampleSopFieldValue_.sopField);
    query.select(builder.count(root))
        .where(
            builder.equal(sopFieldJoin.get(SopField_.fieldType), SopField.FieldType.WORKSTATION),
            builder.equal(root.get(SampleSopFieldValue_.value), Long.toString(workstation.getId())));
    return currentSession().createQuery(query).getSingleResult();
  }

  @Override
  public List<Workstation> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("workstationId", ids);
  }
}
