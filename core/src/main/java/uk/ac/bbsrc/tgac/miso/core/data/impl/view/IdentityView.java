package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Immutable
@Table(name = "Sample")
public class IdentityView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleId;

  @Column(name = "project_projectId")
  private long projectId;

  private String alias;
  private String externalName;

  private String discriminator;

  public long getId() {
    return sampleId;
  }

  public void setId(long id) {
    this.sampleId = id;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public void setDiscriminator(String discriminator) {
    this.discriminator = discriminator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sampleId, projectId, alias, externalName);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        IdentityView::getId,
        IdentityView::getProjectId,
        IdentityView::getAlias,
        IdentityView::getExternalName);
  }

}
