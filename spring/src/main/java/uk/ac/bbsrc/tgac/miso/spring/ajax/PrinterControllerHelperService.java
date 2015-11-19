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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.CustomPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.DefaultPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;
import uk.ac.bbsrc.tgac.miso.core.util.PrintServiceUtils;

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
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private MisoFilesManager misoFileManager;

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }

  public JSONObject listAvailableServices(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      if (json.has("serviceClass") && !isStringEmptyOrNull(json.getString("serviceClass"))) {
        Collection<MisoPrintService> ps = printManager.listPrintServicesByBarcodeableClass(Class.forName(json.getString("serviceClass")));
        if (ps.size() > 1) {
          sb.append("<option value=''>Select print service...</option>");
        }
        for (MisoPrintService p : ps) {
          if (p.isEnabled()) sb.append("<option value=" + p.getName() + ">" + p.getName() + "</option>");
        }
      } else {
        Collection<MisoPrintService> ps = printManager.listAllPrintServices();
        if (ps.size() > 1) {
          sb.append("<option value=''>Select print service...</option>");
        }
        for (MisoPrintService p : ps) {
          if (p.isEnabled()) sb.append("<option value=" + p.getName() + ">" + p.getName() + "</option>");
        }
      }
      return JSONUtils.JSONObjectResponse("services", sb.toString());
    } catch (IOException e) {
      log.error("list available services", e);
      return JSONUtils.SimpleJSONError("Cannot retrieve available print services");
    } catch (ClassNotFoundException e) {
      log.error("list available services", e);
      return JSONUtils.SimpleJSONError("Cannot resolve the print service class: " + json.getString("serviceClass"));
    }
  }

  public JSONObject listAvailableContexts(HttpSession session, JSONObject json) {
    Collection<PrintContext> ps = printManager.getPrintContexts();
    StringBuilder sb = new StringBuilder();
    sb.append("<option value=''>Select printer context type...</option>");
    for (PrintContext p : ps) {
      sb.append("<option value=" + p.getName() + ">" + p.getName() + "</option>");
    }
    return JSONUtils.JSONObjectResponse("contexts", sb.toString());
  }

  public JSONObject listBarcodableEntities(HttpSession session, JSONObject json) {
    Set<Class<? extends Barcodable>> bs = printManager.getBarcodableEntities();
    StringBuilder sb = new StringBuilder();
    sb.append("<option value=''>Select barcodable entity...</option>");
    sb.append("<option value='Custom'>Custom</option>");
    for (Class<? extends Barcodable> b : bs) {
      sb.append("<option value=" + b.getName() + ">" + b.getSimpleName() + "</option>");
    }
    return JSONUtils.JSONObjectResponse("barcodables", sb.toString());
  }

  public JSONObject getPrinterFormEntities(HttpSession session, JSONObject json) {
    JSONObject entities = new JSONObject();
    entities.put("contexts", listAvailableContexts(session, json).getString("contexts"));
    entities.put("barcodables", listBarcodableEntities(session, json).getString("barcodables"));
    entities.put("barcodableSchemas", listBarcodableSchemas(session, json).getString("barcodableSchemas"));
    return entities;
  }

  public JSONObject listBarcodableSchemas(HttpSession session, JSONObject json) {
    Collection<BarcodableSchema> bss = printManager.getBarcodableSchemas();
    StringBuilder sb = new StringBuilder();
    sb.append("<option value=''>Select barcodable schema...</option>");
    for (BarcodableSchema bs : bss) {
      sb.append("<option value=" + bs.getName() + ">" + bs.getName() + "</option>");
    }
    return JSONUtils.JSONObjectResponse("barcodableSchemas", sb.toString());
  }

  public JSONObject getContextFields(HttpSession session, JSONObject json) {
    try {
      if (json.has("contextName") && !isStringEmptyOrNull(json.getString("contextName"))) {
        PrintContext p = printManager.getPrintContext(json.getString("contextName"));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contextFields", PrintServiceUtils.mapContextFieldsToJSON(p));
        return JSONUtils.JSONObjectResponse(map);
      } else {
        return JSONUtils.SimpleJSONError("No context or invalid context name supplied");
      }
    } catch (IllegalAccessException e) {
      log.error("get context fiedls", e);
      return JSONUtils.SimpleJSONError("Cannot get context fields for context: " + json.getString("contextName"));
    }
  }

  public JSONObject checkPrinterAvailability(HttpSession session, JSONObject json) {
    try {

      if (json.has("host") && !isStringEmptyOrNull(json.getString("host"))) {
        InetAddress i = InetAddress.getByName(json.getString("host"));
        if (i.isReachable(2000)) {
          return JSONUtils.JSONObjectResponse("html", "OK");
        } else {
          return JSONUtils.JSONObjectResponse("html", "FAIL");
        }
      }
    } catch (Exception e) {
      log.debug("Failed to check printer availability: ", e);
      return JSONUtils.JSONObjectResponse("html", "FAIL");
    }
    return JSONUtils.SimpleJSONError("Cannot check printer availability");
  }

  public JSONObject addPrintService(HttpSession session, JSONObject json) {
    try {
      BarcodableSchema barcodableSchema = printManager.getBarcodableSchema(json.getString("schema"));
      MisoPrintService printService = new DefaultPrintService();
      if ("Custom".equals(json.getString("serviceFor"))) {
        printService = new CustomPrintService();
      }
      printService.setName(json.getString("serviceName"));
      PrintContext pc = printManager.getPrintContext(json.getString("contextName"));
      JSONObject contextFields = JSONObject.fromObject(json.getString("contextFields"));
      PrintServiceUtils.mapJSONToContextFields(contextFields, pc);
      printService.setPrintContext(pc);
      printService.setBarcodableSchema(barcodableSchema);
      printService.setEnabled(true);
      if ("Custom".equals(json.getString("serviceFor"))) {
        printService.setPrintServiceFor(JSONObject.class);
      } else {
        printService.setPrintServiceFor(Class.forName(json.getString("serviceFor")).asSubclass(Barcodable.class));
      }
      printManager.storePrintService(printService);
      return JSONUtils.JSONObjectResponse("html", "OK");
    } catch (ClassNotFoundException e) {
      log.error("add print service", e);
      return JSONUtils.SimpleJSONError("Cannot add printer service:" + e.getMessage());
    } catch (IllegalAccessException e) {
      log.error("add print service", e);
      return JSONUtils.SimpleJSONError("Cannot add printer service." + e.getMessage());
    } catch (IOException e) {
      log.error("add print service", e);
      return JSONUtils.SimpleJSONError("Cannot add printer service." + e.getMessage());
    }
  }

  public JSONObject disablePrintService(HttpSession session, JSONObject json) {
    if (json.has("printerName") && !isStringEmptyOrNull(json.getString("printerName"))) {
      String printerName = json.getString("printerName");
      try {
        MisoPrintService bps = printManager.getPrintService(printerName);
        if (bps != null) {
          bps.setEnabled(false);
          printManager.storePrintService(bps);
        }
        return JSONUtils.SimpleJSONResponse("Printer disabled");
      } catch (IOException e) {
        log.error("disable print service", e);
        return JSONUtils.SimpleJSONError("Cannot resolve printer with name: " + printerName + " : " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No such printer, or no printer specified to disable.");
    }
  }

  public JSONObject enablePrintService(HttpSession session, JSONObject json) {
    if (json.has("printerName") && !isStringEmptyOrNull(json.getString("printerName"))) {
      String printerName = json.getString("printerName");
      try {
        MisoPrintService bps = printManager.getPrintService(printerName);
        if (bps != null) {
          bps.setEnabled(true);
          printManager.storePrintService(bps);
        }
        return JSONUtils.SimpleJSONResponse("Printer enabled");
      } catch (IOException e) {
        log.error("enable print service", e);
        return JSONUtils.SimpleJSONError("Cannot resolve printer with name: " + printerName + " : " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No such printer, or no printer specified to enable.");
    }
  }

  public JSONObject changePrinterServiceRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      String serviceName = json.getString("serviceName");

      MisoPrintService bps = printManager.getPrintService(serviceName);

      response.put("hostname", "<input type='text' id='newhost-" + serviceName + "' value='" + bps.getPrintContext().getHost() + "'/>");
      response.put("edit", "<a href='javascript:void(0);' onclick='Print.ui.editPrinterService(\"" + serviceName + "\");'>Save</a>");
      response.put("barcodableSchemas", "<select id='newschema-" + serviceName + "' name='printSchema'>"
          + listBarcodableSchemas(session, json).getString("barcodableSchemas") + "</select>");
      return response;
    } catch (Exception e) {
      log.error("Unable to edit this printer service: ", e);
      return JSONUtils.SimpleJSONError("Unable to edit this printer service: " + e.getMessage());
    }
  }

  public JSONObject editPrinterService(HttpSession session, JSONObject json) {
    try {
      if (json.has("serviceName") && !isStringEmptyOrNull(json.getString("serviceName"))) {
        MisoPrintService bps = printManager.getPrintService(json.getString("serviceName"));
        if (bps != null) {
          PrintContext pc = bps.getPrintContext();
          JSONObject contextFields = new JSONObject();
          contextFields.put("host", json.getString("host"));
          PrintServiceUtils.mapJSONToContextFields(contextFields, pc);
          bps.setPrintContext(pc);
          if (json.has("schema") && !isStringEmptyOrNull(json.getString("schema"))) {
            BarcodableSchema barcodableSchema = printManager.getBarcodableSchema(json.getString("schema"));
            bps.setBarcodableSchema(barcodableSchema);
          }
          printManager.storePrintService(bps);
          return JSONUtils.SimpleJSONResponse("done");
        } else {
          return JSONUtils.SimpleJSONError("No printer service of name: " + json.getString("serviceName"));
        }
      } else {
        return JSONUtils.SimpleJSONError("No printer service name supplied.");
      }
    } catch (Exception e) {
      log.error("Failed to edit printer service: ", e);
      return JSONUtils.SimpleJSONError("Failed to edit printer service: " + e.getMessage());
    }
  }

  public JSONObject reprintJob(HttpSession session, JSONObject json) {
    if (json.has("jobId")) {
      try {
        PrintJob pj = printManager.getPrintJob(json.getLong("jobId"));
        printManager.print(pj.getQueuedElements(), pj.getPrintService().getName(), pj.getPrintUser());
        return JSONUtils.SimpleJSONResponse("Print job " + pj + " reprinted successfully");
      } catch (IOException e) {
        log.error("cannot retrieve print job", e);
        return JSONUtils.SimpleJSONError("Cannot retrieve print job.");
      } catch (MisoPrintException e) {
        log.error("no such printer", e);
        return JSONUtils.SimpleJSONError("No such printer, or no printer specified.");
      }
    } else {
      return JSONUtils.SimpleJSONError("No print job specified to reprint.");
    }
  }

  public JSONObject printCustomBarcode(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String line1 = json.getString("line1");
      String line2 = json.getString("line2");
      String line3 = json.getString("line3");
      String barcodeit = json.getString("barcodeit");
      String serviceName = json.getString("serviceName");

      MisoPrintService<File, JSONObject, PrintContext<File>> mps = null;
      mps = printManager.getPrintService(serviceName);

      Queue<File> thingsToPrint = new LinkedList<File>();

      JSONObject jsonObject = new JSONObject();

      jsonObject.put("barcode", barcodeit);
      jsonObject.put("field1", line1);
      jsonObject.put("field2", line2);
      jsonObject.put("field3", line3);

      File f = mps.getLabelFor(jsonObject);
      if (f != null) thingsToPrint.add(f);

      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    } catch (MisoPrintException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    } catch (IOException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printCustom1DBarcode(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String line1 = json.getString("line1");
      String line2 = json.getString("line2");
      String serviceName = json.getString("serviceName");

      MisoPrintService<File, JSONObject, PrintContext<File>> mps = null;
      mps = printManager.getPrintService(serviceName);

      Queue<File> thingsToPrint = new LinkedList<File>();

      JSONObject jsonObject = new JSONObject();

      jsonObject.put("field1", line1);
      jsonObject.put("field2", line2);

      File f = mps.getLabelFor(jsonObject);
      if (f != null) thingsToPrint.add(f);

      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    } catch (MisoPrintException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    } catch (IOException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject printCustom1DBarcodeBulk(HttpSession session, JSONObject json) {
    try {

      if (json.has("barcodes")) {
        String response = "";
        String barcodes = json.getString("barcodes");
        String[] codes = barcodes.split("\n");
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

        for (String code : codes) {
          String serviceName = json.getString("serviceName");

          MisoPrintService<File, JSONObject, PrintContext<File>> mps = null;
          mps = printManager.getPrintService(serviceName);

          Queue<File> thingsToPrint = new LinkedList<File>();

          JSONObject jsonObject = new JSONObject();

          jsonObject.put("field1", code);
          jsonObject.put("field2", "1");

          File f = mps.getLabelFor(jsonObject);
          if (f != null) thingsToPrint.add(f);

          PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
          response += "Job " + pj.getJobId() + " : Barcodes printed.\n";
        }
        return JSONUtils.SimpleJSONResponse(response);
      } else {
        return JSONUtils.SimpleJSONResponse("No barcode.");
      }
    } catch (MisoPrintException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    } catch (IOException e) {
      log.error("failed to print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }
}
