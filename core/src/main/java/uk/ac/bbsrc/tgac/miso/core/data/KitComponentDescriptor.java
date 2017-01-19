/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

/**
 * A KitComponentDescriptor handles information about a consumable element that is part of a kit. Every element of that type has a name and
 * reference number and uses KitDescriptor. KitComponents use
 * KitComponentDescriptors, which in turn use KitDescriptors, to represent a real-world manifestation of a consumable kit.
 *
 * @author Michal Zak
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
// @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface KitComponentDescriptor extends Nameable {
  void setId(long kitComponentDescriptorId);

  /**
   * Sets this Kit Component Descriptor's name
   *
   * @param name
   */
  void setName(String name);

  /**
   * Gets this Kit Component Descriptor's reference number
   *
   * @return referenceNumber String
   */
  String getReferenceNumber();

  /**
   * Sets this Kit Component Descriptor's reference number
   *
   * @param referenceNumber
   */
  void setReferenceNumber(String referenceNumber);

  /**
   * Returns Kit Descriptor referenced by this Kit Component Descriptor
   *
   * @return kitDescriptor KitDescriptor
   */
  KitDescriptor getKitDescriptor();

  /**
   * Sets this Kit Component Descriptor's Kit Descriptor
   *
   * @param kitDescriptor
   */
  void setKitDescriptor(KitDescriptor kitDescriptor);

  @Override
  String toString();
}