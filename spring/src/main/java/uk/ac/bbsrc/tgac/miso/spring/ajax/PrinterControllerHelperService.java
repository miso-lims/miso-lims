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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Ajaxified
public class PrinterControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PrinterControllerHelperService.class);
  @Autowired
  private PrinterService printerService;

  public JSONObject addPrinter(HttpSession session, JSONObject json) {
    try {
      Printer printer = new Printer();
      printer.setBackend(Backend.values()[json.getInt("backend")]);
      printer.setDriver(Driver.values()[json.getInt("driver")]);
      printer.setConfiguration(json.getJSONObject("configuration").toString());
      printer.setName(json.getString("name"));
      printer.setEnabled(true);
      printerService.create(printer);
      return JSONUtils.JSONObjectResponse("html", "OK");
    } catch (IOException e) {
      log.error("add printer", e);
      return JSONUtils.SimpleJSONError("Cannot add printer." + e.getMessage());
    }
  }

  public JSONObject deletePrinter(HttpSession session, JSONObject json) {
    try {
      if (!json.has("printerId")) {
        return JSONUtils.SimpleJSONError("No printer name supplied.");
      }
      Printer printer = printerService.get(json.getLong("printerId"));
      if (printer == null) {
        return JSONUtils.SimpleJSONError("No such printer.");
      }
      printerService.remove(printer);
      return JSONUtils.SimpleJSONResponse("done");
    } catch (

    IOException e) {
      log.error("Failed to delete printer: ", e);
      return JSONUtils.SimpleJSONError("Failed to edit printer service: " + e.getMessage());
    }
  }

  public JSONObject listAvailableServices(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<Printer> ps = printerService.getEnabled();
      if (ps.size() > 1) {
        sb.append("<option value=''>Select print service...</option>");
      }
      for (Printer p : ps) {
        if (p.isEnabled()) sb.append("<option value=" + p.getId() + ">" + p.getName() + "</option>");
      }
      return JSONUtils.JSONObjectResponse("services", sb.toString());
    } catch (IOException e) {
      log.error("list available services", e);
      return JSONUtils.SimpleJSONError("Cannot retrieve available print services");
    }
  }

  public JSONObject setPrinterState(HttpSession session, JSONObject json) {
    if (!json.has("printerId")) {
      return JSONUtils.SimpleJSONError("No such printer, or no printer specified to disable.");
    }
    long id = json.getLong("printerId");
    boolean state = json.getBoolean("state");
    try {
      Printer printer = printerService.get(id);
      if (printer != null) {
        printer.setEnabled(state);
        printerService.update(printer);
      }
      return JSONUtils.SimpleJSONResponse("Printer " + (state ? "enabled" : "disabled"));
    } catch (IOException e) {
      log.error("change printer state", e);
      return JSONUtils.SimpleJSONError("Cannot resolve printer with name: " + id + " : " + e.getMessage());
    }
  }

  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
  }
}
