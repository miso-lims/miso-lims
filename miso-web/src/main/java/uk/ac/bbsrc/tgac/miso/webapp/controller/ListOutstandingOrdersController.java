package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletionGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;

@Controller
public class ListOutstandingOrdersController {
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  @ModelAttribute("title")
  public String title() {
    return "List Outstanding Orders";
  }

  @RequestMapping("/poolorders/outstanding")
  public ModelAndView listPools() throws IOException {
    ModelAndView model = new ModelAndView("/pages/listPoolOrders.jsp");
    Map<Pool<?>, Map<SequencingParameters, PoolOrderCompletionGroup>> groups = LimsUtils
        .filterUnfulfilledCompletions(LimsUtils.groupCompletions(poolOrderCompletionService.getAllOrders()));
    SortedSet<HealthType> healths = new TreeSet<>(HealthType.COMPARATOR);
    for (Map<SequencingParameters, PoolOrderCompletionGroup> parameterGroup : groups.values()) {
      for (PoolOrderCompletionGroup completions : parameterGroup.values()) {
        LimsUtils.addUsedHealthTypes(completions.values(), healths);
      }
    }
    model.addObject("ordercompletions", groups);
    model.addObject("ordercompletionheadings", healths);
    return model;
  }
}
