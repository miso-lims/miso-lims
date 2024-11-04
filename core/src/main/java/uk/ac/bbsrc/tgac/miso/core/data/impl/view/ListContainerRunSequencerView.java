package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Instrument")
@Immutable
public class ListContainerRunSequencerView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long instrumentId;

  private String name;

  public long getId() {
    return instrumentId;
  }

  public void setId(long instrumentId) {
    this.instrumentId = instrumentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
