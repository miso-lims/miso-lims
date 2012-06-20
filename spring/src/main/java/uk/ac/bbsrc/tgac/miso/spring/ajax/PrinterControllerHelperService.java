/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.DefaultPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.PrintServiceUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

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
  private PrintManager<MisoPrintService, Queue<?>> printManager;

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }

  public JSONObject listAvailableServices(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      if (json.has("serviceClass") && !"".equals(json.getString("serviceClass"))) {
        Collection<MisoPrintService> ps = printManager.listPrintServicesByBarcodeableClass(Class.forName(json.getString("serviceClass")));
        if (ps.size() > 1) {
          sb.append("<option value=''>Select print service...</option>");
        }
        for (MisoPrintService p : ps) {
          if (p.isEnabled()) sb.append("<option value="+p.getName()+">"+p.getName()+"</option>");
        }
      }
      else {
        Collection<MisoPrintService> ps = printManager.listAllPrintServices();
        if (ps.size() > 1) {
          sb.append("<option value=''>Select print service...</option>");
        }
        for (MisoPrintService p : ps) {
          if (p.isEnabled()) sb.append("<option value="+p.getName()+">"+p.getName()+"</option>");
        }
      }
      return JSONUtils.JSONObjectResponse("services", sb.toString());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot retrieve available print services");
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot resolve the print service class: " + json.getString("serviceClass"));
    }
  }

  public JSONObject listAvailableContexts(HttpSession session, JSONObject json) {
    Collection<PrintContext> ps = printManager.getPrintContexts();
    StringBuilder sb = new StringBuilder();
    sb.append("<option value=''>Select printer context type...</option>");
    for (PrintContext p : ps) {
      sb.append("<option value="+p.getName()+">"+p.getName()+"</option>");
    }
    return JSONUtils.JSONObjectResponse("contexts", sb.toString());
  }

  public JSONObject listBarcodableEntities(HttpSession session, JSONObject json) {
    Set<Class<? extends Barcodable>> bs = printManager.getBarcodableEntities();
    StringBuilder sb = new StringBuilder();
    sb.append("<option value=''>Select barcodable entity...</option>");
    for (Class<? extends Barcodable> b : bs) {
      sb.append("<option value="+b.getName()+">"+b.getSimpleName()+"</option>");
    }
    return JSONUtils.JSONObjectResponse("barcodables", sb.toString());
  }

  public JSONObject getPrinterFormEntities(HttpSession session, JSONObject json) {
    JSONObject entities = new JSONObject();
    entities.put("contexts", listAvailableContexts(session, json).getString("contexts"));
    entities.put("barcodables", listBarcodableEntities(session, json).getString("barcodables"));
    return entities;
  }

  public JSONObject getContextFields(HttpSession session, JSONObject json) {
    try {
      if (json.has("contextName") && !json.get("contextName").equals("")) {
        PrintContext p = printManager.getPrintContext(json.getString("contextName"));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextFields", PrintServiceUtils.mapContextFieldsToJSON(p));
        return JSONUtils.JSONObjectResponse(map);
      }
      else {
        return JSONUtils.SimpleJSONError("No context or invalid context name supplied");
      }
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot get context fields for context: " + json.getString("contextName"));
    }
  }

  public JSONObject checkPrinterAvailability(HttpSession session, JSONObject json) {
    try {

      if (json.has("host") && !json.get("host").equals("")) {
        InetAddress i = InetAddress.getByName(json.getString("host"));
        if (i.isReachable(2000)) {
          return JSONUtils.JSONObjectResponse("html", "OK");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "FAIL");
        }
      }
    }
    catch (Exception e) {
      log.debug("Failed to check printer availability: ", e);
      return JSONUtils.JSONObjectResponse("html", "FAIL");
    }
    return JSONUtils.SimpleJSONError("Cannot check printer availability");
  }

  public JSONObject addPrintService(HttpSession session, JSONObject json) {
    try {
      MisoPrintService printService = new DefaultPrintService();
      printService.setName(json.getString("serviceName"));
      PrintContext pc = printManager.getPrintContext(json.getString("contextName"));
      JSONObject contextFields = JSONObject.fromObject(json.getString("contextFields"));
      PrintServiceUtils.mapJSONToContextFields(contextFields, pc);
      printService.setPrintContext(pc);
      printService.setEnabled(true);
      printService.setPrintServiceFor(Class.forName(json.getString("serviceFor")).asSubclass(Barcodable.class));
      printManager.storePrintService(printService);
      return JSONUtils.JSONObjectResponse("html", "OK");
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot add printer service:" + e.getMessage());
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot add printer service." + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot add printer service." + e.getMessage());
    }
  }

  public JSONObject disablePrintService(HttpSession session, JSONObject json) {
    if (json.has("printerName") && json.getString("printerName") != null && !"".equals(json.getString("printerName"))) {
      String printerName = json.getString("printerName");
      try {
        MisoPrintService bps = printManager.getPrintService(printerName);
        if (bps != null) {
          bps.setEnabled(false);
          printManager.storePrintService(bps);
        }
        return JSONUtils.SimpleJSONResponse("Printer disabled");
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot resolve printer with name: " + printerName + " : " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No such printer, or no printer specified to disable.");
    }
  }

  public JSONObject enablePrintService(HttpSession session, JSONObject json) {
    if (json.has("printerName") && json.getString("printerName") != null && !"".equals(json.getString("printerName"))) {
      String printerName = json.getString("printerName");
      try {
        MisoPrintService bps = printManager.getPrintService(printerName);
        if (bps != null) {
          bps.setEnabled(true);
          printManager.storePrintService(bps);
        }
        return JSONUtils.SimpleJSONResponse("Printer enabled");
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot resolve printer with name: " + printerName + " : " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No such printer, or no printer specified to enable.");
    }
  }

  public JSONObject reprintJob(HttpSession session, JSONObject json) {
    if (json.has("jobId")) {
      try {
        PrintJob pj = printManager.getPrintJob(json.getLong("jobId"));
        printManager.print(pj.getQueuedElements(), pj.getPrintService().getName(), pj.getPrintUser());
        return JSONUtils.SimpleJSONResponse("Print job "+pj+" reprinted successfully");
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot retrieve print job.");
      }
      catch (MisoPrintException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("No such printer, or no printer specified.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No print job specified to reprint.");
    }
  }
}