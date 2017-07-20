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
import uk.ac.bbsrc.tgac.miso.runscanner.processors.StandardIllumina;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.Testing;

/**
 * Class for a sequencer-specific implementation to transform a directory containing a sequencer's output into {@link NotificationDto}
 * results.
 *
 * The behaviour of this class is specific to either a sequencer or group of sequencers. For instance, most Illumina sequencers' output can
 * be processed the same way (except the GA and GAII); therefore, if there is one "default" Illumina processor. This processor is unaware of
 * the MISO sequencer configuration, so the returned object type should match what the platform-type-specific DTO in MISO.
 * 
 * It is important that the processor be stateless. The same input may be requested again based on whether the resulting data was marked as
 * incomplete.
 */
public abstract class RunProcessor {

  // TODO add run processors as you create them
  public static final Iterable<RunProcessor> INSTANCES = Stream
      .concat(Stream.of(new StandardIllumina()), Arrays.stream(PlatformType.values()).map(Testing::new)).collect(Collectors.toList());

  /**
   * Creates a JSON mapper that is configured to handle the dates in {@link NotificationDto}.
   */
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

  /**
   * This is the name of the sequencer.
   * 
   * It serves only to help the user match this processor with the configuration provided.
   */
  public final String getName() {
    return name;
  }

  /**
   * This is the platform type of the sequencer.
   * 
   * It serves only to help the user match this processor with the configuration provided. No attempt is made to match the platform-type
   * with the returned DTO.
   */
  public final PlatformType getPlatformType() {
    return platformType;
  }

  /**
   * Provide the directories containing runs given a user-specified configuration directory.
   * 
   * For most sequencer, the runs are the directories immediately under the sequencer's output directory. In other platforms, they may be a
   * subset of those directories or nested further down. This method is to return the appropriate directories that are worth processing.
   * 
   * @param root The directory as specified by the user.
   * @return a stream of directories to process
   */
  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(File::isDirectory));
  }

  /**
   * Read a run directory and compute a result that can be sent to MISO.
   * 
   * @param runDirectory the directory to scan (which will be output from {@link #getRunsFromRoot(File)}
   * @param tz the user-specified timezone that the sequencer exists in
   * @return the DTO result for consumption by MISO; if {@link PlatformType#isDone} is false, this directory may be processed again.
   */
  public abstract NotificationDto process(File runDirectory, TimeZone tz) throws IOException;

}
