package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;

@Controller
public class IndexDistanceToolController {

  @ModelAttribute("title")
  public String title() {
    return "Index Distance Tool";
  }

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

  public static class IndexDistanceResponseDto {
    private final Set<IndexDistanceWarningDto> collisions;
    private final int shortestIndexLength;

    public IndexDistanceResponseDto(Set<IndexDistanceWarningDto> collisions, int shortestIndexLength) {
      this.collisions = collisions;
      this.shortestIndexLength = shortestIndexLength;
    }

    public Set<IndexDistanceWarningDto> getCollisions() {
      return collisions;
    }

    public int getShortestIndexLength() {
      return shortestIndexLength;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((collisions == null) ? 0 : collisions.hashCode());
      result = prime * result + shortestIndexLength;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      IndexDistanceResponseDto other = (IndexDistanceResponseDto) obj;
      if (collisions == null) {
        if (other.collisions != null)
          return false;
      } else if (!collisions.equals(other.collisions))
        return false;
      if (shortestIndexLength != other.shortestIndexLength)
        return false;
      return true;
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      IndexDistanceWarningDto other = (IndexDistanceWarningDto) obj;
      if (editDistance != other.editDistance)
        return false;
      if (indices == null) {
        if (other.indices != null)
          return false;
      } else if (!indices.equals(other.indices))
        return false;
      return true;
    }
  }

  @RequestMapping(value = "/tools/indexdistance", method = RequestMethod.GET)
  public ModelAndView getTool(ModelMap model) {
    return new ModelAndView("/WEB-INF/pages/indexDistanceTool.jsp", model);
  }

  @RequestMapping(value = "/rest/indexdistance", method = RequestMethod.POST)
  public @ResponseBody IndexDistanceResponseDto checkIndices(@RequestBody IndexDistanceRequestDto requestObject) {
    List<String> indices = requestObject.getIndices().stream()
        .map(String::trim)
        .map(line -> line.replaceAll("\\W+", "")) // remove any spaces, commas, dashes, etc. used to separate dual index
                                                  // sequences
        .collect(Collectors.toList());
    int shortestIndexLength = getShortestIndexLength(indices); // compare against first index
    Set<IndexDistanceWarningDto> results = new HashSet<>();

    for (int i = 0; i < indices.size(); i++) {
      for (int j = i + 1; j < indices.size(); j++) {
        int editDistance = LibraryIndex.checkMismatches(truncate(indices.get(i), shortestIndexLength),
            truncate(indices.get(j), shortestIndexLength));
        if (editDistance < requestObject.getMinimumDistance()) {
          results.add(new IndexDistanceWarningDto(indices.get(i), indices.get(j), editDistance));
        }
      }
    }

    IndexDistanceResponseDto response = new IndexDistanceResponseDto(results, shortestIndexLength);
    return response;
  }

  private static int getShortestIndexLength(List<String> indices) {
    try {
      int shortestIndex = indices.stream().filter(Objects::nonNull)
          .mapToInt(String::length).min().getAsInt();
      if (shortestIndex == 0)
        return 0;
      return shortestIndex;
    } catch (NoSuchElementException e) {
      return 0;
    }
  }

  private static String truncate(String index, int shortestLength) {
    return index.substring(0, shortestLength);
  }

}
