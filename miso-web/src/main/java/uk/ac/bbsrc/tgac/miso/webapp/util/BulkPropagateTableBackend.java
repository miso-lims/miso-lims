package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Create a Handsontable for propagating a particular entity type
 *
 * @param <ParentModel> The database model for the entity from which the model can be propagated
 * @param <Dto> The DTO for the entity being propagated
 */
public abstract class BulkPropagateTableBackend<ParentModel extends Identifiable, Dto> extends BulkTableBackend<Dto> {
  private final String name;
  private final String parentName;

  public BulkPropagateTableBackend(String targetType, Class<? extends Dto> dtoClass, String name, String parentName) {
    super(targetType, dtoClass);
    this.name = name;
    this.parentName = parentName;
  }

  /**
   * Create a DTO from the parent for propagation.
   */
  protected abstract Dto createDtoFromParent(ParentModel item);

  /**
   * Read all the specified parents from the database by their IDs.
   */
  protected abstract Stream<ParentModel> loadParents(List<Long> parentIds) throws IOException;

  /**
   * Create a view to propagate parents to new entities.
   * 
   * @param idString a comma-delimited list of parent IDs
   */
  public final ModelAndView propagate(String idString, ModelMap model) throws IOException {
    return propagate(idString, 1, model);
  }

  /**
   * Create a view to propagate parents to new entities.
   * 
   * @param idString a comma-delimited list of parent IDs
   * @param replicates the number of copies of each target that should be provided for parent
   */
  public final ModelAndView propagate(String idString, int replicates, ModelMap model) throws IOException {
      if (replicates < 1) throw new IllegalArgumentException("Invalid number of replicates.");
    List<Long> ids = LimsUtils.parseIds(idString);
    List<Dto> dtos = loadParents(ids).map(this::createDtoFromParent).flatMap(dto -> Stream.generate(() -> dto).limit(replicates))
        .collect(Collectors.toList());
    return prepare(model, true, "Create " + name + " from " + parentName, dtos);
  }

  /**
   * Create a view to propagate parents to new entities.
   * 
   * @param idString a comma-delimited list of parent IDs
   * @param replicatesString the number of copies of each target that should be provided for parent
   */
  public final ModelAndView propagate(String idString, String replicatesString, ModelMap model) throws IOException {
    List<Integer> replicates = parseReplicates(replicatesString);
    if (replicates.size() == 1) {
      return propagate(idString, replicates.get(0), model);
    }
    List<Long> ids = LimsUtils.parseIds(idString);
    if (ids.size() != replicates.size()) {
      throw new IllegalArgumentException("Invalid number of replicates.");
    }
    List<Dto> dtos = loadParents(ids)
        .flatMap(parent -> Stream.generate(() -> parent).limit(replicates.get(ids.indexOf(parent.getId())))).map(this::createDtoFromParent)
        .collect(Collectors.toList());
    return prepare(model, true, "Create " + name + " from " + parentName, dtos);
  }

  protected static List<Integer> parseReplicates(String replicatesString) {
    String[] split = replicatesString.split(",");
    List<Integer> replicates = new ArrayList<>();
    for (int i = 0; i < split.length; i++) {
      replicates.add(Integer.parseInt(split[i]));
    }
    return replicates;
  }

}
