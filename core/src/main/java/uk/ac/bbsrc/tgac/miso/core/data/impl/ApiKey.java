package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class ApiKey implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long keyId = UNSAVED_ID;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "userId")
  private User user;

  @Column(name = "apiKey")
  private String key;

  @Column(name = "apiSecret")
  private String secret;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @Override
  public long getId() {
    return keyId;
  }

  @Override
  public void setId(long id) {
    this.keyId = id;
  }

  @Override
  public boolean isSaved() {
    return keyId != UNSAVED_ID;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyId, user, key, secret);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        ApiKey::getId,
        ApiKey::getUser,
        ApiKey::getKey,
        ApiKey::getSecret);
  }

}
