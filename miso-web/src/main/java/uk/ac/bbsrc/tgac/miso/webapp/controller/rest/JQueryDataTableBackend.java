package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;

public abstract class JQueryDataTableBackend<Model, Dto, Filter extends PaginationFilter> {

  protected abstract Dto asDto(Model model, UriComponentsBuilder builder);

  protected abstract PaginatedDataSource<Model, Filter> getSource() throws IOException;

  public DataTablesResponseDto<Dto> get(Filter filter, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    if (request.getParameterMap().size() > 0) {
      long numItems = getSource().count(filter);
      // get request params from DataTables
      Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
      Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
      String sSearch = request.getParameter("sSearch");
      String sSortDir = request.getParameter("sSortDir_0");
      String sortColIndex = request.getParameter("iSortCol_0");
      String sortCol = request.getParameter("mDataProp_" + sortColIndex);

      // get requested subset of item
      Long numMatches;

      if (!isStringEmptyOrNull(sSearch)) {
        filter.setQuery(sSearch);
        numMatches = getSource().count(filter);
      } else {
        numMatches = numItems;
      }
      Collection<Model> models = getSource().list(filter, iDisplayStart, iDisplayLength, "asc".equalsIgnoreCase(sSortDir), sortCol);

      List<Dto> dtos = new ArrayList<>();
      for (Model model : models) {
        dtos.add(asDto(model, uriBuilder));
      }

      DataTablesResponseDto<Dto> dtResponse = new DataTablesResponseDto<>();
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
