package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentStatusService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentStatusDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

@Controller
@RequestMapping("/rest/instrumentstatus")
public class InstrumentStatusRestController extends AbstractRestController {

  private enum RunningPositions {
    NONE, SOME, ALL; // Note: order is important - ordinals used for sorting
  }

  @Autowired
  private InstrumentStatusService instrumentStatusService;

  @GetMapping(produces = "application/json")
  @ResponseBody
  public List<InstrumentStatusDto> listAll() throws IOException {
    return instrumentStatusService.list().stream()//
        .sorted((a, b) -> {
          RunningPositions aRunning = checkRunning(a);
          RunningPositions bRunning = checkRunning(b);
          if (aRunning != bRunning) {
            return bRunning.ordinal() - aRunning.ordinal();
          }
          LocalDate aDate = getCompareDate(a);
          LocalDate bDate = getCompareDate(b);
          if (bDate == null) {
            return -1;
          } else if (aDate == null) {
            return 1;
          }
          return bDate.compareTo(aDate);
        })//
        .map(Dtos::asDto)//
        .collect(Collectors.toList());
  }

  private static RunningPositions checkRunning(InstrumentStatus status) {
    Predicate<InstrumentStatusPositionRun> running = run -> run != null && run.getHealth() == HealthType.Running;
    if (status.getPositions().stream().map(InstrumentStatusPosition::getRun).allMatch(running)) {
      return RunningPositions.ALL;
    } else if (status.getPositions().stream().map(InstrumentStatusPosition::getRun).anyMatch(running)) {
      return RunningPositions.SOME;
    } else {
      return RunningPositions.NONE;
    }
  }

  private static LocalDate getCompareDate(InstrumentStatus status) {
    return status.getPositions().stream()
        .map(InstrumentStatusPosition::getRun)
        .filter(Objects::nonNull)
        .map(run -> getCompareDate(run))
        .max(LocalDate::compareTo)
        .orElse(null);
  }

  private static LocalDate getCompareDate(InstrumentStatusPositionRun run) {
    return run.getCompletionDate() == null ? run.getStartDate() : run.getCompletionDate();
  }

}
