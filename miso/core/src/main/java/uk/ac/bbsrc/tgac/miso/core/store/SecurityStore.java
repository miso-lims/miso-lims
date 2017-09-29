package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Map;

public interface SecurityStore extends com.eaglegenomics.simlims.core.store.SecurityStore {

  /**
   * @return a map containing all column names and max lengths from the User table
   * @throws IOException
   */
  public Map<String, Integer> getUserColumnSizes() throws IOException;
  
  /**
   * @return a map containing all column names and max lengths from the Group table
   * @throws IOException
   */
  public Map<String, Integer> getGroupColumnSizes() throws IOException;
  
}
