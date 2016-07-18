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

import de.jfalk.gradle.nativeplatform.JFNativeLibrarySpec;

import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.internal.AbstractTargetedNativeComponentSpec;
import org.gradle.nativeplatform.internal.ProjectNativeLibraryRequirement;

public class DefaultJFNativeLibrarySpec extends AbstractTargetedNativeComponentSpec implements JFNativeLibrarySpec {
  private final Logger logger;
  protected boolean enableFlavorsAndBuildTypes = false;

  public DefaultJFNativeLibrarySpec() {
    this.logger = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFNativeLibrarySpec::DefaultJFNativeLibrarySpec() [CALLED]");
  }

  @Override
  public Set<Flavor> chooseFlavors(Set<? extends Flavor> candidates) {
    Set<Flavor> retval = new HashSet<Flavor>();
    if (enableFlavorsAndBuildTypes)
      retval = super.chooseFlavors(candidates);
    logger.debug("DefaultJFNativeLibrarySpec::chooseFlavors(...) for " + this.getName()+ " => " + retval);
    return retval;
  }

  @Override
  public Set<BuildType> chooseBuildTypes(Set<? extends BuildType> candidates) {
    Set<BuildType> retval = new HashSet<BuildType>();
    if (enableFlavorsAndBuildTypes)
      retval = super.chooseBuildTypes(candidates);
    logger.debug("DefaultJFNativeLibrarySpec::chooseBuildTypes(...) for " + this.getName() + " => " + retval);
    return retval;
  }

//// This methods is used by the AbstractComponentSpec base class in getDisplayName().
//@Override
//protected String getTypeName() {
//  logger.debug("DefaultJFNativeLibrarySpec::getTypeName() [CALLED]");
//  return "DefaultJFNativeLibrarySpec";
//}

  @Override
  public NativeLibraryRequirement getShared() {
    return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "shared");
  }

  @Override
  public NativeLibraryRequirement getStatic() {
    return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "static");
  }

  @Override
  public NativeLibraryRequirement getApi() {
    return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "api");
  }
}
