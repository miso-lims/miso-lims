package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;

public interface IdentityService {

  Identity get(Long identityId) throws IOException;

  Identity get(String externalName);

  Set<Identity> getAll() throws IOException;

  void delete(Long identityId) throws IOException;
  
  /**
   * copies all the editable properties from one Identity instance to another
   * 
   * @param target the persisted Identity to copy changes into
   * @param source the modified Identity to copy changes from
   */
  public void applyChanges(Identity target, Identity source);

}