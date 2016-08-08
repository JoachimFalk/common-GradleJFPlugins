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

import java.util.List;
import java.util.Set;

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;

import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.platform.base.internal.PlatformRequirement;

public interface JFPrebuiltLibraryInternal extends JFPrebuiltLibrarySpec {
  List<PlatformRequirement> getTargetPlatforms();
  Set<Flavor>               chooseFlavors(Set<? extends Flavor> candidates);
  Set<BuildType>            chooseBuildTypes(Set<? extends BuildType> candidates);
}
