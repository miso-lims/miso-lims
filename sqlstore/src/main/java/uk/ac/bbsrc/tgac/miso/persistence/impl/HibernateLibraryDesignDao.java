package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryDesignDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDesignDao extends HibernateSaveDao<LibraryDesign> implements LibraryDesignDao {

  public HibernateLibraryDesignDao() {
    super(LibraryDesign.class);
  }

  @Override
  public LibraryDesign getByNameAndSampleClass(String name, SampleClass sampleClass) throws IOException {
    return (LibraryDesign) currentSession().createCriteria(LibraryDesign.class)
        .add(Restrictions.eq("name", name))
        .add(Restrictions.eq("sampleClass", sampleClass))
        .uniqueResult();
  }

  @Override
  public List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException {
    if (sampleClass == null) return Collections.emptyList();
    Criteria criteria = currentSession().createCriteria(LibraryDesign.class);
    criteria.createAlias("sampleClass", "sampleClass");
    criteria.add(Restrictions.eq("sampleClass.id", sampleClass.getId()));
    @SuppressWarnings("unchecked")
    List<LibraryDesign> rules = criteria.list();
    return rules;
  }

  @Override
  public List<LibraryDesign> listByIdList(List<Long> idList) throws IOException {
    return listByIdList("libraryDesignId", idList);
  }

  @Override
  public long getUsage(LibraryDesign design) throws IOException {
    return getUsageBy(DetailedLibraryImpl.class, "libraryDesign", design);
  }

}
