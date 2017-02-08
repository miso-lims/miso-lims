package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletionGroup;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;

@Controller
public class ListPoolOrdersController {
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  @ModelAttribute("title")
  public String title() {
    return "Orders";
  }

  private SortedSet<HealthType> generateHealths(Map<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> groups) {
    SortedSet<HealthType> healths = new TreeSet<>(HealthType.COMPARATOR);

    for (Map<SequencingParameters, PoolOrderCompletionGroup> parameterGroup : groups.values()) {
      for (PoolOrderCompletionGroup completions : parameterGroup.values()) {
        LimsUtils.addUsedHealthTypes(completions.values(), healths);
      }
    }
    return healths;
  }

  private ArrayNode convertToJSON(ObjectMapper mapper, SortedSet<HealthType> headings,
      Map<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> input) {
    ArrayNode result = mapper.createArrayNode();
    for (Entry<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> poolEntry : input.entrySet()) {
      for (Entry<SequencingParameters, PoolOrderCompletionGroup> paramGroup : poolEntry.getValue().entrySet()) {
        ArrayNode row = mapper.createArrayNode();
        row.add(String.format("<a href=\"/miso/pool/%s\">%s</a>", poolEntry.getKey().getId(), poolEntry.getKey().getName()));
        row.add(String.format("<a href=\"/miso/pool/%s\">%s</a>", poolEntry.getKey().getId(), poolEntry.getKey().getAlias()));
        row.add(paramGroup.getKey().getPlatform().getNameAndModel());
        row.add(paramGroup.getKey().getName());
        for (HealthType health : headings) {
          row.add(paramGroup.getValue().get(health).getNumPartitions());
        }
        row.add(paramGroup.getValue().getRemaining());
        row.add(paramGroup.getValue().getLastUpdatedISO());
        result.add(row);
      }
    }
    return result;
  }

  private ObjectNode convertToJSON(ObjectMapper mapper, String htmlName, String humanName,
      Map<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> input) {
    ObjectNode result = mapper.createObjectNode();
    SortedSet<HealthType> healths = generateHealths(input);
    result.put("data", convertToJSON(mapper, healths, input));
    ArrayNode headings = result.putArray("headings");
    for (HealthType health : healths) {
      headings.add(health.toString());
    }
    result.put("htmlElement", htmlName);
    result.put("humanName", humanName);
    return result;
  }

  @RequestMapping("/poolorders")
  public ModelAndView listPools() throws IOException {
    ModelAndView model = new ModelAndView("/pages/listPoolOrders.jsp");
    Collection<PoolOrderCompletion> orders = poolOrderCompletionService.getAllOrders();

    Map<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> allGroups = LimsUtils.groupCompletions(orders);
    Map<Pool, Map<SequencingParameters, PoolOrderCompletionGroup>> unfulfilledGroups = LimsUtils.filterUnfulfilledCompletions(allGroups);

    ObjectMapper mapper = new ObjectMapper();
    ArrayNode results = mapper.createArrayNode();
    results.add(convertToJSON(mapper, "unful", "Unfulfilled", unfulfilledGroups));
    results.add(convertToJSON(mapper, "all", "All", allGroups));
    model.addObject("ordercompletionJSON", mapper.writeValueAsString(results));
    return model;
  }
}
