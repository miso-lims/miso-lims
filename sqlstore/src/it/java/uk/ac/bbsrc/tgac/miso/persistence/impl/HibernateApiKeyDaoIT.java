package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateApiKeyDaoIT extends AbstractHibernateSaveDaoTest<ApiKey, HibernateApiKeyDao> {

  public HibernateApiKeyDaoIT() {
    super(ApiKey.class, 1L, 1);
  }

  @Override
  public HibernateApiKeyDao constructTestSubject() {
    HibernateApiKeyDao dao = new HibernateApiKeyDao();
    dao.setEntityManager(getEntityManager());
    return dao;
  }

  @Override
  public ApiKey getCreateItem() {
    User admin = currentSession().get(UserImpl.class, 1L);
    ApiKey key = new ApiKey();
    key.setKey("asdf");
    key.setSecret("ghjk");
    // normally the API key would have its own user, but it doesn't matter for DAO testing
    key.setUser(admin);
    key.setCreator(admin);
    key.setCreated(new Date());
    return key;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<ApiKey, String> getUpdateParams() {
    // Note: We're never actually updating the ApiKey, but the DAO does support it
    return new UpdateParameters<ApiKey, String>(1L, ApiKey::getKey, ApiKey::setKey, "Changed");
  }

  @Test
  public void testGetByKey() throws Exception {
    String key = "LwTTBMu4QSOeTWp7Uo7Bva0Jm0+Zpnxb";
    ApiKey result = getTestSubject().getByKey(key);
    assertNotNull(result);
    assertEquals(key, result.getKey());
  }

  @Test
  public void testDelete() throws Exception {
    ApiKey beforeDelete = currentSession().get(ApiKey.class, 1L);
    assertNotNull(beforeDelete);

    getTestSubject().delete(beforeDelete);
    clearSession();

    ApiKey afterDelete = currentSession().get(ApiKey.class, 1L);
    assertNull(afterDelete);
  }

}
