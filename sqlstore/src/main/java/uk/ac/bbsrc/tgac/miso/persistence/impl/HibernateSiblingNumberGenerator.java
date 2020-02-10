package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
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
    Set<Integer> siblingNumbers = getExistingSiblingNumbers(clazz, partialAlias);
    Integer max = siblingNumbers.stream().max(Integer::compare).orElse(0);
    return max + 1;
  }

  @Override
  public <T extends Aliasable> int getFirstAvailableSiblingNumber(Class<T> clazz, String partialAlias) throws IOException {
    Set<Integer> siblingNumbers = getExistingSiblingNumbers(clazz, partialAlias);
    int next = 1;
    while (siblingNumbers.contains(next)) {
      next++;
    }
    return next;
  }

  public <T extends Aliasable> Set<Integer> getExistingSiblingNumbers(Class<T> clazz, String partialAlias) throws IOException {
    @SuppressWarnings("unchecked")
    List<String> results = currentSession().createCriteria(clazz)
        .add(Restrictions.like("alias", partialAlias, MatchMode.START))
        .setProjection(Projections.property("alias"))
        .list();

    String regex = "^.{" + partialAlias.length() + "}(\\d+)$";
    Pattern pattern = Pattern.compile(regex);
    return results.stream()
        .map(alias -> {
          Matcher m = pattern.matcher(alias);
          if (!m.matches()) {
            return null;
          }
          return Integer.parseInt(m.group(1));
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

}
