package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Poolable;

/**
 * uk.ac.bbsrc.tgac.miso.core.util.jackson
 * <p/>
 * Deserializer class to help with processing pooled elements
 * 
 * @author Rob Davey
 * @date 08/01/13
 * @since 0.1.9
 */
public class PooledElementDeserializer extends JsonDeserializer<Collection<Poolable>> {
  protected static final Logger log = LoggerFactory.getLogger(PooledElementDeserializer.class);

  static final TypeReference<Poolable> type = new TypeReference<Poolable>() {
  };

  @Override
  public Collection<Poolable> deserialize(JsonParser jp, DeserializationContext arg1) throws IOException, JsonProcessingException {
    Collection<Poolable> poolables = new ArrayList<Poolable>();
    JsonNode node = jp.readValueAsTree();
    ObjectMapper o = new ObjectMapper();
    for (JsonNode element : node) {
      // this should be the poolable, i.e. plate, library or dilution
      log.debug("Element: " + element.toString());
      Poolable p = o.readValue(element, type);
      poolables.add(p);
    }
    return poolables;
  }
}
