package uk.ac.bbsrc.tgac.miso.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class IlluminaNotificationDtoTest {


  @Test
  public void testPartiallyPopulatedIlluminaNotificationRoundTrip() throws Exception {
    IlluminaNotificationDto notificationDto = new IlluminaNotificationDto();
    notificationDto.setSequencerName("Coffee");
    notificationDto.setCompletionDate("2017-02-23");
    notificationDto.setHealthType(HealthType.Started);

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule())
        .setDateFormat(new ISO8601DateFormat())
        .enable(SerializationFeature.INDENT_OUTPUT);
    String serialized = mapper.writeValueAsString(notificationDto);

    NotificationDto deSerialized = mapper.readValue(serialized, NotificationDto.class);
    assertThat("Round trip of", notificationDto, is(deSerialized));
  }

  @Test
  public void testFullyPopulatedIlluminaNotificationRoundTrip() throws Exception {
    IlluminaNotificationDto notificationDto = fullyPopulatedIlluminaNotificationDto("RUN_B");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule())
        .setDateFormat(new ISO8601DateFormat())
        .enable(SerializationFeature.INDENT_OUTPUT);
    String serialized = mapper.writeValueAsString(notificationDto);

    NotificationDto deSerialized = mapper.readValue(serialized, NotificationDto.class);
    assertThat("Round trip of", notificationDto, is(deSerialized));
  }

  static IlluminaNotificationDto fullyPopulatedIlluminaNotificationDto(String sequencerName) {
    IlluminaNotificationDto notificationDto = new IlluminaNotificationDto();
    notificationDto.setRunName("TEST_RUN_NAME");
    notificationDto.setSequencerFolderPath(Paths.get("/sequencers/TEST_RUN_FOLDER"));
    notificationDto.setContainerId("CONTAINER_ID");
    notificationDto.setSequencerName(sequencerName);
    notificationDto.setLaneCount(8);
    notificationDto.setHealthType(HealthType.Started);
    notificationDto.setStartDate("2017-02-23");
    notificationDto.setCompletionDate("2017-02-27");
    notificationDto.setPairedEndRun(true);
    notificationDto.setSoftware("Fido Opus SEAdog Standard Interface Layer");
    notificationDto.setNumCycles(20);
    notificationDto.setImgCycle(19);
    notificationDto.setScoreCycle(18);
    notificationDto.setCallCycle(17);
    return notificationDto;
  }

}
