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
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;

import org.gradle.api.Buildable;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.file.collections.MinimalFileSet;
import org.gradle.api.internal.tasks.DefaultTaskDependency;
import org.gradle.api.tasks.TaskDependency;
//import org.gradle.model.ModelElement;
import org.gradle.platform.base.ComponentSpec;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;

class APISharedLinkFileSet implements MinimalFileSet, Buildable {

  private final Logger logger;

  protected final JFSharedLibraryBinarySpecInternal owner;
  protected final DomainObjectSet<ComponentSpec>    inputInterfaceSets;

  public APISharedLinkFileSet(
      final JFSharedLibraryBinarySpecInternal owner,
      final DomainObjectSet<ComponentSpec>    inputInterfaceSets)
  {
    this.logger             = LoggerFactory.getLogger(this.getClass());
    this.owner              = owner;
    this.inputInterfaceSets = inputInterfaceSets;
  }

  @Override
  public String getDisplayName() {
    return "Link files for " + owner.getDisplayName();
  }

  @Override
  public Set<File> getFiles() {
    Set<File> linkFiles = new LinkedHashSet<File>();
    if (owner.getSharedLibraryLinkFile() != null)
      linkFiles.add(owner.getSharedLibraryLinkFile());
    for (JFHeaderExportingDependentInterfaceSet interfaceSet : inputInterfaceSets.withType(JFHeaderExportingDependentInterfaceSet.class)) {
      for (Object obj : interfaceSet.getHeaderReexportLibs()) {
        NativeBinaryResolveResult resolution = new NativeBinaryResolveResult(owner, Collections.singleton(obj));
        owner.getResolver().resolve(resolution);
        for (NativeLibraryBinary nativeLibraryBinary : resolution.getAllLibraryBinaries()) {
          linkFiles.addAll(nativeLibraryBinary.getLinkFiles().getFiles());
        }
      }
    }
    return linkFiles;
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

