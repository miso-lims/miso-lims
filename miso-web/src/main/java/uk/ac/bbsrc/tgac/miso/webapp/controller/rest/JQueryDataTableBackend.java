package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

public abstract class JQueryDataTableBackend<Model, Dto> {

  private static final Logger log = LoggerFactory.getLogger(JQueryDataTableBackend.class);

  protected abstract Dto asDto(Model model);

  public DataTablesResponseDto<Dto> get(HttpServletRequest request, AdvancedSearchParser advancedSearchParser,
      PaginationFilter... filters)
      throws IOException {
    if (request.getParameterMap().size() > 0) {
      long numItems = getSource().count(filters);
      // get request params from DataTables
      Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
      Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
      String sSearch = request.getParameter("sSearch");
      String sSortDir = request.getParameter("sSortDir_0");
      String sortColIndex = request.getParameter("iSortCol_0");
      String sortCol = request.getParameter("mDataProp_" + sortColIndex);

      // get requested subset of item
      Long numMatches;
      DataTablesResponseDto<Dto> dtResponse = new DataTablesResponseDto<>();

      List<PaginationFilter> additionalFilters = new ArrayList<>(Arrays.asList(filters));
      StringWriter errorBuffer = new StringWriter();
      Consumer<String> errorHandler = message -> {
        if (errorBuffer.getBuffer().length() > 0) {
          errorBuffer.append("<br/>");
        }
        try {
          StringEscapeUtils.escapeHtml(errorBuffer, message);
        } catch (IOException e) {
          log.error("Failed to write to string writer.", e);
        }
      };
      if (!isStringEmptyOrNull(sSearch)) {
        additionalFilters
            .addAll(Arrays.asList(
                advancedSearchParser.parseQuery(sSearch,
                    SecurityContextHolder.getContext().getAuthentication().getName(), errorHandler)));
        numMatches = getSource().count(additionalFilters.toArray(filters));
      } else {
        numMatches = numItems;
      }
      Collection<Model> models =
          getSource().list(errorHandler, iDisplayStart, iDisplayLength, "asc".equalsIgnoreCase(sSortDir),
              sortCol,
              additionalFilters.toArray(filters));

      List<Dto> dtos = new ArrayList<>();
      for (Model model : models) {
        Dto dto = asDto(model);
        dtos.add(dto);
      }

      dtResponse.setITotalRecords(numItems);
      dtResponse.setITotalDisplayRecords(numMatches);
      dtResponse.setAaData(dtos);
      dtResponse.setSEcho(Long.valueOf(request.getParameter("sEcho")));
      String errorMessage = errorBuffer.toString();
      if (errorMessage.length() > 0) {
        dtResponse.setSError(errorMessage);
      }
      return dtResponse;
    } else {
      throw new RestException("Request must specify DataTables parameters.");
    }
  }

  protected abstract PaginatedDataSource<Model> getSource() throws IOException;
}
