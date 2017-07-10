package uk.ac.bbsrc.tgac.miso.runscanner.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor;

public final class StandardIllumina extends RunProcessor {
  private static final Pattern FAILED_MESSAGE = Pattern.compile("Application\\sexited\\sbefore\\scompletion");

  private static final Logger log = LoggerFactory.getLogger(StandardIllumina.class);

  public StandardIllumina() {
    super(PlatformType.ILLUMINA, "default");
  }

  @Override
  public NotificationDto process(File runDirectory, TimeZone tz) throws IOException {
    Path runPath = runDirectory.toPath().toAbsolutePath();

    Process process = new ProcessBuilder("runscanner-illumina", runDirectory.getAbsolutePath()).directory(runDirectory).start();

    IlluminaNotificationDto dto = createObjectMapper().readValue(process.getInputStream(), IlluminaNotificationDto.class);
    dto.setSequencerFolderPath(runPath);
    try {
      if (process.waitFor() != 0) {
        throw new IOException("Illumina run processor did not exit cleanly: " + runDirectory.getAbsolutePath());
      }
    } catch (InterruptedException e) {
      throw new IOException(e);
    }
    File rtaLogDir = new File(runDirectory, "/Data/RTALogs");
    boolean failed = rtaLogDir.exists()
        ? Arrays.stream(rtaLogDir.listFiles(file -> file.getName().endsWith("Log.txt") || file.getName().endsWith("Log_00.txt")))
            .anyMatch(file -> {
              try (Scanner scanner = new Scanner(file)) {
                return scanner.findWithinHorizon(FAILED_MESSAGE, 0) != null;
              } catch (FileNotFoundException e) {
                log.error("RTA file vanished before reading", e);
                return false;
              }
            })
        : false;
    if (failed) {
      dto.setHealthType(HealthType.Failed);
    }
    return dto;
  }
}
