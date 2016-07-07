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

//import javax.inject.Inject;

import org.slf4j.Logger
import org.slf4j.LoggerFactory


import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.nativeplatform.internal.resolve.ApiRequirementNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.InputHandlingNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.LibraryBinaryLocator;
import org.gradle.nativeplatform.internal.resolve.LibraryNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.RequirementParsingNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.SourceSetNativeDependencyResolver;

public class JFNativeDependencyResolver implements NativeDependencyResolver {
  private final Logger                   logger;
  private final NativeDependencyResolver resolver;

//@Inject
  public JFNativeDependencyResolver(LibraryBinaryLocator locator, FileCollectionFactory fileCollectionFactory) {
    this.logger = LoggerFactory.getLogger(this.class);
    logger.debug("JFNativeDependencyResolver::JFNativeDependencyResolver(...) [CALLED]")
    resolver = new LibraryNativeDependencyResolver(locator);
    resolver = new ApiRequirementNativeDependencyResolver(resolver, fileCollectionFactory);
    resolver = new RequirementParsingNativeDependencyResolver(resolver);
    resolver = new SourceSetNativeDependencyResolver(resolver, fileCollectionFactory);
    resolver = new InputHandlingNativeDependencyResolver(resolver);
    logger.debug("JFNativeDependencyResolver::JFNativeDependencyResolver(...) [DONE]")
  }

  @Override
  public void resolve(NativeBinaryResolveResult resolution) {
    logger.debug("JFNativeDependencyResolver::resolve(...) [CALLED]")
    resolver.resolve(resolution);
    logger.debug("JFNativeDependencyResolver::resolve(...) [DONE]")
  }
}
