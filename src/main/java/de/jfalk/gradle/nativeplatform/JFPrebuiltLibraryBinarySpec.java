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

package de.jfalk.gradle.nativeplatform;

import java.util.Collection;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;

import org.gradle.platform.base.ComponentSpec;
import org.gradle.nativeplatform.NativeComponentSpec;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.NativeDependencySet;
import org.gradle.model.ModelMap;
import org.gradle.nativeplatform.NativeLibraryBinarySpec;

public interface JFPrebuiltLibraryBinarySpec extends ComponentSpec, JFNativeLibraryBinary {
  /**
   * The component that this binary belongs to.
   */
  JFPrebuiltLibrarySpec getComponent();

  /**
   * Adds a library as input to this binary.
   * <p/>
   * This method accepts the following types:
   *
   * <ul>
   *     <li>A {@link NativeLibrarySpec}</li>
   *     <li>A {@link NativeDependencySet}</li>
   *     <li>A {@link java.util.Map} containing the library selector.</li>
   * </ul>
   *
   * The Map notation supports the following String attributes:
   *
   * <ul>
   *     <li>project: the path to the project containing the library (optional, defaults to current project)</li>
   *     <li>library: the name of the library (required)</li>
   *     <li>linkage: the library linkage required ['shared'/'static'] (optional, defaults to 'shared')</li>
   * </ul>
   */
  void lib(Object library);

  ModelMap<JFHeaderExportingDependentInterfaceSet> getInterfaces();
}
