package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Index;

@Controller
public class IndexDistanceToolController {

  public static class IndexDistanceRequestDto {
    private List<String> indices;
    private int minimumDistance;

    public List<String> getIndices() {
      return indices;
    }

    public void setIndices(List<String> indices) {
      this.indices = indices;
    }

    public int getMinimumDistance() {
      return minimumDistance;
    }

    public void setMinimumDistance(int minimumDistance) {
      this.minimumDistance = minimumDistance;
    }
  }

  public static class IndexDistanceWarningDto {
    private final Set<String> indices;
    private final int editDistance;

    public IndexDistanceWarningDto(String index1, String index2, int editDistance) {
      this.indices = Collections.unmodifiableSet(Sets.newHashSet(index1, index2));
      this.editDistance = editDistance;
    }

    public Set<String> getIndices() {
      return indices;
    }

    public int getEditDistance() {
      return editDistance;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + editDistance;
      result = prime * result + ((indices == null) ? 0 : indices.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      IndexDistanceWarningDto other = (IndexDistanceWarningDto) obj;
      if (editDistance != other.editDistance) return false;
      if (indices == null) {
        if (other.indices != null) return false;
      } else if (!indices.equals(other.indices)) return false;
      return true;
    }
  }

  @RequestMapping(value = "/tools/indexdistance", method = RequestMethod.GET)
  public ModelAndView getTool(ModelMap model) {
    return new ModelAndView("/pages/indexDistanceTool.jsp", model);
  }

  @RequestMapping(value = "/rest/indexdistance", method = RequestMethod.POST)
  public @ResponseBody Set<IndexDistanceWarningDto> checkIndices(@RequestBody IndexDistanceRequestDto requestObject) {
    List<String> indices = requestObject.getIndices();
    Set<IndexDistanceWarningDto> results = new HashSet<>();

    for (int i = 0; i < indices.size(); i++) {
      for (int j = i+1; j < indices.size(); j++) {
        int editDistance = Index.checkEditDistance(indices.get(i), indices.get(j));
        if (editDistance < requestObject.getMinimumDistance()) {
          results.add(new IndexDistanceWarningDto(indices.get(i), indices.get(j), editDistance));
        }
      }
    }

    return results;
  }

}
