package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;

public interface ApiKeyDao extends SaveDao<ApiKey> {

  ApiKey getByKey(String key) throws IOException;

  void delete(ApiKey apiKey) throws IOException;

}
