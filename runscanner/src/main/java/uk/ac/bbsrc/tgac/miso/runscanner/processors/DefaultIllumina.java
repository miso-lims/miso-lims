package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LatencyHistogram;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor;

import io.prometheus.client.Counter;

/**
 * Scan an Illumina sequener's output using the Illumina Interop C++ library.
 *
 * This should work for all sequencer execept the Genome Analyzer and Genome Analyzer II.
 */
public final class DefaultIllumina extends RunProcessor {
  private static final LatencyHistogram directory_scan_time = new LatencyHistogram("runscanner_illumina_file_completness_time",
      "The time to scan all the output files in a sequencer's directory to tell if it's finished.");

  private static final Counter completness_method_success = Counter.build("runscanner_illumina_completness_check",
      "The number of times a method was used to determine a run's completeness after sequencing").labelNames("method").register();

  private static final Pattern FAILED_MESSAGE = Pattern
      .compile("(\\d{1,2}/\\d{1,2}/\\d{4},\\d{2}:\\d{2}:\\d{2}).*Application\\sexited\\sbefore\\scompletion.*");
  private static final DateTimeFormatter FAILED_MESSAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy,HH:mm:ss");

  private static final Logger log = LoggerFactory.getLogger(DefaultIllumina.class);

  private static final XPathExpression RUN_COMPLETION_STATUS_EXPRESSION = compileXPath("//CompletionStatus")[0];

  private static final Pattern COMMA = Pattern.compile(",");

  private static final Predicate<String> BCL_FILENAME = Pattern.compile("^s_[0-9]*_[0-9]*\\.bcl(\\.gz)?").asPredicate();

  private static final Predicate<String> BCL_BGZF_FILENAME = Pattern.compile("^[0-9]*\\.(bcl\\.bgzf|cbcl)").asPredicate();

  private static final Set<XPathExpression> CONTAINER_PARTNUMBER_XPATHS;

  private static final XPathExpression FLOWCELL;
  private static final Pattern FLOWCELL_PATTERN = Pattern.compile("^([a-zA-Z]+(?: Rapid)?) (Flow Cell v\\d)$");
  private static final XPathExpression FLOWCELL_PAIRED;

