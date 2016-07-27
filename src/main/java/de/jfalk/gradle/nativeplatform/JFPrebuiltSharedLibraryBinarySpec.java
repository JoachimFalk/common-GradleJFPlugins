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

import java.io.File;

public interface JFPrebuiltSharedLibraryBinarySpec extends JFPrebuiltLibraryBinarySpec, JFSharedLibraryBinary {

  /// The shared library file (DSO).
  /// The corresponding getter interface is defined in {@link org.gradle.nativeplatform.SharedLibraryBinary}.
  void setSharedLibraryFile(final File sharedLibraryFile);

  /// The shared library link file.
  /// The corresponding getter interface is defined in {@link org.gradle.nativeplatform.SharedLibraryBinary}.
  void setSharedLibraryLinkFile(final File sharedLibraryLinkFile);

}
