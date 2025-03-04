package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;

public interface ApiKeyService extends ListService<ApiKey> {

  /**
   * Generates a new API Key
   * 
   * @param name User/Service name to associate with the key
   * @return The plaintext API Key that should be used to authenticate. Because the secret portion of
   *         the key is hashed for storage, it will not be possible to recover the plaintext version
   *         again after this.
   * @throws IOException
   */
  String generateApiKey(String name) throws IOException;

  /**
   * Attempts to resolve an API key String to an ApiKey
   * 
   * @param apiKeyString a String containing the key and plaintext secret, separated by a hyphen
   * @return the matching ApiKey if valid; null otherwise
   * @throws IOException
   */
  ApiKey authenticate(String apiKeyString) throws IOException;

  public void bulkDelete(Collection<ApiKey> keys) throws IOException;

}