  static {
    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      FLOWCELL = xpath.compile("//Setup/Flowcell/text()");
      FLOWCELL_PAIRED = xpath.compile("//Setup/PairEndFC/text()");

      XPathExpression miSeqPartNumber = xpath.compile("//FlowcellRFIDTag/PartNumber/text()");
      XPathExpression novaSeqPartNum = xpath.compile("//FlowCellRfidTag/PartNumber/text()");

      CONTAINER_PARTNUMBER_XPATHS = Collections.unmodifiableSet(Sets.newHashSet(miSeqPartNumber, novaSeqPartNum));
    } catch (XPathExpressionException e) {
      throw new IllegalStateException("Failed to compile xpaths", e);
    }
  }

  public static DefaultIllumina create(Builder builder, ObjectNode parameters) {
    return new DefaultIllumina(builder,
        parameters.hasNonNull("checkOutput") ? parameters.get("checkOutput").asBoolean() : true);
  }

  private static Optional<HealthType> getHealth(Document document) {
    try {
      String status = (String) RUN_COMPLETION_STATUS_EXPRESSION.evaluate(document, XPathConstants.STRING);
      switch (status) {
      case "CompletedAsPlanned":
        return Optional.of(HealthType.Completed);
      default:
        log.debug("New Illumina completion status found: %s", status);
      }
    } catch (XPathExpressionException e) {
      log.error("Failed to evaluate completion status", e);
    }
    return Optional.empty();
  }

  private final boolean checkOutput;

  public DefaultIllumina(Builder builder, boolean checkOutput) {
    super(builder);
    this.checkOutput = checkOutput;
  }

  @Override
  public Stream<File> getRunsFromRoot(File root) {
    return Arrays.stream(root.listFiles(f -> f.isDirectory() && !f.getName().equals("Instrument")));
  }

  private boolean isLaneComplete(Path laneDir, IlluminaNotificationDto dto) {
    // For MiSeq and HiSeq, check for a complete set of BCL files per each cycle
    boolean completeCycleData = IntStream.rangeClosed(1, dto.getNumCycles())//
        .mapToObj(cycle -> String.format("C%d.1", cycle))//
        .map(laneDir::resolve)//
        .filter(Files::exists)//
        .allMatch(cycleDir -> {
          try (Stream<Path> cycleWalk = Files.walk(cycleDir, 1)) {

            long bclCount = cycleWalk//
                .map(file -> file.getFileName().toString())//
                .filter(BCL_FILENAME)//
                .count();
            return bclCount == dto.getBclCount();
          } catch (IOException e) {
            log.error("Failed to walk lane directory: " + laneDir.toString(), e);
            return false;
          }
        });
    if (completeCycleData) {
      return true;
    }
    // For NextSeq, check for a file per cycle
    try (Stream<Path> laneWalk = Files.walk(laneDir, 1)) {
      // First, examine the control files to determine all the BCL files we intend to find for each cycle.
      long bgzfCount = laneWalk//
          .map(file -> file.getFileName().toString())//
          .filter(BCL_BGZF_FILENAME)//
          .count();
      if (bgzfCount == dto.getNumCycles()) {
        return true;
      }
    } catch (IOException e) {
      log.error("Failed to walk lane directory: " + laneDir.toString(), e);
    }
    return false;
  }

  @Override
  public NotificationDto process(File runDirectory, TimeZone tz) throws IOException {
    // Call the C++ program to do the real work and write a notification DTO to standard output. The C++ object has no direct binding to the
    // DTO, so any changes to the DTO must be manually changed in the C++ code.
    ProcessBuilder builder = new ProcessBuilder("nice", "runscanner-illumina", runDirectory.getAbsolutePath()).directory(runDirectory)
        .redirectError(Redirect.INHERIT);
    builder.environment().put("TZ", tz.getID());
    Process process = builder.start();

    IlluminaNotificationDto dto;
    int exitcode;
    try (InputStream output = process.getInputStream(); OutputStream input = process.getOutputStream()) {
      dto = createObjectMapper().readValue(output, IlluminaNotificationDto.class);
      dto.setSequencerFolderPath(runDirectory.getAbsolutePath());
    } finally {
      try {
        exitcode = process.waitFor();
      } catch (InterruptedException e) {
        throw new IOException(e);
      } finally {
        process.destroy();
      }
    }

    if (exitcode != 0) {
      throw new IOException("Illumina run processor did not exit cleanly: " + runDirectory.getAbsolutePath());
    }

    Stream.of("runParameters.xml", "RunParameters.xml")
        .map(f -> new File(runDirectory, f))
        .filter(file -> file.exists() && file.canRead())
        .findAny()
        .flatMap(RunProcessor::parseXml)
        .ifPresent(runParams -> {
          // See if we can figure out the chemistry
          dto.setChemistry(Arrays.stream(IlluminaChemistry.values()).filter(chemistry -> chemistry.test(runParams)).findFirst()
              .orElse(IlluminaChemistry.UNKNOWN));
          dto.setContainerModel(findContainerModel(runParams));
        });

    // See if we can figure out a sample sheet
    dto.setPoolNames(Optional.of(new File(runDirectory, "SampleSheet.csv"))//
        .filter(File::canRead)//
        .map(File::toPath)
        .map(WhineyFunction.rethrow(path -> {
          try (Stream<String> lines = Files.lines(path)) {
            return lines.filter(LimsUtils.rejectUntil(line -> line.startsWith("Sample_ID,")))//
                .map(COMMA::split)//
                .map(Pair.number(1))
                .filter(pair -> pair.getValue().length > 0)//
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue()[0]));
          }
        }))//
        .orElse(Collections.emptyMap()));

    // The Illumina library can't distinguish between a failed run and one that either finished or is still going. Scan the logs, if
    // available to determine if the run failed.
    File rtaLogDir = new File(runDirectory, "/Data/RTALogs");
    LocalDateTime failedDate = Optional
        .ofNullable(rtaLogDir.listFiles(file -> file.getName().endsWith("Log.txt") || file.getName().endsWith("Log_00.txt")))//
        .map(Arrays::stream)//
        .orElseGet(Stream::empty)
        .map(file -> {
          try (Scanner scanner = new Scanner(file)) {
            String failMessage = scanner.findWithinHorizon(FAILED_MESSAGE, 0);
            if (failMessage == null) {
              return null;
            }
            Matcher m = FAILED_MESSAGE.matcher(failMessage);
            return LocalDateTime.parse(m.group(1), FAILED_MESSAGE_DATE_FORMATTER);
          } catch (FileNotFoundException e) {
            log.error("RTA file vanished before reading", e);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .sorted(LocalDateTime::compareTo)
        .findFirst()
        .orElse(null);

    if (failedDate != null) {
      dto.setHealthType(HealthType.Failed);
      dto.setCompletionDate(failedDate);
    }

    // This run claims to be complete, but is it really?
    if (dto.getHealthType() == HealthType.Completed) {
      // Maybe a NextSeq wrote a completion status, that we take as authoritative even though it's totally undocumented behaviour.
      Optional<HealthType> updatedHealth = Optional.of(new File(runDirectory, "RunCompletionStatus.xml"))//
          .filter(File::canRead)//
          .flatMap(RunProcessor::parseXml)//
          .flatMap(DefaultIllumina::getHealth);

      if (updatedHealth.isPresent()) {
        completness_method_success.labels("xml").inc();
        updateCompletionDateFromFile(runDirectory, "RunCompletionStatus.xml", dto);
      }

      if (!updatedHealth.isPresent() && dto.getNumReads() > 0) {
        if (new File(runDirectory, "CopyComplete.txt").exists()) {
          // It's allegedly done.
          updatedHealth = Optional.of(HealthType.Completed);
          completness_method_success.labels("complete.txt").inc();
          updateCompletionDateFromFile(runDirectory, "CopyComplete.txt", dto);
        } else {
          // Well, that didn't work. Maybe there are netcopy files.
          long netCopyFiles = IntStream.rangeClosed(1, dto.getNumReads())//
              .mapToObj(read -> String.format("Basecalling_Netcopy_complete_Read%d.txt", read))//
              .map(fileName -> new File(runDirectory, fileName))//
              .filter(File::exists)//
              .count();
          if (netCopyFiles == 0) {
            // This might mean incomplete or it might mean the sequencer never wrote the files
          } else {
            // If we see some net copy files, then it's still running; if they're all here, assume it's done.
            if (netCopyFiles < dto.getNumReads()) {
              updatedHealth = Optional.of(HealthType.Running);
            } else {
              updatedHealth = Optional.of(HealthType.Completed);
              updateCompletionDateFromFile(runDirectory, String.format("Basecalling_Netcopy_complete_Read%d.txt", dto.getNumReads()), dto);
            }
            completness_method_success.labels("netcopy").inc();
          }
        }
      }

      // Check that all the data files have copied. This is a really expensive check, so we let the user disable it.
      if (!updatedHealth.isPresent() && checkOutput) {
        try (AutoCloseable latency = directory_scan_time.start()) {
          Path baseCallDirectory = Paths.get(dto.getSequencerFolderPath(), "Data", "Intensities", "BaseCalls");
          // Check that each lane directory is complete
          boolean dataCopied = IntStream.rangeClosed(1, dto.getLaneCount())//
              .mapToObj(lane -> String.format("L%03d", lane))//
              .map(baseCallDirectory::resolve)//
              .allMatch(laneDir -> isLaneComplete(laneDir, dto));
          if (!dataCopied) {
            updatedHealth = Optional.of(HealthType.Running);
            completness_method_success.labels("dirscan").inc();
          }
        } catch (Exception e) {
          throw new IOException(e);
        }
      }
      updatedHealth.ifPresent(dto::setHealthType);
    }

    return dto;
  }

  private void updateCompletionDateFromFile(File runDirectory, String fileName, IlluminaNotificationDto dto) throws IOException {
    if (dto.getCompletionDate() == null) {
      dto.setCompletionDate(Files.getLastModifiedTime(new File(runDirectory, fileName).toPath()).toInstant()
          .atZone(ZoneId.of("Z")).toLocalDateTime());
    }
  }

  private String findContainerModel(Document runParams) {
    // See if we can figure out the container model
    String partNum = CONTAINER_PARTNUMBER_XPATHS.stream()
        .map(expr -> {
          try {
            return expr.evaluate(runParams);
          } catch (XPathExpressionException e) {
            // ignore
            return null;
          }
        })
        .filter(model -> !LimsUtils.isStringEmptyOrNull(model))
        .findAny().orElse(null);
    if (partNum != null) {
      return partNum;
    }
    try {
      String flowcell = FLOWCELL.evaluate(runParams);
      if (LimsUtils.isStringEmptyOrNull(flowcell)) {
        return null;
      }
      String paired = FLOWCELL_PAIRED.evaluate(runParams);
      Matcher m = FLOWCELL_PATTERN.matcher(flowcell);
      if (!LimsUtils.isStringEmptyOrNull(paired) && m.matches()) {
        return m.group(1) + (Boolean.parseBoolean(paired) ? " PE " : " SR ") + m.group(2);
      } else {
        return flowcell;
      }
    } catch (XPathExpressionException e) {
      // ignore
      return null;
    }
  }

}
