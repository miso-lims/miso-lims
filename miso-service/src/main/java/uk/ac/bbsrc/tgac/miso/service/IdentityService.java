package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public interface IdentityService {

  Identity get(Long identityId);

  Identity get(String externalName);

  Long create(Identity identity) throws IOException;

  void update(Identity identity) throws IOException;

  Set<Identity> getAll();

  void delete(Long identityId);

}