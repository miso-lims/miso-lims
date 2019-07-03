/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentStatusService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentStatusDto;

@Controller
@RequestMapping("/rest/instrumentstatus")
public class InstrumentStatusRestController extends RestController {

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
          Date aDate = getCompareDate(a);
          Date bDate = getCompareDate(b);
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
    Predicate<Run> running = run -> run != null && run.getHealth() == HealthType.Running;
    if (status.getPositions().values().stream().allMatch(running)) {
      return RunningPositions.ALL;
    } else if (status.getPositions().values().stream().anyMatch(running)) {
      return RunningPositions.SOME;
    } else {
      return RunningPositions.NONE;
    }
  }

  private static Date getCompareDate(InstrumentStatus status) {
    Date latest = null;
    for (Run run : status.getPositions().values()) {
      if (run == null) {
        continue;
      }
      Date runDate = getCompareDate(run);
      if (latest == null || runDate.after(latest)) {
        latest = runDate;
      }
    }
    return latest;
  }

  private static Date getCompareDate(Run run) {
    return run.getCompletionDate() == null ? run.getStartDate() : run.getCompletionDate();
  }

}
