package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.IOException;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface Timestamped {

  public User getLastModifier();

  public void setLastModifier(User user);

  public Date getLastModified();

  public void setLastModified(Date lastModified);

  public User getCreator();

  public void setCreator(User user);

  public Date getCreationTime();

  public void setCreationTime(Date creationTime);

  public boolean isSaved();

  /**
   * Updates all user data and timestamps associated with the change. Existing timestamps will be preserved
   * if the object is unsaved, and they are already set; otherwise, they are set to the current time
   * 
   * @param user User to associate with the change
   * @throws IOException
   */
  public default void setChangeDetails(User user) {
    Date now = new Date();
    setLastModifier(user);

    if (!isSaved()) {
      setCreator(user);
      if (getCreationTime() == null) {
        setCreationTime(now);
        setLastModified(now);
      } else if (getLastModified() == null) {
        setLastModified(now);
      }
    } else {
      setLastModified(now);
    }
  }

}
