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

package uk.ac.bbsrc.tgac.miso.core.data;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import java.util.Date;

/**
 * A Dilution represents a stepwise serial dilution in a given process, from a
 * parent Library at some point upstream. At any stage, a dilution has a creator,
 * date and concentration.
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonWriteNullProperties(false)
@JsonIgnoreProperties({"securityProfile","library"})
@PrintableBarcode
public interface Dilution extends SecurableByProfile, Barcodable, Comparable, Deletable, Poolable {

  /**
   * Gets the current dilutionId
   *
   * @return Long
   */
  public Long getDilutionId();

  /**
   * Sets the dilutionId of this Dilution object
   *
   * @param dilutionId dilutionId.
   *
   */
  public void setDilutionId(Long dilutionId);

  /**
   * Returns the name of this Dilution object.
   *
   * @return String name.
   */
  public String getName();

  /**
   * Method setName sets the name of this Dilution object.
   *
   * @param name name.
   *
   */
  public void setName(String name);

  /**
   * Returns the dilutionCreator of this Dilution object.
   *
   * @return String dilutionCreator.
   */
  public String getDilutionCreator();

  /**
   * Sets the dilutionCreator of this Dilution object.
   *
   * @param creator dilutionCreator.
   *
   */
  public void setDilutionCreator(String creator);

  /**
   * Returns the creationDate of this Dilution object.
   *
   * @return Date creationDate.
   */
  public Date getCreationDate();

  /**
   * Sets the creationDate of this Dilution object.
   *
   * @param creationDate creationDate.
   *
   */
  public void setCreationDate(Date creationDate);

  /**
   * Returns the concentration of this Dilution object.
   *
   * @return Double concentration.
   */
  public Double getConcentration();

  /**
   * Sets the concentration of this Dilution object.
   *
   * @param concentration concentration.
   */
  public void setConcentration(Double concentration);

  /**
   * Returns the Library of this Dilution object.
   *
   * @return Library library.
   */
  public Library getLibrary();
}
