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

package uk.ac.bbsrc.tgac.miso.core.service.integration.strategy;

import org.springframework.integration.Message;
import org.springframework.integration.annotation.Gateway;

/**
 * An interface that defines how a Spring Integration {@link Message} can be consumed with parameters mapped via a {@link Gateway}, with the
 * Message payload T being typed by implementations of this class.
 * 
 * @author Rob Davey
 * @date 03-Feb-2012
 * @since 0.1.5
 */
public interface NotificationGateway<T> {
  /**
   * Consume the given {@link Message} and have the parameters mapped via the {@link Gateway}
   * 
   * @param m
   */
  @Gateway
  void consume(Message<T> m);
}
