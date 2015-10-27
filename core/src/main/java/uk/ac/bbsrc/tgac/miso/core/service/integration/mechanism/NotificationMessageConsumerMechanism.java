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

package uk.ac.bbsrc.tgac.miso.core.service.integration.mechanism;

import java.util.Set;

import org.springframework.integration.Message;

import uk.ac.bbsrc.tgac.miso.core.data.Run;

/**
 * Sub-interface of ConsumerMechanism which specifically deals with a Spring Integration {@link Message} as the parameter, and produces a
 * list of {@link Run} objects that were consumed
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public interface NotificationMessageConsumerMechanism<R extends Message, O extends Set<Run>> extends ConsumerMechanism<R, O> {
}
