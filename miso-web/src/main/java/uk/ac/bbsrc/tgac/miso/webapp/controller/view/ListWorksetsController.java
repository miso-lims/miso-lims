package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetCategoryService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListWorksetsController {

  @Autowired
  private WorksetCategoryService worksetCategoryService;
  @Autowired
  private ObjectMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Worksets";
  }

  @RequestMapping("/worksets")
  public ModelAndView listWorksets(ModelMap model) throws IOException {
    Stream<String> tabs = Stream.of(Workset.ReservedWord.MINE.getText(), Workset.ReservedWord.ALL.getText());
    List<WorksetCategory> categories = worksetCategoryService.list();
    if (!categories.isEmpty()) {
      tabs = Stream.concat(tabs, categories.stream().map(WorksetCategory::getAlias).sorted());
      tabs = Stream.concat(tabs, Stream.of(Workset.ReservedWord.UNCATEGORIZED.getText()));
    }
    return new TabbedListItemsPage("workset", "category", tabs, (t1, t2) -> 1, Function.identity(),
        String::toLowerCase, mapper).list(model);
  }

}
