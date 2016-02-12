package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;

public interface IdentityService {

  Identity get(Long identityId);

  Long create(Identity identity) throws IOException;

  void update(Identity identity) throws IOException;

  Set<Identity> getAll();

  void delete(Long identityId);

}