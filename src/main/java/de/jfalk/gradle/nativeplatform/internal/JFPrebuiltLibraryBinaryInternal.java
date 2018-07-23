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

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraryBinarySpec;

import org.gradle.api.DomainObjectSet;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.platform.NativePlatform;

public interface JFPrebuiltLibraryBinaryInternal extends JFPrebuiltLibraryBinarySpec {

  void setFlavor(Flavor flavor);

//void setToolChain(NativeToolChain nativeToolChain);

  void setTargetPlatform(NativePlatform nativePlatform);

  void setBuildType(BuildType buildType);

  void setResolver(NativeDependencyResolver resolver);

  DomainObjectSet<JFHeaderExportingDependentInterfaceSet> getInterfaceSets();

}
