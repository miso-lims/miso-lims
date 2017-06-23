package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;

public final class Main {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println(
          "Usage: java -DplatformType=ILLUMINA -Dname=default -Dtz=America/Toronto uk.ac.bbsrc.tgac.miso.runscanner.Main /path/to/run/folder");
    }
    String platformName = System.getProperty("platformType");
    if (platformName == null) {
      System.err.println("Please set -DplatformType=X where X is one of:");
      Arrays.stream(PlatformType.values()).map(PlatformType::name).forEach(System.err::println);
      System.exit(1);
    }

    String tzId = System.getProperty("tz");
    TimeZone tz;
    if (tzId == null) {
      tz = TimeZone.getDefault();
    } else {
      tz = TimeZone.getTimeZone(tzId);
    }

    PlatformType pt = PlatformType.valueOf(platformName);
    String name = System.getProperty("name", "default");
    for (RunProcessor rp : RunProcessor.INSTANCES) {
      if (rp.getPlatformType() == pt && rp.getName().equals(name)) {
        List<NotificationDto> results = new ArrayList<>();
        boolean success = true;
        for (String path : args) {
          File directory = new File(path);
          if (!directory.isDirectory() || !directory.canExecute() || !directory.canRead()) {
            System.err.println("Target is not a useable directory: " + path);
            success = false;
            continue;
          }
          try {
            results.add(rp.process(directory, tz));
          } catch (Exception e) {
            System.err.println("Cannot process directory: " + path);
            e.printStackTrace();
            success = false;
          }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
            .setDateFormat(new ISO8601DateFormat());

        try {
          mapper.writeValue(System.out, results);
        } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
        }
        System.exit(success ? 0 : 2);
      }
    }
    System.err.println("Cannot find a run processor that matches.");
    System.exit(1);
  }
}
