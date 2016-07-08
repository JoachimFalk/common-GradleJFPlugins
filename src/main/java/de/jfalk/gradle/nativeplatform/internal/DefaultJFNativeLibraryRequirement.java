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

import de.jfalk.gradle.nativeplatform.JFNativeLibraryRequirement;

public class DefaultJFNativeLibraryRequirement implements JFNativeLibraryRequirement {
  private final String projectPath;
  private final String libraryName;
  private final String linkage;
  private final String flavor;

  public DefaultJFNativeLibraryRequirement(String projectPath, String libraryName, String linkage, String flavor) {
    this.projectPath = projectPath;
    this.libraryName = libraryName;
    this.linkage = linkage;
    this.flavor = flavor;
  }

  @Override
  public String getProjectPath() {
    return projectPath;
  }

  @Override
  public String getLibraryName() {
    return libraryName;
  }

  @Override
  public String getLinkage() {
    return linkage;
  }

  @Override
  public String getFlavor() {
    return flavor;
  }
}
