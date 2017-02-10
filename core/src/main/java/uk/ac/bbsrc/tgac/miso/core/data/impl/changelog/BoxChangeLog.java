package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;

@Entity
@Table(appliesTo = "BoxChangeLog", indexes = { @Index(name = "BoxChangeLog_boxId_changeTime", columnNames = { "boxId", "changeTime" }) })
public class BoxChangeLog extends AbstractChangeLog {

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
