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

import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrary;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
//import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.PolymorphicDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.model.ModelMap;
import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.platform.internal.NativePlatforms;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.platform.base.PlatformContainer;

class DefaultJFPrebuiltLibrary implements JFPrebuiltLibrary {
  // Constants
  private static final ModelType<LanguageSourceSet>   LANGUAGE_SOURCE_SET_MODEL_TYPE = ModelType.of(LanguageSourceSet.class);

  private final Logger                                logger;

  private final MutableModelNode                      interfaces;

  // Properties required by gradle
  private final String                                name;
  private final SourceDirectorySet                    headers;
  private final DomainObjectSet<NativeLibraryBinary>  binaries;

  public DefaultJFPrebuiltLibrary(
      final String              name,
      final PlatformContainer   platforms,
      final BuildTypeContainer  buildTypes,
      final FlavorContainer     flavors,
      final ServiceRegistry     serviceRegistry)
  {
    this.logger        = LoggerFactory.getLogger(this.getClass());
    this.interfaces    = null;
    this.name          = name;
    this.headers       = serviceRegistry.get(SourceDirectorySetFactory.class).create("headers");
    this.binaries      = new DefaultDomainObjectSet<NativeLibraryBinary>(NativeLibraryBinary.class);

    NativePlatforms   nativePlatforms = serviceRegistry.get(NativePlatforms.class);

    Set<NativePlatform> allPlatforms = new HashSet<NativePlatform>();

//  logger.debug("platforms:       " + platforms.withType(NativePlatform.class));
//  logger.debug("nativePlatforms: " + nativePlatforms.defaultPlatformDefinitions());
    allPlatforms.addAll(platforms.withType(NativePlatform.class));
    allPlatforms.addAll(nativePlatforms.defaultPlatformDefinitions());
//  logger.debug("allPlatforms: " + allPlatforms);

    for (NativePlatform platform : allPlatforms) {
      for (BuildType buildType : buildTypes) {
        for (Flavor flavor : flavors) {
          logger.debug("BINARY "+name+" on platform: " + platform + " " + buildType + " " + flavor);
          binaries.add(new DefaultJFPrebuiltStaticLibraryBinary(this, flavor, platform, buildType, serviceRegistry));
          binaries.add(new DefaultJFPrebuiltSharedLibraryBinary(this, flavor, platform, buildType, serviceRegistry));
        }
      }
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public SourceDirectorySet getHeaders() {
    return headers;
  }

  @Override
  public DomainObjectSet<NativeLibraryBinary> getBinaries() {
    return binaries;
  }

  @Override
  public ModelMap<LanguageSourceSet> getInterfaces() {
    return ModelMaps.toView(interfaces, LANGUAGE_SOURCE_SET_MODEL_TYPE);
  }
  
}
