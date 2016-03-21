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

package de.jfalk.GradleJFPlugins;

import org.gradle.api.Project;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
//import org.gradle.api.internal.file.DefaultSourceDirectorySet;

import org.gradle.nativeplatform.PrebuiltLibrary;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.platform.internal.NativePlatforms;

import org.gradle.internal.service.ServiceRegistry;
import org.gradle.platform.base.PlatformContainer;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JFalkPrebuiltLibrary implements PrebuiltLibrary {

  private final Project                               parentProject;
  private final Logger                                logger;

  // Properties required by gradle
  private final String                                name;
  private final SourceDirectorySet                    headers;
  private final DomainObjectSet<NativeLibraryBinary>  binaries;

  public JFalkPrebuiltLibrary(String name, Project project) {
    ServiceRegistry serviceRegistry = project.getServices();

    this.parentProject = project;
    this.logger        = LoggerFactory.getLogger(this.class);
    this.name          = name;
    this.headers       = serviceRegistry.get(SourceDirectorySetFactory.class).create("headers");
    this.binaries      = new DefaultDomainObjectSet<NativeLibraryBinary>(NativeLibraryBinary.class);

//  ModelRegistry   modelRegistry   = project.getModelRegistry();

    NativePlatforms   nativePlatforms = serviceRegistry.get(NativePlatforms.class);
//  FileResolver      fileResolver    = serviceRegistry.get(FileResolver.class);
    PlatformContainer platforms       = project.getExtensions().findByName("platforms");

    Set<NativePlatform> allPlatforms = new LinkedHashSet<NativePlatform>();

    // toolChains = project.getExtensions().getByType(NativeToolChainRegistryInternal.class);
//  logger.debug("platforms:       " + platforms.withType(NativePlatform.class));
//  logger.debug("nativePlatforms: " + nativePlatforms.defaultPlatformDefinitions());
    allPlatforms.addAll(platforms.withType(NativePlatform.class));
    allPlatforms.addAll(nativePlatforms.defaultPlatformDefinitions());
//  logger.debug("allPlatforms: " + allPlatforms);

    for (NativePlatform platform : allPlatforms) {
      for (BuildType buildType : project.getExtensions().getByType(BuildTypeContainer.class)) {
        for (Flavor flavor : project.getExtensions().getByType(FlavorContainer.class)) {
          logger.debug("BINARY "+name+" on platform: " + platform + " " + buildType + " " + flavor);
          binaries.add(new JFalkStaticLibraryBinary(this, flavor, platform, buildType));
          binaries.add(new JFalkSharedLibraryBinary(this, flavor, platform, buildType));
        }
      }
    }
  }

  public String getName() {
    return name;
  }

  public SourceDirectorySet getHeaders() {
    return headers;
  }

  public DomainObjectSet<NativeLibraryBinary> getBinaries() {
    return binaries;
  }
}

//@Managed
//trait JFalkPrebuiltLibraryX implements PrebuiltLibrary {
//
//    private final String name;
//    private final SourceDirectorySet headers;
//    private final DomainObjectSet<NativeLibraryBinary> binaries;
//
//    public String getName() {
//        return name;
//    }
//
//    public SourceDirectorySet getHeaders() {
//        return headers;
//    }
//
//    public DomainObjectSet<NativeLibraryBinary> getBinaries() {
//        return binaries;
//    }
//}
