package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.User;

public interface SequencingParameters extends Comparable<SequencingParameters> {

  public User getCreatedBy();

  public Date getCreationDate();

  public Long getId();

  public Date getLastUpdated();

  public String getName();

  public Platform getPlatform();

  /**
   * Gets the platform ID. This exists due to platforms not being in Hibernate.
   */
  public Long getPlatformId();

  public User getUpdatedBy();

  String getXPath();

  boolean matches(Document document) throws XPathExpressionException;

  public void setCreatedBy(User createdby);

  public void setCreationDate(Date creation);

  public void setId(Long id);

  public void setLastUpdated(Date lastUpdated);

  public void setName(String string);

  public void setPlatform(Platform platform);

  public void setUpdatedBy(User updatedBy);

  void setXPath(String xpath);
}
