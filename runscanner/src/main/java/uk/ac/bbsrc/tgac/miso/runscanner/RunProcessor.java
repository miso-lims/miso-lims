package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;

public abstract class RunProcessor {

  // TODO add runprocessors as you create them
  public static final Iterable<RunProcessor> INSTANCES = Arrays.asList();

  private final String name;

  private final PlatformType platformType;

  public RunProcessor(PlatformType platformType, String name) {
    super();
    this.platformType = platformType;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public PlatformType getPlatformType() {
    return platformType;

  }

  public abstract NotificationDto process(File runDirectory, TimeZone tz) throws IOException;

}
