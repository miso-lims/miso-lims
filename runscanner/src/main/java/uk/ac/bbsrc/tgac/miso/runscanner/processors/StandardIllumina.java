package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor;

/**
 * Scan an Illumina sequener's output using the Illumina Interop C++ library.
 *
 * This should work for all sequencer execept the Genome Analyzer and Genome Analyzer II.
 */
public final class StandardIllumina extends RunProcessor {
  private static final Pattern FAILED_MESSAGE = Pattern.compile("Application\\sexited\\sbefore\\scompletion");

  private static final Logger log = LoggerFactory.getLogger(StandardIllumina.class);

  public static StandardIllumina create(Builder builder, ObjectNode parameters) {
    return new StandardIllumina(builder);
  }

  public StandardIllumina(Builder builder) {
    super(builder);
  }

  @Override
  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(f -> f.isDirectory() && !f.getName().equals("Instrument")));
  }

  @Override
  public NotificationDto process(File runDirectory, TimeZone tz) throws IOException {
    // Call the C++ program to do the real work and write a notification DTO to standard output. The C++ object has no direct binding to the
    // DTO, so any changes to the DTO must be manually changed in the C++ code.
    Process process = new ProcessBuilder("nice", "runscanner-illumina", runDirectory.getAbsolutePath()).directory(runDirectory).start();

    IlluminaNotificationDto dto = createObjectMapper().readValue(process.getInputStream(), IlluminaNotificationDto.class);
    dto.setSequencerFolderPath(runDirectory.getAbsolutePath());
    try {
      if (process.waitFor() != 0) {
        throw new IOException("Illumina run processor did not exit cleanly: " + runDirectory.getAbsolutePath());
      }
    } catch (InterruptedException e) {
      throw new IOException(e);
    }

    // See if we can figure out the chemistry

    dto.setChemistry(Stream.of("runParameters.xml", "RunParameters.xml").map(f -> new File(runDirectory, f))
        .filter(file -> file.exists() && file.canRead()).findAny().flatMap(file -> {
          try {
            Document parameters = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            return Arrays.stream(IlluminaChemistry.values()).filter(chemistry -> chemistry.test(parameters)).findFirst();
          } catch (SAXException | ParserConfigurationException | IOException e) {
            log.error("Failed to parse parameters", e);
            return Optional.empty();
          }
        }).orElse(IlluminaChemistry.UNKNOWN));

    // The Illumina library can't distinguish between a failed run and one that either finished or is still going. Scan the logs, if
    // available to determine if the run failed.
    File rtaLogDir = new File(runDirectory, "/Data/RTALogs");
    boolean failed = Optional
        .ofNullable(rtaLogDir.listFiles(file -> file.getName().endsWith("Log.txt") || file.getName().endsWith("Log_00.txt")))
        .map(files -> Arrays.stream(files)
            .anyMatch(file -> {
              try (Scanner scanner = new Scanner(file)) {
                return scanner.findWithinHorizon(FAILED_MESSAGE, 0) != null;
              } catch (FileNotFoundException e) {
                log.error("RTA file vanished before reading", e);
                return false;
              }
            }))
        .orElse(false);
    if (failed) {
      dto.setHealthType(HealthType.Failed);
    }
    return dto;
  }
}
