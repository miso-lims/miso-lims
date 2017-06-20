package uk.ac.bbsrc.tgac.miso.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class PacBioNotificationDtoTest {


  @Test
  public void testRoundTrip() throws Exception {
    PacBioNotificationDto notificationDto = new PacBioNotificationDto();
    notificationDto.setSequencerName("Coffee");
    notificationDto.setMovieDurationInSec(42);
    notificationDto.setCompletionDate(LocalDate.of(2017, 2, 23));
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
  public void testFullyPopulatedPacBioNotificationRoundTrip() throws Exception {
    PacBioNotificationDto notificationDto = fullyPopulatedPacBioNotificationDto("RUN_B");
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
    NotificationDto dto = fullyPopulatedPacBioNotificationDto("RUN_B");

    Run run = Dtos.to(dto);

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    assertThat(dto.getStartDate().toString(), is(format.format(run.getStartDate())));
  }

  static PacBioNotificationDto fullyPopulatedPacBioNotificationDto(String sequencerName) {
    PacBioNotificationDto notificationDto = new PacBioNotificationDto();
    notificationDto.setRunName("TEST_RUN_NAME");
    notificationDto.setSequencerFolderPath(Paths.get("/sequencers/TEST_RUN_FOLDER"));
    notificationDto.setContainerId("CONTAINER_ID");
    notificationDto.setSequencerName(sequencerName);
    notificationDto.setLaneCount(8);
    notificationDto.setHealthType(HealthType.Started);
    notificationDto.setStartDate(LocalDate.of(2017, 2, 23));
    notificationDto.setCompletionDate(LocalDate.of(2017, 2, 27));
    notificationDto.setPairedEndRun(true);
    notificationDto.setSoftware("Fido Opus SEAdog Standard Interface Layer");
    notificationDto.setMovieDurationInSec(42);
    return notificationDto;
  }

}
