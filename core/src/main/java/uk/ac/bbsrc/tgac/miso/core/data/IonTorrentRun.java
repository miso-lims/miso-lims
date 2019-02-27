package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunIonTorrent")
public class IonTorrentRun extends Run {
  private static final long serialVersionUID = 1L;

  public IonTorrentRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.IONTORRENT;
  }

}
