package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;

public interface IdentityDao {

  List<Identity> getIdentity();

  Identity getIdentity(Long id);

  Long addIdentity(Identity identity);

  void deleteIdentity(Identity identity);

  void update(Identity identity);

}