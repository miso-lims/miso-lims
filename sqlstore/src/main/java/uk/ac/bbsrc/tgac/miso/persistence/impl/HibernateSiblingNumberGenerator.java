package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.service.naming.SiblingNumberGenerator;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSiblingNumberGenerator implements SiblingNumberGenerator {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public <T extends Aliasable> int getNextSiblingNumber(Class<T> clazz, String partialAlias) throws IOException {
    Set<Integer> siblingNumbers = getExistingSiblingNumbers(clazz, partialAlias);
    Integer max = siblingNumbers.stream().max(Integer::compare).orElse(0);
    return max + 1;
  }

  @Override
  public <T extends Aliasable> int getFirstAvailableSiblingNumber(Class<T> clazz, String partialAlias)
      throws IOException {
    Set<Integer> siblingNumbers = getExistingSiblingNumbers(clazz, partialAlias);
    int next = 1;
    while (siblingNumbers.contains(next)) {
      next++;
    }
    return next;
  }

  private <T extends Aliasable> Set<Integer> getExistingSiblingNumbers(Class<T> clazz, String partialAlias)
      throws IOException {
    QueryBuilder<String, T> builder = new QueryBuilder<>(currentSession(), clazz, String.class);
    builder.addPredicate(builder.getCriteriaBuilder().like(builder.getRoot().get("alias"), partialAlias + '%'));
    builder.setColumn(builder.getRoot().get("alias"));
    List<String> results = builder.getResultList();

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
