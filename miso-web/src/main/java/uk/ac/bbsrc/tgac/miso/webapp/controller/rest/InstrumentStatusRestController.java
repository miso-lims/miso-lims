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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentStatusDto;
import uk.ac.bbsrc.tgac.miso.service.InstrumentStatusService;

@Controller
@RequestMapping("/rest/instrumentstatus")
public class InstrumentStatusRestController extends RestController {

  @Autowired
  private InstrumentStatusService instrumentStatusService;

  @GetMapping(produces = "application/json")
  @ResponseBody
  public List<InstrumentStatusDto> listAll() throws IOException {
    return instrumentStatusService.list().stream()//
        .sorted((a, b) -> {
          boolean aRunning = a.getRun() == null ? false : (a.getRun().getHealth() == HealthType.Running);
          boolean bRunning = b.getRun() == null ? false : (b.getRun().getHealth() == HealthType.Running);
          Date aDate;
          Date bDate;
          if (aRunning != bRunning) {
            return (bRunning ? 1 : 0) - (aRunning ? 1 : 0);
          } else if (aRunning && bRunning) {
            aDate = a.getRun().getStartDate();
            bDate = b.getRun().getStartDate();
          } else {
            aDate = a.getRun() == null || a.getRun().getCompletionDate() == null ? new Date(0L) : a.getRun().getCompletionDate();
            bDate = b.getRun() == null || b.getRun().getCompletionDate() == null ? new Date(0L) : b.getRun().getCompletionDate();
          }
          return bDate.compareTo(aDate);
        })//
        .map(Dtos::asDto)//
        .collect(Collectors.toList());
  }

}
