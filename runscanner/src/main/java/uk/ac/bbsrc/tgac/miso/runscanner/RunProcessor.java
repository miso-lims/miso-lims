package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.processor.StandardIllumina;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.Testing;

public abstract class RunProcessor {

  // TODO add run processors as you create them
  public static final Iterable<RunProcessor> INSTANCES = Stream
      .concat(Stream.of(new StandardIllumina()), Arrays.stream(PlatformType.values()).map(Testing::new)).collect(Collectors.toList());

  public static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule())
        .setDateFormat(new ISO8601DateFormat());

    return mapper;
  }
  private final String name;

  private final PlatformType platformType;

  public RunProcessor(PlatformType platformType, String name) {
    super();
    this.platformType = platformType;
    this.name = name;
  }

  public final String getName() {
    return name;
  }

  public final PlatformType getPlatformType() {
    return platformType;
  }

  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(File::isDirectory));
  }

  public abstract NotificationDto process(File runDirectory, TimeZone tz) throws IOException;

}
