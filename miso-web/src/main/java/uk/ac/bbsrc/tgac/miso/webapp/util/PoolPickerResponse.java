package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderCompletionDto;

public class PoolPickerResponse {

  public static class PoolPickerEntry {
    private final List<PoolOrderCompletionDto> orders;
    private final PoolDto pool;

    public PoolPickerEntry(PoolDto pool, List<PoolOrderCompletionDto> orders) {
      super();
      this.pool = pool;
      this.orders = orders;
    }

    public List<PoolOrderCompletionDto> getOrders() {
      return orders;
    }

    public PoolDto getPool() {
      return pool;
    }

  }

  List<PoolPickerEntry> items;
  Long numMatches;
  List<String> errors = new ArrayList<>();

  public <T> void populate(PaginatedDataSource<T> source, boolean sortOrder, String sortColumn, Integer limit,
      WhineyFunction<T, PoolPickerEntry> transform,
      PaginationFilter... filters) throws IOException {

    Consumer<String> errorHandler = message -> errors.add(message);
    
    Map<Long, List<PoolPickerEntry>> groupedByPool = source.list(errorHandler, 0, limit, sortOrder, sortColumn, filters)
        .stream().map(WhineyFunction.rethrow(transform)).collect(Collectors.groupingBy(entry -> entry.getPool().getId()));
    items = groupedByPool.values().stream().map(listOfPicks -> {
      List<PoolOrderCompletionDto> completionsByPool = listOfPicks.stream().flatMap(pick -> pick.getOrders().stream())
          .collect(Collectors.toList());
      PoolDto pool = listOfPicks.get(0).getPool();
      return new PoolPickerEntry(pool, completionsByPool);
    }).collect(Collectors.toList());

    numMatches = source.count(filters);
  }

  public List<PoolPickerEntry> getItems() {
    return items;
  }

  public void setItems(List<PoolPickerEntry> items) {
    this.items = items;
  }

  public Long getNumMatches() {
    return numMatches;
  }

  public void setNumMatches(Long numMatches) {
    this.numMatches = numMatches;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
}
