package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public abstract interface PaginationFilter {

  public static PaginationFilter fulfilled(final boolean isFulfilled) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.setFulfilled(item, isFulfilled);
      }
    };
  }

  public static PaginationFilter platformType(final PlatformType platformType) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.setPlatformType(item, platformType);
      }
    };
  }

  public static PaginationFilter pool(final long poolId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.setPoolId(item, poolId);
      }
    };
  }

  public static PaginationFilter pool(final Pool pool) {
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      throw new IllegalArgumentException("Cannot filter by unsaved pool.");
    }
    return pool(pool.getId());
  }

  public static PaginationFilter project(final long projectId) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.setProjectId(item, projectId);
      }
    };
  }

  public static PaginationFilter query(final String query) {
    return new PaginationFilter() {

      @Override
      public <T> void apply(PaginationFilterSink<T> sink, T item) {
        sink.setQuery(item, query);
      }
    };
  }

  public abstract <T> void apply(PaginationFilterSink<T> sink, T item);

}
