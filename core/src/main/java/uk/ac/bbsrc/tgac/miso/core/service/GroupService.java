package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;

public interface GroupService extends DeleterService<Group>, SaveService<Group> {

  public List<Group> list() throws IOException;

  public void updateMembers(Group group) throws IOException;

}
