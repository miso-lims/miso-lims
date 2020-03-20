package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.IndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.IndexDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping(value = "/rest/indices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class IndexRestController extends RestController {

  @Autowired
  private IndexService indexService;

  @Autowired
  private IndexFamilyService indexFamilyService;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  @Autowired
  private ConstantsController constantsController;

  private final JQueryDataTableBackend<Index, IndexDto> jQueryBackend = new JQueryDataTableBackend<Index, IndexDto>() {

    @Override
    protected IndexDto asDto(Index model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Index> getSource() throws IOException {
      return indexService;
    }
  };

  @GetMapping("/dt")
  @ResponseBody
  public DataTablesResponseDto<IndexDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping("/dt/platform/{platform}")
  @ResponseBody
  public DataTablesResponseDto<IndexDto> dataTableByPlatform(@PathVariable("platform") String platform, HttpServletRequest request)
      throws IOException {
    PlatformType platformType = PlatformType.valueOf(platform);
    if (platformType == null) {
      throw new RestException("Invalid platform type.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.platformType(platformType),
        PaginationFilter.archived(false));
  }

  @PostMapping
  public @ResponseBody IndexDto create(@RequestBody IndexDto dto) throws IOException {
    return RestUtils.createObject("Index", dto, Dtos::to, indexService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{indexId}")
  public @ResponseBody IndexDto update(@PathVariable long indexId, @RequestBody IndexDto dto) throws IOException {
    return RestUtils.updateObject("Index", indexId, dto, Dtos::to, indexService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Index", ids, indexService);
  }

  public static class IndexSearchRequest {

    private List<String> position1Indices;
    private List<String> position2Indices;

    public List<String> getPosition1Indices() {
      return position1Indices;
    }

    public void setPosition1Indices(List<String> position1Indices) {
      this.position1Indices = position1Indices;
    }

    public List<String> getPosition2Indices() {
      return position2Indices;
    }

    public void setPosition2Indices(List<String> position2Indices) {
      this.position2Indices = position2Indices;
    }

  }

  public static class IndexSearchResult {

    private String indexFamily;

    private long position1Matches;

    private long position2Matches;

    public String getIndexFamily() {
      return indexFamily;
    }

    public void setIndexFamily(String indexFamily) {
      this.indexFamily = indexFamily;
    }

    public long getPosition1Matches() {
      return position1Matches;
    }

    public void setPosition1Matches(long position1Matches) {
      this.position1Matches = position1Matches;
    }

    public long getPosition2Matches() {
      return position2Matches;
    }

    public void setPosition2Matches(long position2Matches) {
      this.position2Matches = position2Matches;
    }

  }

  @PostMapping("/search")
  public @ResponseBody List<IndexSearchResult> searchIndexFamilies(@RequestBody IndexSearchRequest request) throws IOException {
    return indexFamilyService.list().stream()
        .map(fam -> {
          IndexSearchResult result = new IndexSearchResult();
          result.setIndexFamily(fam.getName());
          result.setPosition1Matches(fam.getIndices().stream()
              .filter(i -> i.getPosition() == 1)
              .map(Index::getSequence)
              .filter(sequence -> request.getPosition1Indices().contains(sequence))
              .count());
          result.setPosition2Matches(fam.getIndices().stream()
              .filter(i -> i.getPosition() == 2)
              .map(Index::getSequence)
              .filter(sequence -> request.getPosition2Indices().contains(sequence))
              .count());
          return result;
        })
        .filter(result -> result.getPosition1Matches() > 0 || result.getPosition2Matches() > 0)
        .collect(Collectors.toList());
  }

}
