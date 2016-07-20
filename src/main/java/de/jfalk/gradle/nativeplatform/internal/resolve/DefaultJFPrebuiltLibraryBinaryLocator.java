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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.nativeplatform.JFRepositoriesSpec;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrary;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraries;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.resolve.ProjectModelResolver;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.nativeplatform.Repositories;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.internal.resolve.LibraryBinaryLocator;

public class DefaultJFPrebuiltLibraryBinaryLocator implements LibraryBinaryLocator {
  private final Logger               logger;
  private final ProjectModelResolver projectModelResolver;

  public DefaultJFPrebuiltLibraryBinaryLocator(ProjectModelResolver projectModelResolver) {
    this.logger               = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFPrebuiltLibraryBinaryLocator(...) [CALLED]");
    this.projectModelResolver = projectModelResolver;
    logger.debug("DefaultJFPrebuiltLibraryBinaryLocator(...) [DONE]");
  }

  @Override
  public DomainObjectSet<NativeLibraryBinary> getBinaries(NativeLibraryRequirement requirement) {
    logger.debug("getBinaries(...) for " + requirement +" [CALLED]");
    ModelRegistry projectModel = projectModelResolver.resolveProjectModel(requirement.getProjectPath());
    JFRepositoriesSpec flummy =
      projectModel.realize("flummy", JFRepositoriesSpec.class);
    NamedDomainObjectSet<JFPrebuiltLibraries> repositories =
      projectModel.realize("repositories", Repositories.class).withType(JFPrebuiltLibraries.class);
    DomainObjectSet<NativeLibraryBinary> retval = null;
    for (JFPrebuiltLibraries prebuiltLibraries : repositories) {
      JFPrebuiltLibrary prebuiltLibrary = prebuiltLibraries.resolveLibrary(requirement.getLibraryName());
      if (prebuiltLibrary != null) {
        retval = prebuiltLibrary.getBinaries();
        break;
      }
    }
    logger.debug("getBinaries(...) for " + requirement +" [DONE]");
    return retval;
  }
}
