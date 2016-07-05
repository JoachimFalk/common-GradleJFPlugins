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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.gradle.api.file.FileCollection;
import org.gradle.nativeplatform.StaticLibraryBinarySpec;
import org.gradle.nativeplatform.internal.DefaultStaticLibraryBinarySpec;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;

interface JFStaticLibraryBinarySpec extends StaticLibraryBinarySpec {

  String getFlammy();

  void setFlammy(String flammy);
}

class DefaultJFStaticLibraryBinarySpec extends DefaultStaticLibraryBinarySpec implements JFStaticLibraryBinarySpec {

  private final Logger                    logger;
  private final JFCommonLibraryBinarySpec commonHelpers;

  private   String                    flammy;
  protected NativeDependencyResolver  resolver;

  DefaultJFStaticLibraryBinarySpec() {
    this.logger        = LoggerFactory.getLogger(this.class);
    this.commonHelpers = new JFCommonLibraryBinarySpec(this);
  }

  @Override
  public FileCollection getHeaderDirs() {
    return commonHelpers.extendHeaderDirs(super.getHeaderDirs());
  }

  /// Unfortunately, AbstractNativeBinarySpec.this.resolver is private and, thus, we
  /// have to store our own reference to the resolver.
  @Override
  public void setResolver(NativeDependencyResolver resolver) {
    super.setResolver(resolver);
    this.resolver = resolver;
  }

  @Override
  public String getFlammy()
    { return this.flammy; }

  @Override
  public void setFlammy(String flammy)
    { this.flammy = flammy; }

}
