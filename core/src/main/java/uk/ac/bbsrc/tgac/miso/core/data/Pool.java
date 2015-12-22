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

import java.util.Collection;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * A Pool represents a collection of one or more {@link Poolable} objects, which enables multiplexing to be modelled if necessary. Pools
 * provide the link between the {@link Sample} tree and the {@link Run} tree of the MISO data model, which means that multiple samples from
 * multiple {@link Project}s can be pooled together.
 * <p/>
 * Pools are typed by the {@link Poolable} interface type they can accept, and as such, Pools can accept {@link Dilution} and {@link Plate}
 * objects at present. At creation time, a Pool is said to be "ready to run", which makes it easy to categorise and list Pools according to
 * whether they have been placed on a {@link SequencerPoolPartition} (at which point ready to run becomes false) or not.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "dilutions" })
@PrintableBarcode
public interface Pool<P extends Poolable> extends SecurableByProfile, Comparable, Barcodable, Watchable, Deletable, Alertable, Boxable {
  /**
   * Returns the poolId of this Pool object.
   * 
   * @return Long poolId.
   */
  @Deprecated
  public Long getPoolId();

  /**
   * Sets the poolId of this Pool object.
   * 
   * @param poolId
   *          poolId.
   * 
   */
  @Deprecated
  public void setPoolId(Long poolId);

  /**
   * Sets the ID of this Pool object.
   * 
   * @param id
   *          long.
   */
  public void setId(long id);

  /**
   * Sets the name of this Pool object.
   * 
   * @param name
   *          name.
   * 
   */
  public void setName(String name);

  /**
   * Returns the alias of this Pool object.
   * 
   * @return String alias.
   */
  @Override
  public String getAlias();

  /**
   * Sets the alias of this Pool object.
   * 
   * @param alias
   *          alias.
   * 
   */
  @Override
  public void setAlias(String alias);

  /**
   * Adds a Poolable element to this Pool
   * 
   * @param poolable
   *          element of type P
   * @throws MalformedDilutionException
   *           when the Dilution added is not valid
   */
  public void addPoolableElement(P poolable) throws MalformedDilutionException;

  /**
   * Sets the Poolable elements of this Pool object.
   * 
   * @param poolables
   *          poolables.
   */
  public <T extends Poolable> void setPoolableElements(Collection<T> poolables);

  /**
   * Returns the Poolable elements of this Pool object.
   * 
   * @return Collection<D> poolables.
   */
  public Collection<P> getPoolableElements();

  /**
   * Convenience method to return Dilutions from this Pool given that the Pooled Elements may well
   * 
   * @return Collection<D> poolables.
   */
  public Collection<? extends Dilution> getDilutions();

  /**
   * Registers an Experiment to this Pool
   * 
   * @param experiment
   *          of type Experiment
   * @throws MalformedExperimentException
   *           when
   */
  public void addExperiment(Experiment experiment) throws MalformedExperimentException;

  /**
   * Sets the experiments related to this Pool object.
   * 
   * @param experiments
   *          experiments.
   */
  public void setExperiments(Collection<Experiment> experiments);

  /**
   * Returns the experiments related to this Pool object.
   * 
   * @return Experiment experiment.
   */
  public Collection<Experiment> getExperiments();

  /**
   * Returns the creationDate of this Pool object.
   * 
   * @return Date creationDate.
   */
  public Date getCreationDate();

  /**
   * Sets the creationDate of this Pool object.
   * 
   * @param creationDate
   *          creationDate.
   */
  public void setCreationDate(Date creationDate);

  /**
   * Returns the concentration of this Pool object.
   * 
   * @return Double concentration.
   */
  public Double getConcentration();

  /**
   * Sets the concentration of this Pool object.
   * 
   * @param concentration
   *          concentration.
   */
  public void setConcentration(Double concentration);

  /**
   * Returns the platformType of this Platform object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType();

  /**
   * Sets the platformType of this Platform object.
   * 
   * @param name
   *          platformType.
   */
  public void setPlatformType(PlatformType name);

  /**
   * Checks if this Pool is ready to be run
   * 
   * @return boolean ready.
   */
  public boolean getReadyToRun();

  /**
   * Sets the ready to run status of this Pool
   * 
   * @param ready
   *          Boolean.
   */
  public void setReadyToRun(boolean ready);

  /**
   * Registers that a LibraryQC has been carried out on this Library
   * 
   * @param poolQC
   *          of type PoolQC
   * @throws MalformedPoolQcException
   *           when the PoolQC being added is not valid
   */
  public void addQc(PoolQC poolQC) throws MalformedPoolQcException;

  /**
   * Returns the poolQCs of this Pool object.
   * 
   * @return Collection<PoolQC> poolQCs.
   */
  public Collection<PoolQC> getPoolQCs();

  /**
   * Returns the qcPassed of this Pool object.
   * 
   * @return Boolean qcPassed.
   */
  public Boolean getQcPassed();

  /**
   * Sets the qcPassed attribute of this Pool object. This should be true when a suitable QC has been carried out that passes a given
   * result.
   * 
   * @param qcPassed
   *          qcPassed.
   */
  public void setQcPassed(Boolean qcPassed);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  public Collection<ChangeLog> getChangeLog();

  /**
   * Returns the user who last modified this item.
   */
  public User getLastModifier();

  /**
   * Sets the user who last modified this item. It should always be set to the current user on save.
   */
  public void setLastModifier(User user);

  public boolean getHasLowQualityMembers();

}
