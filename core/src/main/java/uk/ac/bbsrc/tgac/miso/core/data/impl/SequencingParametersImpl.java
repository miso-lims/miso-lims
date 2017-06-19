package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

@Entity
@Table(name = "SequencingParameters")
public class SequencingParametersImpl implements SequencingParameters {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long parametersId;

  @Column(nullable = false)
  private String name;

  private String xpath;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Transient
  private transient XPathExpression expression;

  @ManyToOne(targetEntity = PlatformImpl.class)
  @JoinColumn(name = "platformId")
  private Platform platform;

  @Override
  public int compareTo(SequencingParameters o) {
    return name.compareTo(o.getName());
  }

  @Override
  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public Long getId() {
    return parametersId;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public String getXPath() {
    return xpath;
  }

  @Override
  public boolean matches(Document document) throws XPathExpressionException {
    if (xpath == null) {
      return false;
    }
    if (expression == null) {
      expression = XPathFactory.newInstance().newXPath().compile(xpath);
    }
    return (Boolean) expression.evaluate(document, XPathConstants.BOOLEAN);
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public void setCreationDate(Date creation) {
    this.creationDate = creation;
  }

  @Override
  public void setId(Long id) {
    this.parametersId = id;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public void setXPath(String xpath) {
    expression = null;
    this.xpath = xpath;
  }

}
