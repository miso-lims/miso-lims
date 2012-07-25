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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import java.util.Collection;

/**
 * An Experiment contains design information about a sequencing experiment,
 * as part of a parent {@link Study}.
 *
 * Experiments are associated with {@link Run} objects via {@link Pool} objects
 * which contain the actual sequencable material in prepared form. A Pool is
 * attached to an Experiment which is then assigned to an instrument
 * {@link SequencerPoolPartition}.
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonWriteNullProperties(false)
@JsonIgnoreProperties({"securityProfile"})
public interface Experiment extends SecurableByProfile, Submittable<Document>, Comparable, Deletable {

  /** Field UNSAVED_ID  */
  public static final Long UNSAVED_ID = -1L;

  /** Field PREFIX  */
  public static final String PREFIX = "EXP";

  /**
   * Returns the experimentId of this Experiment object.
   *
   * @return Long experimentId.
   */
  public Long getExperimentId();

  /**
   * Sets the experimentId of this Experiment object.
   *
   * @param experimentId the id of this Experiment object.
   *
   */
  public void setExperimentId(Long experimentId);

  /**
   * Returns the study of this Experiment object.
   *
   * @return Study study.
   */
  public Study getStudy();

  /**
   * Sets the study of this Experiment object.
   *
   * @param s study.
   *
   */
  public void setStudy(Study s);

  /**
   * Returns the accession of this Experiment object.
   *
   * @return String accession.
   */
  public String getAccession();

  /**
   * Sets the accession of this Experiment object.
   *
   * @param accession accession.
   *
   */
  public void setAccession(String accession);

  /**
   * Returns the alias of this Experiment object.
   *
   * @return String alias.
   */
  public String getAlias();

  /**
   * Sets the alias of this Experiment object.
   *
   * @param alias alias.
   *
   */
  public void setAlias(String alias);

  /**
   * Returns the title of this Experiment object.
   *
   * @return String title.
   */
  public String getTitle();

  /**
   * Sets the title of this Experiment object.
   *
   * @param title title.
   *
   */
  public void setTitle(String title);

  /**
   * Returns the name of this Experiment object.
   *
   * @return String name.
   */
  public String getName();

  /**
   * Sets the name of this Experiment object.
   *
   * @param name name.
   *
   */
  public void setName(String name);

  /**
   * Returns the description of this Experiment object.
   *
   * @return String description.
   */
  public String getDescription();

  /**
   * Sets the description of this Experiment object.
   *
   * @param description description.
   *
   */
  public void setDescription(String description);

  /**
   * Returns the platform of this Experiment object.
   *
   * @return Platform platform.
   */
  public Platform getPlatform();

  /**
   * Sets the platform of this Experiment object.
   *
   * @param platform platform.
   *
   */
  public void setPlatform(Platform platform);

  /**
   * Returns the run of this Experiment object.
   *
   * @return Run run.
   */
  public Run getRun();

  /**
   * Sets the run of this Experiment object.
   *
   * @param run run.
   *
   */
  public void setRun(Run run);

  /**
   * Sets the pool of this Experiment object.
   *
   * @param pool pool.
   *
   */
  public void setPool(Pool pool);

  /**
   * Returns the pool of this Experiment object.
   *
   * @return Pool pool.
   */
  public Pool<? extends Poolable> getPool();

  /**
   * Sets the kits of this Experiment object.
   *
   * @param kits kits.
   */
  public void setKits(Collection<Kit> kits);

  /**
   * Add a Kit to this Experiment object
   *
   * @param kit of type Kit
   */
  public void addKit(Kit kit);

  /**
   * Returns the kits used to construct this Experiment object.
   *
   * @return Collection<Kit> kits.
   */
  public Collection<Kit> getKits();

  /**
   * Return kits used within this experiment of a given KitType
   *
   * @param kitType of type KitType
   * @return Collection<Kit>
   */
  public Collection<Kit> getKitsByKitType(KitType kitType);
}
