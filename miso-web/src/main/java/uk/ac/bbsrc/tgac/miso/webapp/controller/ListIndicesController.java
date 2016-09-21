package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;

@Controller
public class ListIndicesController {
  @Autowired
  private IndexService indexService;

  @ModelAttribute("title")
  public String title() {
    return "Indices";
  }

  @RequestMapping("/indices")
  public ModelAndView listIndices() throws IOException {
    ModelAndView model = new ModelAndView("/pages/listIndices.jsp");
    Set<Index> indices = new HashSet<>();
    for (IndexFamily family : indexService.getIndexFamilies()) {
      indices.addAll(family.getIndices());
    }
    model.addObject("indices", indices);
    return model;
  }
}
