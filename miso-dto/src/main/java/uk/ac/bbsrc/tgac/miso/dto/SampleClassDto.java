package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.hasStockParent;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleClassDto implements WritableUrls {

  private Long id;
  private String url;
  private String alias;
  private String sampleCategory;
  private String suffix;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private Boolean dnaseTreatable;
  private boolean canCreateNew;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

  public String getCreatedByUrl() {
    return createdByUrl;
  }

  public void setCreatedByUrl(String createdByUrl) {
    this.createdByUrl = createdByUrl;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getUpdatedByUrl() {
    return updatedByUrl;
  }

  public void setUpdatedByUrl(String updatedByUrl) {
    this.updatedByUrl = updatedByUrl;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Long getCreatedById() {
    return createdById;
  }

  public void setCreatedById(Long createdById) {
    this.createdById = createdById;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public Boolean getDNAseTreatable() {
    return dnaseTreatable;
  }

  public void setDNAseTreatable(Boolean dnaseTreatable) {
    this.dnaseTreatable = dnaseTreatable;
  }

  public boolean getCanCreateNew() {
    return canCreateNew;
  }

  public void setCanCreateNew(boolean canCreateNew) {
    this.canCreateNew = canCreateNew;
  }

  public void setCanCreateNew(Iterable<SampleValidRelationship> relationships) {
    setCanCreateNew((getSampleCategory().equals(SampleTissue.CATEGORY_NAME)
        || getSampleCategory().equals(SampleStock.CATEGORY_NAME)
        || getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)
            && hasStockParent(getId(), relationships)));
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/sampleclass/{id}", getId()));
    setCreatedByUrl(WritableUrls.buildUriPath(baseUri, "/rest/user/{id}", getCreatedById()));
    setUpdatedByUrl(WritableUrls.buildUriPath(baseUri, "/rest/user/{id}", getUpdatedById()));
  }

  @Override
  public String toString() {
    return "SampleClassDto [id=" + id + ", url=" + url + ", alias=" + alias + ", sampleCategory=" + sampleCategory + ", suffix=" + suffix
        + ", createdById=" + createdById + ", createdByUrl=" + createdByUrl + ", creationDate=" + creationDate + ", updatedById="
        + updatedById + ", updatedByUrl=" + updatedByUrl + ", lastUpdated=" + lastUpdated + ", dnaseTreatable=" + dnaseTreatable + "]";
  }
}
