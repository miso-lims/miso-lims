package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.addJsonArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.CompressionFormat;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
public class ListPoolsController {

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private ObjectMapper mapper;

  @Value("${miso.genomeFolder:}")
  private String genomeFolder;

  @Value("${miso.pools.samplesheet.dragenVersion:}")
  private String dragenVersion;

  @Value("${miso.pools.samplesheet.compressionFormat:gzip}")
  private String compressionFormat;

  @ModelAttribute("title")
  public String title() {
    return "Pools";
  }

  @RequestMapping("/pools")
  public ModelAndView listPools(ModelMap model) throws IOException {
    return new TabbedListPoolsPage("pool", "platformType",
        TabbedListItemsPage.getPlatformTypes(instrumentModelService),
        PlatformType::getKey, PlatformType::name, mapper)
            .list(model);
  }

  public class TabbedListPoolsPage extends TabbedListItemsPage {

    public <T> TabbedListPoolsPage(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
        Function<T, Object> getValue, ObjectMapper mapper) {
      super(targetType, property, tabItems, getName, getValue, mapper);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("dragenVersion", dragenVersion);
      config.put("genomeFolder", genomeFolder);
      config.put("compressionFormat", compressionFormat);
      addJsonArray(mapper, config, "compressionFormats", Arrays.asList(CompressionFormat.values()), Dtos::asDto);

    }
  }

}
