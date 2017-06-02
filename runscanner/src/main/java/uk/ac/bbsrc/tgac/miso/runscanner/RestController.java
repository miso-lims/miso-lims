package uk.ac.bbsrc.tgac.miso.runscanner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;

@Controller
public class RestController {
  @Autowired
  private Scheduler scheduler;

  @RequestMapping(value = "/runs/all", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public List<NotificationDto> list(@PathVariable("name") String id) {
    return scheduler.finished().map(Map.Entry::getValue).collect(Collectors.toList());
  }

  @RequestMapping(value = "/runs/since/{date}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public List<NotificationDto> listAfter(@PathVariable("date") @DateTimeFormat(iso = ISO.DATE) Date id) {
    // TODO write correct filter
    return scheduler.finished().filter(entry -> entry.getValue() != null).map(Map.Entry::getValue).collect(Collectors.toList());
  }

  @RequestMapping(value = "/run/{name}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public NotificationDto getByName(@PathVariable("name") String id) {
    return scheduler.finished().filter(entry -> entry.getValue().getRunName().equals(id)).map(Map.Entry::getValue).findAny().orElse(null);
  }

}
