package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA. User: bianx Date: 04/12/2013 Time: 13:39 To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/importexport")
public class ImportExportController {

  @RequestMapping("")
  public ModelAndView index(ModelMap model) {
    return new ModelAndView("/pages/importExportIndex.jsp", model);
  }

  @RequestMapping("/exportsamplesheet")
  public ModelAndView exportSampleSheet(ModelMap model) {
    return new ModelAndView("/pages/exportSampleSheet.jsp", model);
  }

  @RequestMapping("/importsamplesheet")
  public ModelAndView importSampleSheet(ModelMap model) {
    return new ModelAndView("/pages/importSampleSheet.jsp", model);
  }

  @RequestMapping("/importlibrarypoolsheet")
  public ModelAndView importLibrarySheet(ModelMap model) {
    return new ModelAndView("/pages/importLibraryPoolSheet.jsp", model);
  }

}
