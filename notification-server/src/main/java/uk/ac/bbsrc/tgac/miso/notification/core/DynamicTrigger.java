/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.notification.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

/**
 * A trigger for periodic task execution with the added capability to modify runtime the period between polls. A desired behavior when you
 * are trying to throttle inbound messages via polling rate.
 * 
 * @author jtedilla
 * @see org.springframework.scheduling.support.PeriodicTrigger
 */
public class DynamicTrigger implements Trigger {
  private static final Log log = LogFactory.getLog(DynamicTrigger.class);

  private long period;
  private final TimeUnit timeUnit;
  private volatile long initialDelay = 0;
  private volatile boolean fixedRate = false;

  /**
   * Create a trigger with the given period in milliseconds.
   */
  public DynamicTrigger(long period) {
    this(period, null);
  }

  /**
   * Create a trigger with the given period and time unit. The time unit will apply not only to the period but also to any 'initialDelay'
   * value, if configured on this Trigger later via {@link #setInitialDelay(long)}.
   */
  public DynamicTrigger(long period, TimeUnit timeUnit) {
    Assert.isTrue(period >= 0, "period must not be negative");
    this.timeUnit = (timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS;
    this.period = this.timeUnit.toMillis(period);
  }

  /**
   * Specify a new period. Very useful when you try to throttle inbound messages by modifying the polling rate.
   */
  public void setPeriod(long newPeriod) {
    this.period = newPeriod;
  }

  /**
   * Specify the delay for the initial execution. It will be evaluated in terms of this trigger's {@link TimeUnit}. If no time unit was
   * explicitly provided upon instantiation, the default is milliseconds.
   */
  public void setInitialDelay(long initialDelay) {
    this.initialDelay = this.timeUnit.toMillis(initialDelay);
  }

  /**
   * Specify whether the periodic interval should be measured between the scheduled start times rather than between actual completion times.
   * The latter, "fixed delay" behavior, is the default.
   */
  public void setFixedRate(boolean fixedRate) {
    this.fixedRate = fixedRate;
  }

  /**
   * Returns the time after which a task should run again.
   */
  @Override
  public Date nextExecutionTime(TriggerContext triggerContext) {
    if (this.period == 0L) {
      // don't trigger any more
      return null;
    } else {
      log.debug("lastScheduledExecutionTime::" + triggerContext.lastScheduledExecutionTime());
      if (triggerContext.lastScheduledExecutionTime() == null) {
        log.debug("nextExecutionTime::" + new Date(System.currentTimeMillis() + this.initialDelay));
        return new Date(System.currentTimeMillis() + this.initialDelay);
      } else if (this.fixedRate) {
        log.debug("nextExecutionTime::" + new Date(triggerContext.lastScheduledExecutionTime().getTime() + this.period));
        return new Date(triggerContext.lastScheduledExecutionTime().getTime() + this.period);
      }
      log.debug("nextExecutionTime::" + new Date(triggerContext.lastCompletionTime().getTime() + this.period));
      return new Date(triggerContext.lastCompletionTime().getTime() + this.period);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (fixedRate ? 1231 : 1237);
    result = prime * result + (int) (initialDelay ^ (initialDelay >>> 32));
    result = prime * result + (int) (period ^ (period >>> 32));
    result = prime * result + ((timeUnit == null) ? 0 : timeUnit.hashCode());
    return result;
  }

}
