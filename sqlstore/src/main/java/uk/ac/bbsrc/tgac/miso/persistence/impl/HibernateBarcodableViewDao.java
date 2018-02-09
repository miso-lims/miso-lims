package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.persistence.BarcodableViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBarcodableViewDao implements BarcodableViewDao {
  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<BarcodableView> searchByBarcode(String barcode) {
    if (barcode == null)
      throw new IllegalArgumentException("Barcode cannot be null!");

    @SuppressWarnings("unchecked")
    List<BarcodableView> results = currentSession().createCriteria(BarcodableView.class)
        .add(Restrictions.eq("identificationBarcode", barcode)).list();

    return results;
  }

  @Override
  public List<BarcodableView> searchByBarcode(String barcode, Collection<EntityType> typeFilter) {
    if (barcode == null)
      throw new IllegalArgumentException("Barcode cannot be null!");

    Predicate<BarcodableView> matchesTypeFilter = barcodableView -> typeFilter.contains(barcodableView.getId().getTargetType());

    return searchByBarcode(barcode).stream().filter(matchesTypeFilter).collect(toList());
  }
}
