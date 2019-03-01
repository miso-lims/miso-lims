package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;
import com.google.common.base.Charsets;

import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Layout;

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
  @Enumerated(EnumType.STRING)
  private Layout layout;

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

  @Override
  public String getDeleteDescription() {
    return getName() + " (" + getDriver().name() + "/" + getBackend().name() + ")";
  }

  @Override
  public String getDeleteType() {
    return "Printer";
  }

  public Driver getDriver() {
    return driver;
  }

  @Override
  public long getId() {
    return printerId;
  }

  public Layout getLayout() {
    return layout;
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public long printBarcode(User user, int copies, Stream<Barcodable> barcodables) {
    AtomicLong counter = new AtomicLong();
    backend.print(barcodables//
        .map(b -> layout.draw(driver, b).finish(copies))//
        .peek(x -> counter.incrementAndGet())//
        .collect(Collectors.joining())//
        .getBytes(Charsets.US_ASCII), configuration, user);
    return counter.get();
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

  @Override
  public void setId(long printerId) {
    this.printerId = printerId;
  }

  public void setLayout(Layout layout) {
    this.layout = layout;
  }

  public void setName(String name) {
    this.name = name;
  }

}
