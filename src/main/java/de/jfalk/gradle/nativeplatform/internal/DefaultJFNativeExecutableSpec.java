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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;

import de.jfalk.gradle.nativeplatform.JFNativeExecutableSpec;

import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.internal.AbstractTargetedNativeComponentSpec;

public class DefaultJFNativeExecutableSpec extends AbstractTargetedNativeComponentSpec implements JFNativeExecutableSpec {
  private final Logger logger;
  protected boolean enableFlavorsAndBuildTypes = false;

  public DefaultJFNativeExecutableSpec() {
    this.logger = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFNativeExecutableSpec::DefaultJFNativeExecutableSpec() [CALLED]");
  }

  @Override
  public Set<Flavor> chooseFlavors(Set<? extends Flavor> candidates) {
    Set<Flavor> retval = new HashSet<Flavor>();
    if (enableFlavorsAndBuildTypes)
      retval = super.chooseFlavors(candidates);
    logger.debug("DefaultJFNativeExecutableSpec::chooseFlavors(...) for " + this.getName() + " => " + retval);
    return retval;
  }

  @Override
  public Set<BuildType> chooseBuildTypes(Set<? extends BuildType> candidates) {
    Set<BuildType> retval = new HashSet<BuildType>();
    if (enableFlavorsAndBuildTypes)
      retval = super.chooseBuildTypes(candidates);
    logger.debug("DefaultJFNativeExecutableSpec::chooseBuildTypes(...) for " + this.getName() + " => " + retval);
    return retval;
  }

//// This methods is used by the AbstractComponentSpec base class in getDisplayName().
//@Override
//protected String getTypeName() {
//  logger.debug("DefaultJFNativeExecutableSpec::getTypeName() [CALLED]");
//  return "DefaultJFNativeExecutableSpec";
//}
}
