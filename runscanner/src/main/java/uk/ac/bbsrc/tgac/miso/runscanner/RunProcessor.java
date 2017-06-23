package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.Testing;

public abstract class RunProcessor {

  public static final Iterable<RunProcessor> INSTANCES;
  static {
    List<RunProcessor> processors = new ArrayList<>();
    // TODO add run processors as you create them
    for (PlatformType type : PlatformType.values()) {
      processors.add(new Testing(type));
    }
    INSTANCES = processors;
  }

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

  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(File::isDirectory));
  }

  public abstract NotificationDto process(File runDirectory, TimeZone tz) throws IOException;

}
