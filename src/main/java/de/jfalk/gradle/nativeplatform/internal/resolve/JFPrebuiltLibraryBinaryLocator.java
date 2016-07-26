// Copyright (C) 2016 Joachim Falk <joachim.falk@gmx.de>
//
// This file is part of the GradleJFPlugins project of Joachim Falk; you can
// redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version.
//
// The GradleJFPlugins project is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along with
// this program; If not, write to the Free Software Foundation, Inc., 59 Temple
// Place - Suite 330, Boston, MA 02111-1307, USA.

package de.jfalk.gradle.nativeplatform.internal.resolve;

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraryBinarySpec;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.internal.resolve.ProjectModelResolver;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.model.ModelMap;
import org.gradle.nativeplatform.internal.resolve.LibraryBinaryLocator;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.platform.base.ComponentSpecContainer;

public class JFPrebuiltLibraryBinaryLocator implements LibraryBinaryLocator {
  private final ProjectModelResolver projectModelResolver;

  public JFPrebuiltLibraryBinaryLocator(ProjectModelResolver projectModelResolver) {
    this.projectModelResolver = projectModelResolver;
  }

  // Converts the binaries of a project library into regular binary instances
  @Override
  public DomainObjectSet<NativeLibraryBinary> getBinaries(NativeLibraryRequirement requirement) {
    DomainObjectSet<NativeLibraryBinary> retval = new DefaultDomainObjectSet<NativeLibraryBinary>(NativeLibraryBinary.class);
    ModelRegistry                        projectModel = projectModelResolver.resolveProjectModel(requirement.getProjectPath());
    ComponentSpecContainer               components   = projectModel.find("components", ComponentSpecContainer.class);
    if (components != null) {
      JFPrebuiltLibrarySpec library = components.withType(JFPrebuiltLibrarySpec.class).get(requirement.getLibraryName());
      if (library != null) {
        retval.addAll(library.getBinaries().values());
      }
    }
    return retval;
  }
}
