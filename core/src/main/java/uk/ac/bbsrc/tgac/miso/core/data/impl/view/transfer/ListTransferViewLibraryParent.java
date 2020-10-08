package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "Library")
@Immutable
public class ListTransferViewLibraryParent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "libraryId")
  private long id;

  @OneToOne
  @JoinColumn(name = "sample_sampleId")
  private ListTransferViewSampleParent sample;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ListTransferViewSampleParent getSample() {
    return sample;
  }

  public void setSample(ListTransferViewSampleParent sample) {
    this.sample = sample;
  }

}
