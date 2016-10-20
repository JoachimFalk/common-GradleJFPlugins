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

package de.jfalk.gradle.language.nativeplatform;

import java.util.Collection;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.BuildableComponentSpec;
//import org.gradle.platform.base.ComponentSpec;

public interface JFHeaderExportingDependentInterfaceSet extends BuildableComponentSpec {
  /**
   * The libraries that this interface set requires.
   */
  Collection<?> getLibs();

  /**
   * Adds a library that this interface set requires. This method accepts the following types:
   *
   * <ul>
   *     <li>A {@link org.gradle.nativeplatform.NativeLibrarySpec}</li>
   *     <li>A {@link org.gradle.nativeplatform.NativeDependencySet}</li>
   *     <li>A {@link LanguageSourceSet}</li>
   *     <li>A {@link java.util.Map} containing the library selector.</li>
   * </ul>
   *
   * The Map notation supports the following String attributes:
   *
   * <ul>
   *     <li>project:       the path to the project containing the library (optional, defaults to current project)</li>
   *     <li>library:       the name of the library (required)</li>
   *     <li>linkage:       the library linkage required ['shared'/'static'] (optional, defaults to 'shared')</li>
   *     <li>flavor:        the flavor of the library required (optional, defaults to 'default')</li>
   *     <li>exportHeaders: if headers exported by the required library should be exported from this interface set in turn [flas/true] (optional, defaults to false)</li>
   * </ul>
   */
  void lib(Object library);

  /// The libs which have been marked by the exportHeaders flag.
  Collection<?> getHeaderReexportLibs();

  /**
   * The headers as a directory set.
   */
  SourceDirectorySet getExportedHeaders();

  String getLanguageName();
}
