package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunIonTorrent")
public class IonTorrentRun extends Run {
  private static final long serialVersionUID = 1L;

  public IonTorrentRun() {
    super();
  }

  public IonTorrentRun(User user) {
    super(user);
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.IONTORRENT;
  }

}
