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

import org.gradle.nativeplatform.SharedLibraryBinary;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.platform.NativePlatform;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.SimpleFileCollection;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import JFalkPrebuiltLibrary;

/// A {@link NativeLibrary} that has been compiled and linked as a shared library.
class JFalkSharedLibraryBinary implements SharedLibraryBinary {

  private final JFalkPrebuiltLibrary parent;

  private final Logger               logger;

  // Properties required by gradle
  private final String               name;
  private final Flavor               flavor;
  private final NativePlatform       platform;
  private final BuildType            buildType;

  public JFalkSharedLibraryBinary(JFalkPrebuiltLibrary parent, Flavor flavor, NativePlatform platform, BuildType buildType) {
    this.parent    = parent;
    this.logger    = LoggerFactory.getLogger(this.class);
    this.name      = parent.name;
    this.flavor    = flavor;
    this.platform  = platform;
    this.buildType = buildType;
  }

  /// Returns a human-consumable display name for this binary.
  String getDisplayName() {
    return "JFalkSharedLibraryBinary " + name;
  }

  /// The {@link org.gradle.nativeplatform.Flavor} that this binary was built with.
  Flavor getFlavor() { return flavor; }

  /// Returns the {@link org.gradle.nativeplatform.platform.NativePlatform} that this binary is targeted to run on.
  NativePlatform getTargetPlatform() { return platform; }

  /// Returns the {@link org.gradle.nativeplatform.BuildType} used to construct this binary.
  BuildType getBuildType() { return buildType; }

  FileCollection getHeaderDirs() {
    logger.debug( "JFalkSharedLibraryBinary::getHeaderDirs() [CALLED]");
    SimpleFileCollection retval = new SimpleFileCollection(parent.headers.getSrcDirs());

    return retval;
  }

  FileCollection getLinkFiles() {
    logger.debug( "JFalkSharedLibraryBinary::getLinkFiles() [CALLED]");
    return new SimpleFileCollection();
  }

  FileCollection getRuntimeFiles() {
    logger.debug( "JFalkSharedLibraryBinary::getRuntimeFiles() [CALLED]");
    return new SimpleFileCollection();
  }

  // Implement interface of SharedLibraryBinary.

  /// The shared library file.
  File getSharedLibraryFile() {
    logger.debug( "JFalkSharedLibraryBinary::getSharedLibraryFile() [CALLED]");
    return sharedLibraryFile;
  }

  /// The shared library link file.
  File getSharedLibraryLinkFile() {
    logger.debug( "JFalkSharedLibraryBinary::getSharedLibraryLinkFile() [CALLED]");
    return sharedLibraryFile;
  }

  // Model configuration properties.
  private File sharedLibraryFile;
}


