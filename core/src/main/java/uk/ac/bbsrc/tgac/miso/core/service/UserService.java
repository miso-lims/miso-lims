package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import com.eaglegenomics.simlims.core.User;

public interface UserService extends DeleterService<User>, SaveService<User> {

  public User getByLoginName(String loginName) throws IOException;

  public List<User> list() throws IOException;

}
