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

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.language.cpp.JFCppSourceSet;

import org.gradle.nativeplatform.NativeDependencySet;
import org.gradle.nativeplatform.internal.AbstractNativeLibraryBinarySpec;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.api.file.FileCollection;

class JFCommonLibraryBinarySpec<T extends AbstractNativeLibraryBinarySpec> {

  private final Logger                          logger;
  private final AbstractNativeLibraryBinarySpec nativeLibraryBinary;

  JFCommonLibraryBinarySpec(AbstractNativeLibraryBinarySpec nativeLibraryBinary) {
    this.logger              = LoggerFactory.getLogger(this.getClass());
    this.nativeLibraryBinary = nativeLibraryBinary;
  }

  public FileCollection extendHeaderDirs(FileCollection headerDirs, NativeDependencyResolver nativeDependencyResolver) {
//  logger.debug("extendHeaderDirs(...) for " + nativeLibraryBinary + " [CALLED]");
//  NativeDependencyResolver nativeDependencyResolver = nativeLibraryBinary.resolver;
    // Handle reexporting of headers from libs via exportHeaders flag.
    for (JFCppSourceSet jfCppSourceSet : nativeLibraryBinary.getInputs().withType(JFCppSourceSet.class)) {
//    logger.debug("  input: " + jfCppSourceSet);
      for (Object obj : jfCppSourceSet.getHeaderReexportLibs()) {
//      logger.debug("    header reexporting lib: " + obj);
        NativeBinaryResolveResult resolution = new NativeBinaryResolveResult(nativeLibraryBinary, Collections.singleton(obj));
        nativeDependencyResolver.resolve(resolution);
        for (NativeDependencySet nativeDependencySet: resolution.getAllResults()) {
//        logger.debug("    header reexporting from: " + flummy.getIncludeRoots());
          headerDirs = headerDirs.plus(nativeDependencySet.getIncludeRoots());
        }
      }
    }
//  logger.debug("extendHeaderDirs(...) for " + nativeLibraryBinary + " [DONE]");
    logger.debug("extendHeaderDirs(...) for " + nativeLibraryBinary + " => " + headerDirs.getFiles());
    return headerDirs;
  }

  public FileCollection extendLinkFiles(FileCollection linkFiles, NativeDependencyResolver nativeDependencyResolver) {
    // Handle transitive library requirements via exportHeaders flag.
    for (JFCppSourceSet jfCppSourceSet : nativeLibraryBinary.getInputs().withType(JFCppSourceSet.class)) {
      for (Object obj : jfCppSourceSet.getHeaderReexportLibs()) {
        NativeBinaryResolveResult resolution = new NativeBinaryResolveResult(nativeLibraryBinary, Collections.singleton(obj));
        nativeDependencyResolver.resolve(resolution);
        for (NativeDependencySet nativeDependencySet: resolution.getAllResults()) {
          linkFiles = linkFiles.plus(nativeDependencySet.getLinkFiles());
        }
      }
    }
    logger.debug("extendLinkFiles(...) for " + nativeLibraryBinary + " => " + linkFiles.getFiles());
    return linkFiles;
  }

}

