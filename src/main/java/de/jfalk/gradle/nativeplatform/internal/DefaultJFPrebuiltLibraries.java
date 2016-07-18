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

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraries;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrary;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.platform.base.PlatformContainer;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.FlavorContainer;

public class DefaultJFPrebuiltLibraries extends AbstractNamedDomainObjectContainer<JFPrebuiltLibrary> implements JFPrebuiltLibraries {
  private String                    name;
  private final PlatformContainer   platforms;
  private final BuildTypeContainer  buildTypes;
  private final FlavorContainer     flavors;
  private final ServiceRegistry     serviceRegistry;

  public DefaultJFPrebuiltLibraries(
      final String              name,
      final Instantiator        instantiator,
      final PlatformContainer   platforms,
      final BuildTypeContainer  buildTypes,
      final FlavorContainer     flavors,
      final ServiceRegistry     serviceRegistry)
  {
    super(JFPrebuiltLibrary.class, instantiator);
    this.name            = name;
    this.platforms       = platforms;
    this.buildTypes      = buildTypes;
    this.flavors         = flavors;
    this.serviceRegistry = serviceRegistry;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  protected JFPrebuiltLibrary doCreate(String name) {
    return getInstantiator().newInstance(DefaultJFPrebuiltLibrary.class, name,
      platforms, buildTypes, flavors, serviceRegistry);
  }

  @Override
  public JFPrebuiltLibrary resolveLibrary(String name) {
    JFPrebuiltLibrary library = findByName(name);
//  if (library != null && library.getBinaries().isEmpty()) {
//    libraryInitializer.execute(library);
//  }
    return library;
  }

}
