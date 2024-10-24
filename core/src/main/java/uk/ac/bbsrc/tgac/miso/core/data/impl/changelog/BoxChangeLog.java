package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;

@Entity
@Table(name = "BoxChangeLog",
    indexes = {@Index(name = "BoxChangeLog_boxId_changeTime", columnList = "boxId, changeTime")})
public class BoxChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long boxChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = BoxImpl.class)
  @JoinColumn(name = "boxId", nullable = false, updatable = false)
  private Box box;

  @Override
  public Long getId() {
    return box.getId();
  }

  @Override
  public void setId(Long id) {
    box.setId(id);
  }

  public Long getBoxChangeLogId() {
    return boxChangeLogId;
  }

  public Box getBox() {
    return box;
  }

  public void setBox(Box box) {
    this.box = box;
  }

}
