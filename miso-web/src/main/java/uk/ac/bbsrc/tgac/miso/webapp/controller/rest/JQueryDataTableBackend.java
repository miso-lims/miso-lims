package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;

public abstract class JQueryDataTableBackend<Model, Dto> {

  protected abstract Dto asDto(Model model, UriComponentsBuilder builder);

  protected abstract PaginatedDataSource<Model> getSource() throws IOException;

  public DataTablesResponseDto<Dto> get(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder, PaginationFilter... filters) throws IOException {
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
      if (!isStringEmptyOrNull(sSearch)) {
        StringBuilder errorBuffer = new StringBuilder();
        additionalFilters
            .addAll(Arrays.asList(
                PaginationFilter.parse(sSearch, SecurityContextHolder.getContext().getAuthentication().getName(), errorBuffer::append)));
        if (errorBuffer.length() > 0) {
          dtResponse.setSError(errorBuffer.toString());
        }
        numMatches = getSource().count(additionalFilters.toArray(filters));
      } else {
        numMatches = numItems;
      }
      Collection<Model> models = getSource().list(iDisplayStart, iDisplayLength, "asc".equalsIgnoreCase(sSortDir), sortCol,
          additionalFilters.toArray(filters));

      List<Dto> dtos = new ArrayList<>();
      for (Model model : models) {
        dtos.add(asDto(model, uriBuilder));
      }

      dtResponse.setITotalRecords(numItems);
      dtResponse.setITotalDisplayRecords(numMatches);
      dtResponse.setAaData(dtos);
      dtResponse.setSEcho(new Long(request.getParameter("sEcho")));
      return dtResponse;
    } else {
      throw new RestException("Request must specify DataTables parameters.");
    }
  }
}
