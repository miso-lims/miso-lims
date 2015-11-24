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

package uk.ac.bbsrc.tgac.miso.core.plugin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * uk.ac.bbsrc.tgac.miso.core.plugin.annotation
 * 
 * <p/>
 * An annotation used to describe method parameters taken by any method tagged with an annotation. Any parameters present in this method
 * should be annotated with this annotation to provide the parameter name and description, used to inform the consumer about what this
 * parameter is for.
 * 
 * @author Tony Burdett
 * @author Rob Davey
 * @since 0.0.2
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MisoParameter {
  /**
   * The name of the parameter to be supplied when executing an operation. When using this annotation, you can legitimately supply any
   * string.
   * 
   * @return The name assigned to this parameter
   */
  String name();

  /**
   * The parameter description. This is optional.
   * 
   * @return The description associated with the parameter
   */
  String description() default "";
}
