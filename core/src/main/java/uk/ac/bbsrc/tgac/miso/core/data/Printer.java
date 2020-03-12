package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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
import javax.persistence.Transient;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Charsets;

import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelElement;

@Entity
@Table(name = "Printer")
public class Printer implements Deletable, Serializable {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Backend backend;

  private String configuration;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Driver driver;
  private boolean enabled;

  private double height;

  @Column(nullable = false)
  private String layout;

  @Transient
  private List<LabelElement> layoutCache;

  @Column(nullable = false)
  private String name;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long printerId = UNSAVED_ID;

  private double width;

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

  public double getHeight() {
    return height;
  }

  @Override
  public long getId() {
    return printerId;
  }

  public String getLayout() {
    return layout;
  }

  public String getName() {
    return name;
  }

  public double getWidth() {
    return width;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public long printBarcode(User user, int copies, Stream<Barcodable> barcodables)
      throws JsonParseException, JsonMappingException, IOException {
    AtomicLong counter = new AtomicLong();
    if (layoutCache == null) {
      layoutCache = MAPPER.readValue(layout,
          TypeFactory.defaultInstance().constructCollectionLikeType(List.class, LabelElement.class));
    }
    backend.print(barcodables//
        .map(b -> {
          final LabelCanvas canvas = driver.start(width, height);
          for (final LabelElement element : layoutCache) {
            element.draw(canvas, b);
          }
          return canvas.finish(copies);
        })//
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

  public void setHeight(double height) {
    this.height = height;
  }

  @Override
  public void setId(long printerId) {
    this.printerId = printerId;
  }

  public void setLayout(String layout) {
    this.layout = layout;
    layoutCache = null;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWidth(double width) {
    this.width = width;
  }

}
