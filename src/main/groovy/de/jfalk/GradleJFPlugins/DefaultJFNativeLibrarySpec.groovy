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

import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.nativeplatform.internal.AbstractTargetedNativeComponentSpec;
import org.gradle.nativeplatform.internal.ProjectNativeLibraryRequirement;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface JFNativeLibrarySpec extends NativeLibrarySpec {
}

public class DefaultJFNativeLibrarySpec extends AbstractTargetedNativeComponentSpec implements JFNativeLibrarySpec {
  private final Logger logger;

  public DefaultJFNativeLibrarySpec() {
    this.logger = LoggerFactory.getLogger(this.class);
    logger.debug("DefaultJFNativeLibrarySpec::DefaultJFNativeLibrarySpec() [CALLED]");
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
