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

package de.jfalk.gradle.nativeplatform.internal;

import java.io.File;
import java.util.Set;
import java.util.LinkedHashSet;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;

import org.gradle.api.Buildable;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.file.collections.MinimalFileSet;
import org.gradle.api.internal.tasks.DefaultTaskDependency;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.model.ModelElement;
import org.gradle.platform.base.ComponentSpec;

class APIHeadersFileSet implements MinimalFileSet, Buildable {

  protected final ModelElement                   owner;
  protected final DomainObjectSet<ComponentSpec> inputInterfaceSets;

  public APIHeadersFileSet(
      final ModelElement                   owner,
      final DomainObjectSet<ComponentSpec> inputInterfaceSets)
  {
    this.owner              = owner;
    this.inputInterfaceSets = inputInterfaceSets;
  }

  @Override
  public String getDisplayName() {
    return "API Headers for " + owner.getDisplayName();
  }

  @Override
  public Set<File> getFiles() {
    Set<File> headerDirs = new LinkedHashSet<File>();
    for (JFHeaderExportingDependentInterfaceSet sourceSet : inputInterfaceSets.withType(JFHeaderExportingDependentInterfaceSet.class)) {
      headerDirs.addAll(sourceSet.getExportedHeaders().getSrcDirs());
    }
    return headerDirs;
  }

  @Override
  public TaskDependency getBuildDependencies() {
    DefaultTaskDependency dependency = new DefaultTaskDependency();
//  for (JFHeaderExportingDependentInterfaceSet sourceSet : inputInterfaceSets.withType(JFHeaderExportingDependentInterfaceSet.class)) {
//    dependency.add(sourceSet.getBuildDependencies());
//  }
    return dependency;
  }
}

