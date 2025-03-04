package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey_;
import uk.ac.bbsrc.tgac.miso.persistence.ApiKeyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateApiKeyDao extends HibernateSaveDao<ApiKey> implements ApiKeyDao {

  public HibernateApiKeyDao() {
    super(ApiKey.class);
  }

  @Override
  public ApiKey getByKey(String key) throws IOException {
    return getBy(ApiKey_.key, key);
  }

  @Override
  public void delete(ApiKey apiKey) throws IOException {
    currentSession().remove(apiKey);
  }

}
