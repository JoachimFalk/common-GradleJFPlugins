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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.nativeplatform.JFSharedLibraryBinarySpec;

import org.gradle.api.file.FileCollection;
import org.gradle.nativeplatform.internal.DefaultSharedLibraryBinarySpec;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;

public class DefaultJFSharedLibraryBinarySpec extends DefaultSharedLibraryBinarySpec implements JFSharedLibraryBinarySpec {

  private final Logger                    logger;
  private final JFCommonLibraryBinarySpec commonHelpers;

  private   String                    flummy;
  protected NativeDependencyResolver  resolver;

  public DefaultJFSharedLibraryBinarySpec() {
    this.logger        = LoggerFactory.getLogger(this.getClass());
    this.commonHelpers = new JFCommonLibraryBinarySpec(this);
  }

  @Override
  public FileCollection getHeaderDirs() {
    return commonHelpers.extendHeaderDirs(super.getHeaderDirs(), this.resolver);
  }
  
  @Override
  public FileCollection getLinkFiles() {
    return commonHelpers.extendLinkFiles(super.getLinkFiles(), this.resolver);
  }

  @Override
  public FileCollection getRuntimeFiles() {
    FileCollection retval = super.getRuntimeFiles();

    return retval;
  }

  /// Unfortunately, AbstractNativeBinarySpec.this.resolver is private and, thus, we
  /// have to store our own reference to the resolver.
  @Override
  public void setResolver(NativeDependencyResolver resolver) {
    super.setResolver(resolver);
    this.resolver = resolver;
  }

  @Override
  public String getFlummy()
    { return this.flummy; }

  @Override
  public void setFlummy(String flummy)
    { this.flummy = flummy; }

}
