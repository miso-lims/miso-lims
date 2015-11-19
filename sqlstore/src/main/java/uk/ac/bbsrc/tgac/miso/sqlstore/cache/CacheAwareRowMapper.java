package uk.ac.bbsrc.tgac.miso.sqlstore.cache;

import org.springframework.jdbc.core.RowMapper;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore.cache
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 14/02/13
 * @since 0.1.9
 */
public abstract class CacheAwareRowMapper<T> implements RowMapper<T> {
  private boolean lazy = false;
  private boolean cacheEnabled = true;
  private String cacheName = null;

  public CacheAwareRowMapper(Class<T> clz) {
    this.cacheName = LimsUtils.noddyCamelCaseify(clz.getSimpleName()) + "Cache";
  }

  public CacheAwareRowMapper(Class<T> clz, boolean lazy) {
    this.lazy = lazy;
    if (lazy) {
      this.cacheName = "lazy" + LimsUtils.capitalise(clz.getSimpleName()) + "Cache";
    } else {
      this.cacheName = LimsUtils.noddyCamelCaseify(clz.getSimpleName()) + "Cache";
    }
  }

  public CacheAwareRowMapper(Class<T> clz, boolean lazy, boolean cacheEnabled) {
    this.lazy = lazy;
    this.cacheEnabled = cacheEnabled;

    if (cacheEnabled) {
      if (lazy && !cacheName.startsWith("lazy")) {
        this.cacheName = "lazy" + LimsUtils.capitalise(clz.getSimpleName()) + "Cache";
      } else {
        this.cacheName = LimsUtils.noddyCamelCaseify(clz.getSimpleName()) + "Cache";
      }
    }
  }

  public CacheAwareRowMapper(String cacheName) {
    this.cacheName = cacheName;
  }

  public CacheAwareRowMapper(String cacheName, boolean lazy) {
    this.lazy = lazy;
    if (lazy && !cacheName.startsWith("lazy")) {
      this.cacheName = "lazy" + LimsUtils.capitalise(cacheName);
    } else {
      this.cacheName = cacheName;
    }
  }

  public CacheAwareRowMapper(String cacheName, boolean lazy, boolean cacheEnabled) {
    this.lazy = lazy;
    this.cacheEnabled = cacheEnabled;

    if (cacheEnabled) {
      if (lazy && !cacheName.startsWith("lazy")) {
        this.cacheName = "lazy" + LimsUtils.capitalise(cacheName);
      } else {
        this.cacheName = cacheName;
      }
    }
  }

  public boolean isLazy() {
    return lazy;
  }

  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  public void setCacheEnabled(boolean cacheEnabled) {
    this.cacheEnabled = cacheEnabled;
  }

  public String getCacheName() {
    return this.cacheName;
  }

  public Cache lookupCache(CacheManager cacheManager) throws CacheException, UnsupportedOperationException {
    if (cacheEnabled) {
      if (cacheManager != null) {
        Cache c = cacheManager.getCache(getCacheName());
        if (c != null) {
          return c;
        }
        throw new CacheException("No such cache: " + getCacheName());
      } else {
        return null;
      }
    } else {
      throw new UnsupportedOperationException("Cannot lookup cache when mapping caches aren't enabled");
    }
  }
}
