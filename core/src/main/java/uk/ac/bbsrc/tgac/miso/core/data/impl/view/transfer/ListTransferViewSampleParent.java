package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSubproject;

@Entity
@Table(name = "Sample")
@Immutable
public class ListTransferViewSampleParent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "sampleId")
  private long id;

  @OneToOne
  @JoinColumn(name = "project_projectId")
  private ListTransferViewProject project;

  @ManyToOne
  @JoinColumn(name = "subprojectId")
  private ParentSubproject subproject;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ListTransferViewProject getProject() {
    return project;
  }

  public void setProject(ListTransferViewProject project) {
    this.project = project;
  }

  public ParentSubproject getSubproject() {
    return subproject;
  }

  public void setSubproject(ParentSubproject subproject) {
    this.subproject = subproject;
  }

}
