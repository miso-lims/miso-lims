package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 08/10/12
 * @since 0.1.9
 */
@Aspect
public class SqlProfiler {
  protected static final Logger log = LoggerFactory.getLogger(SqlProfiler.class);

  private final Map<String, SqlTiming> sqlTimings;

  public SqlProfiler() {
    sqlTimings = Collections.synchronizedMap(new HashMap<String, SqlTiming>());
  }

  @Around("execution(* org.springframework.jdbc.core.JdbcTemplate.query(String,..))")
  public Object profile(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object obj = pjp.proceed();
    long time = System.currentTimeMillis() - start;
    String statement = pjp.getArgs()[0].toString();
    SqlTiming sqlTiming = null;
    synchronized (sqlTimings) {
      sqlTiming = sqlTimings.get(statement);
      if (sqlTiming == null) {
        sqlTiming = new SqlTiming(statement);
        sqlTimings.put(statement, sqlTiming);
      }
    }
    sqlTiming.recordTiming(time);
    log.info(pjp.getArgs()[0].toString() + " :: " + sqlTimings.get(pjp.getArgs()[0].toString()).getCumulativeExecutionTime() + "s");
    return obj;
  }

  public List<SqlTiming> getTimings(final SortedBy sort) {
    List<SqlTiming> timings = new ArrayList<SqlTiming>(sqlTimings.values());
    Collections.sort(timings, new Comparator<SqlTiming>() {

      @Override
      public int compare(SqlTiming o1, SqlTiming o2) {
        switch (sort) {
        case AVG_EXECUTION_TIME:
          return Math.round(o1.getAvgExecutionTime() - o2.getAvgExecutionTime());
        case CUMULATIVE_EXECUTION_TIME:
          long diff = o1.getCumulativeExecutionTime() - o2.getCumulativeExecutionTime();
          if (diff > 0l)
            return 1;
          else if (diff == 0)
            return 0;
          else
            return -1;
        case NUMBER_OF_EXECUTIONS:
          return o1.getExecutionCount() - o2.getExecutionCount();
        }
        return 0;
      }

    });

    return timings;
  }

  private class SqlTiming {

    private final String statement;
    private int count;
    private long cumulativeMillis;

    SqlTiming(String statement) {
      this.statement = statement;
    }

    synchronized SqlTiming recordTiming(long time) {
      count++;
      cumulativeMillis += time;
      return this;
    }

    public String getSqlStatement() {
      return statement;
    }

    public int getExecutionCount() {
      return count;
    }

    public long getCumulativeExecutionTime() {
      return cumulativeMillis;
    }

    public float getAvgExecutionTime() {
      return (float) cumulativeMillis / (float) count;
    }
  }

  private enum SortedBy {
    AVG_EXECUTION_TIME, CUMULATIVE_EXECUTION_TIME, NUMBER_OF_EXECUTIONS
  }
}
