package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSiblingNumberGenerator implements SiblingNumberGenerator {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public <T extends Aliasable> int getNextSiblingNumber(Class<T> clazz, String partialAlias) throws IOException {
    // Find highest existing siblingNumber matching this partialAlias
    Criteria criteria = currentSession().createCriteria(clazz);
    criteria.add(Restrictions.like("alias", partialAlias, MatchMode.START));
    @SuppressWarnings("unchecked")
    List<Aliasable> aliasables = criteria.list();
    String regex = "^.{" + partialAlias.length() + "}(\\d+)$";
    Pattern pattern = Pattern.compile(regex);
    int next = 0;
    for (Aliasable aliasable : aliasables) {
      Matcher m = pattern.matcher(aliasable.getAlias());
      if (!m.matches()) {
        continue;
      }
      int siblingNumber = Integer.parseInt(m.group(1));
      if (siblingNumber > next) {
        next = siblingNumber;
      }
    }
    next++;
    return next;
  }

}
