package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.util.Date;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneCorrector {

  private final TimeZone dbZone;
  private final TimeZone uiZone;

  @Autowired
  public TimeZoneCorrector(@Value("${miso.timeCorrection.uiZone:UTC}") String uiZone,
      @Value("${miso.timeCorrection.dbZone:UTC}") String dbZone) {
    this.dbZone = TimeZone.getTimeZone(dbZone);
    this.uiZone = TimeZone.getTimeZone(uiZone);
  }

  public void toDbTime(Date date, Consumer<Date> setter) {
    if (date == null) {
      return;
    }
    long time = date.getTime();
    int fromOffset = uiZone.getOffset(time);
    int toOffset = dbZone.getOffset(time);
    long shifted = time - fromOffset + toOffset;
    setter.accept(new Date(shifted));
  }

}
