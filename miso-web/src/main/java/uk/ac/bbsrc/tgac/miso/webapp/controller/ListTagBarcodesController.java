package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.service.TagBarcodeService;

@Controller
public class ListTagBarcodesController {
  @Autowired
  private TagBarcodeService tagBarcodeService;

  @RequestMapping("/tagbarcodes")
  public ModelAndView listTagBarcodes() throws IOException {
    ModelAndView model = new ModelAndView("/pages/listTagBarcodes.jsp");
    Set<TagBarcode> barcodes = new HashSet<>();
    for (TagBarcodeFamily family : tagBarcodeService.getTagBarcodeFamilies()) {
      barcodes.addAll(family.getBarcodes());
    }
    model.addObject("tagbarcodes", barcodes);
    return model;
  }
}
