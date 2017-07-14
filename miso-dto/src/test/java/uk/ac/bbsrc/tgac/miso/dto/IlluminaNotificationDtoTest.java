package uk.ac.bbsrc.tgac.miso.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class IlluminaNotificationDtoTest {


  @Test
  public void testPartiallyPopulatedIlluminaNotificationRoundTrip() throws Exception {
    IlluminaNotificationDto notificationDto = new IlluminaNotificationDto();
    notificationDto.setSequencerName("Coffee");
    notificationDto.setCompletionDate(LocalDateTime.of(2017, 2, 23, 0, 0));
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

  @Test
  public void testConvertToUtilDate() throws ParseException {
    NotificationDto dto = fullyPopulatedIlluminaNotificationDto("RUN_B");

    Run run = Dtos.to(dto, null);

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(dto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE), is(format.format(run.getStartDate())));
  }

  static IlluminaNotificationDto fullyPopulatedIlluminaNotificationDto(String sequencerName) {
    IlluminaNotificationDto notificationDto = new IlluminaNotificationDto();
    notificationDto.setRunAlias("TEST_RUN_NAME");
    notificationDto.setSequencerFolderPath("/sequencers/TEST_RUN_FOLDER");
    notificationDto.setContainerSerialNumber("CONTAINER_ID");
    notificationDto.setSequencerName(sequencerName);
    notificationDto.setLaneCount(8);
    notificationDto.setHealthType(HealthType.Started);
    notificationDto.setStartDate(LocalDateTime.of(2017, 2, 23, 0, 0));
    notificationDto.setCompletionDate(LocalDateTime.of(2017, 2, 27, 0, 0));
    notificationDto.setPairedEndRun(true);
    notificationDto.setSoftware("Fido Opus SEAdog Standard Interface Layer");
    notificationDto.setNumCycles(20);
    notificationDto.setImgCycle(19);
    notificationDto.setScoreCycle(18);
    notificationDto.setCallCycle(17);
    return notificationDto;
  }

}
