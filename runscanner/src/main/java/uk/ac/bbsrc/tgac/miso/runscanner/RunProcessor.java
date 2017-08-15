package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.DefaultPacBio;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.DefaultIllumina;
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
  /**
   * Factory for building a particular {@link RunProcessor}
   */
  public static final class Builder implements Function<ObjectNode, RunProcessor> {
    private final BiFunction<Builder, ObjectNode, RunProcessor> create;

    private final String name;
    private final PlatformType platformType;

    public Builder(PlatformType platformType, String name, BiFunction<Builder, ObjectNode, RunProcessor> create) {
      this.platformType = platformType;
      this.name = name;
      this.create = create;
    }

    @Override
    public RunProcessor apply(ObjectNode parameters) {
      return create.apply(this, parameters);
    }

    public String getName() {
      return name;
    }

    public PlatformType getPlatformType() {
      return platformType;
    }
  }

  /**
   * Find the builder that matches the requested parameters.
   * 
   * @param pt the platform type of the processor
   * @param name the name of the processor
   * @return a builder if one exists
   */
  public static Optional<Builder> builderFor(PlatformType pt, String name) {
    return builders().filter(builder -> builder.getPlatformType() == pt && builder.getName().equals(name)).findAny();
  }

  /**
   * Produce a stream of all known builders of run processors.
   */
  public static Stream<Builder> builders() {
    Stream<Builder> standard = Stream.of(new Builder(PlatformType.ILLUMINA, "default", DefaultIllumina::create),
        new Builder(PlatformType.PACBIO, "default", DefaultPacBio::create));
    return Stream.concat(standard,
        Arrays.stream(PlatformType.values()).map(type -> new Builder(type, "testing", (builder, config) -> new Testing(builder))));
  }

  /**
   * Creates a JSON mapper that is configured to handle the dates in {@link NotificationDto}.
   */
  public static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule())
        .setDateFormat(new ISO8601DateFormat());

    return mapper;
  }

  /**
   * Create a run processor for the request configuration, if one exists.
   * 
   * @param pt the platform type of the processor
   * @param name the name of the processor
   * @param parameters a JSON object containing any other configuration parameters
   * @return
   */
  public static Optional<RunProcessor> processorFor(PlatformType pt, String name, ObjectNode parameters) {
    return builderFor(pt, name).map(builder -> builder.apply(parameters));
  }

  private final String name;

  private final PlatformType platformType;

  public RunProcessor(Builder builder) {
    super();
    platformType = builder.getPlatformType();
    name = builder.getName();
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
  public abstract Stream<File> getRunsFromRoot(File root);

  /**
   * Read a run directory and compute a result that can be sent to MISO.
   * 
   * @param runDirectory the directory to scan (which will be output from {@link #getRunsFromRoot(File)}
   * @param tz the user-specified timezone that the sequencer exists in
   * @return the DTO result for consumption by MISO; if {@link PlatformType#isDone} is false, this directory may be processed again.
   */
  public abstract NotificationDto process(File runDirectory, TimeZone tz) throws IOException;

}
