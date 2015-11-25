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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Controller
@SessionAttributes("printer")
public class PrinterController {
  protected static final Logger log = LoggerFactory.getLogger(PrinterController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }

  @RequestMapping(value = "/admin/configuration/printers", method = RequestMethod.GET)
  public ModelAndView view(ModelMap model) throws IOException {
    model.put("barcodePrinters", printManager.listAllPrintServices());
    return new ModelAndView("/pages/viewPrinters.jsp", model);
  }

  @RequestMapping(value = "/admin/configuration/printers/barcode/{printerId}", method = RequestMethod.GET)
  public ModelAndView viewBarcodePrinter(@PathVariable(value = "printerId") String printerId, ModelMap model) throws IOException {
    try {
      MisoPrintService ps = printManager.getPrintService(printerId);
      model.put("barcodePrinter", ps);

      Collection<? extends PrintJob> jobs = printManager.listPrintJobsByPrintService(ps);
      model.put("printJobs", jobs);
    } catch (IOException e) {
      log.error("view barcode printer", e);
    }
    return new ModelAndView("/pages/viewPrinters.jsp", model);
  }

  @RequestMapping(value = "/printjobs", method = RequestMethod.GET)
  public ModelAndView myPrintJobs(ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Collection<? extends PrintJob> jobs = printManager.listPrintJobsByUser(user);
      model.put("userPrintJobs", jobs);
    } catch (Exception e) {
      log.error("my print jobs", e);
    }
    return new ModelAndView("/pages/viewPrinters.jsp", model);
  }
}
