package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Charsets;

import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;

@Entity
@Table(name = "Printer")
public class Printer implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  public static final long UNSAVED_ID = 0;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Backend backend;

  private String configuration;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Driver driver;
  private boolean enabled;
  @Column(nullable = false)
  private String name;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long printerId = UNSAVED_ID;

  public Backend getBackend() {
    return backend;
  }

  public String getConfiguration() {
    return configuration;
  }

  public Driver getDriver() {
    return driver;
  }

  public long getId() {
    return printerId;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean isDeletable() {
    return printerId != UNSAVED_ID;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean printBarcode(Barcodable b) {
    return backend.print(driver.encode(b).getBytes(Charsets.US_ASCII), configuration);
  }

  public void setBackend(Backend backend) {
    this.backend = backend;
  }

  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  public void setDriver(Driver driver) {
    this.driver = driver;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setId(long printerId) {
    this.printerId = printerId;
  }

  public void setName(String name) {
    this.name = name;
  }

}
