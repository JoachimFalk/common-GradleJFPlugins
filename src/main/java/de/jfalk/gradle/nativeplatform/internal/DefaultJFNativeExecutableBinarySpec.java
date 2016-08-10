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

import de.jfalk.gradle.nativeplatform.JFNativeExecutableBinarySpec;

import org.gradle.api.DomainObjectSet;
import org.gradle.nativeplatform.internal.DefaultNativeExecutableBinarySpec;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.nativeplatform.Tool;
import org.gradle.platform.base.ComponentSpec;

public class DefaultJFNativeExecutableBinarySpec extends DefaultNativeExecutableBinarySpec implements JFNativeExecutableBinarySpec, JFNativeBinarySpecInternal {

  private final Logger              logger;

  private final Tool                linker;
  private final PreprocessingTool   cCompiler;
  private final PreprocessingTool   cppCompiler;

  private NativeDependencyResolver  resolver;

  private String flammy;

  public DefaultJFNativeExecutableBinarySpec() {
    this.logger = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFNativeExecutableBinarySpec() [CALLED]");
    @SuppressWarnings("unchecked")
    DomainObjectSet<ComponentSpec> inputs = (DomainObjectSet) this.getInputs();
    this.linker      = new ToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.LinkterToolLocator(), false);
    this.cCompiler   = new PreprocessingToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.CCompilerToolLocator(), false);
    this.cppCompiler = new PreprocessingToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.CppCompilerToolLocator(), false);
    logger.debug("DefaultJFNativeExecutableBinarySpec() [DONE]");
  }

  /// Unfortunately, AbstractNativeBinarySpec.this.resolver is private and, thus, we
  /// have to store our own reference to the resolver.
  @Override
  public void setResolver(NativeDependencyResolver resolver) {
    super.setResolver(resolver);
    this.resolver = resolver;
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.internal.JFNativeBinarySpecInternal}.

  @Override
  public NativeDependencyResolver getResolver() {
    return this.resolver;
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.JFNativeExecutableBinarySpec}.

  @Override
  public String getFlammy()
    { return this.flammy; }

  @Override
  public void setFlammy(String flammy)
    { this.flammy = flammy; }

  // Override some stuff from AbstractNativeBinarySpec

  @Override
  public Tool getLinker() {
    return linker;
  }

  @Override
  public PreprocessingTool getcCompiler() {
    return cCompiler;
  }

  @Override
  public PreprocessingTool getCppCompiler() {
    return cppCompiler;
  }

  @Override
  public Tool getToolByName(String name) {
    if (name.equals("cCompiler")) {
      return getcCompiler();
    } else if (name.equals("cppCompiler")) {
      return getCppCompiler();
    } else
      return super.getToolByName(name);
  }

}
