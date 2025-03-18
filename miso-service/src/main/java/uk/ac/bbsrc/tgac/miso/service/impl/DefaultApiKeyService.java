package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ApiKeyService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.ApiKeyDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultApiKeyService implements ApiKeyService {

  private static final Pattern API_KEY_PATTERN = Pattern.compile("^(.{32})-(.{32})$");

  @Autowired
  private ApiKeyDao apiKeyDao;
  @Autowired
  private UserService userService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private SecureRandom secureRandom = new SecureRandom();

  @Override
  public ApiKey get(long id) throws IOException {
    return apiKeyDao.get(id);
  }

  @Override
  public List<ApiKey> list() throws IOException {
    return apiKeyDao.list();
  }

  @Override
  public String generateApiKey(String name) throws IOException {
    authorizationManager.throwIfNonAdmin();

    User user = userService.getByLoginName(name);
    if (user != null) {
      throw new ValidationException(
          new ValidationError("API key name must be unique and not belong to an existing user."));
    }
    user = new UserImpl();
    user.setActive(false);
    user.setInternal(false);
    user.setAdmin(false);
    user.setLoginName(name);
    user.setFullName(name);
    user.setPassword(null);
    // Roles are handled via ApiKeyAuthentication and not saved to the database
    userService.create(user);

    ApiKey result = new ApiKey();
    result.setUser(user);
    result.setKey(generateRandomString(32));
    String plainSecret = generateRandomString(32);
    result.setSecret(passwordEncoder.encode(plainSecret));
    result.setCreator(authorizationManager.getCurrentUser());
    result.setCreated(new Date());

    apiKeyDao.create(result);
    return "%s-%s".formatted(result.getKey(), plainSecret);
  }

  private String generateRandomString(int length) {
    byte[] bytes = new byte[length];
    secureRandom.nextBytes(bytes);
    String result = Base64.getEncoder().encodeToString(bytes);
    result = result.substring(0, length);
    return result;
  }

  @Override
  public ApiKey authenticate(String apiKeyString) throws IOException {
    if (apiKeyString == null) {
      return null;
    }
    Matcher m = API_KEY_PATTERN.matcher(apiKeyString);
    if (!m.matches()) {
      return null;
    }
    String key = m.group(1);
    String secret = m.group(2);

    ApiKey apiKey = apiKeyDao.getByKey(key);
    if (apiKey == null) {
      return null;
    }
    if (passwordEncoder.matches(secret, apiKey.getSecret())) {
      return apiKey;
    } else {
      return null;
    }
  }

  @Override
  public void bulkDelete(Collection<ApiKey> keys) throws IOException {
    authorizationManager.throwIfNonAdmin();
    // Note: can't delete the user, as it may be attached to changelogs
    for (ApiKey key : keys) {
      ApiKey managed = apiKeyDao.get(key.getId());
      apiKeyDao.delete(managed);
    }
  }

}
