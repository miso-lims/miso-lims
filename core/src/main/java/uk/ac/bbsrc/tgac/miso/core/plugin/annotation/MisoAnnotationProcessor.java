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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * A processor for MISO annotations. You should use this processor when compiling MISO tools, as it extracts metadata encapsulated in
 * annotations to tool configuration files which can then be read by consumers of these tools.
 * 
 * @author Tony Burdett
 * @author Rob Davey
 * @date 20-Jul-2010
 * @since 0.0.2
 */
public class MisoAnnotationProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
    PrintWriter writer = null;
    try {
      // collection of captured classes
      final Set<TypeElement> misoElements = new HashSet<TypeElement>();

      // check everything annotated with @MisoParameter annotations -
      // we can only really apply this to String params
      for (Element element : roundEnv.getElementsAnnotatedWith(MisoParameter.class)) {
        if (!(element.asType().toString().equals(String.class.getName()))) {
          processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@MisoParameter annotations can only be applied to "
              + String.class.getName() + " arguments (not " + element.asType().toString() + " '" + element.getSimpleName() + "')", element);
        }
      }

      // check all elements annotated with @MisoPlugin
      for (Element element : roundEnv.getElementsAnnotatedWith(MisoPlugin.class)) {
        ElementVisitor<Void, Void> visitor = new SimpleElementVisitor6<Void, Void>() {
          @Override
          public Void visitType(TypeElement element, Void aVoid) {
            // check this type is a class
            if (element.getKind().isClass()) {
              // this class is annotated with @MisoPlugin, write out an entry
              misoElements.add(element);
            }

            return super.visitType(element, aVoid);
          }
        };
        element.accept(visitor, null);
      }

      // now, iterate over all classes annotated with @MisoPlugin, and write to file
      if (misoElements.size() > 0) {
        // printwriter for writing list of MISO plugins
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating services for MISO plugins");

        FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/miso/plugins");

        Writer w = fo.openWriter();
        writer = new PrintWriter(w);

        for (TypeElement misoElement : misoElements) {
          writer.println(misoElement.getQualifiedName());
        }
      }

      return true;
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Problem creating META-INF/miso/plugins file");
      return false;
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> result = new HashSet<String>();
    result.add(MisoPlugin.class.getName());
    return result;
  }

  @Override
  public Set<String> getSupportedOptions() {
    // no supported options
    return Collections.emptySet();
  }
}
