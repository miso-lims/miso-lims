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

package uk.ac.bbsrc.tgac.miso.core.data.impl.kit;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;

/**
 * A KitComponentDescriptor handles information about a consumable element that is part of a kit. Every element of that type has a name and
 * reference number and uses KitDescriptor. KitComponents use
 * KitComponentDescriptors, which in turn use KitDescriptors, to represent a real-world manifestation of a consumable kit.
 *
 * @author Michal Zak
 * @since 0.0.2
 */
public class KitComponentDescriptorImpl implements KitComponentDescriptor {
  /** Field UNSAVED_ID */
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long kitComponentDescriptorId = KitComponentDescriptorImpl.UNSAVED_ID;
  private String name = "";
  private String referenceNumber = "";

  private KitDescriptor kitDescriptor;

  @Override
  public long getId() {
    return kitComponentDescriptorId;
  }

  @Override
  public void setId(long kitComponentDescriptorId) {
    this.kitComponentDescriptorId = kitComponentDescriptorId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getReferenceNumber() {
    return referenceNumber;
  }

  @Override
  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  @Override
  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  @Override
  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getReferenceNumber());
    sb.append(" : ");
    sb.append(getKitDescriptor().getId());
    sb.append(" : ");
    return sb.toString();
  }
}