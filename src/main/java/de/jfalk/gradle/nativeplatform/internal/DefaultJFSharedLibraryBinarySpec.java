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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.nativeplatform.JFSharedLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.FileCollectionAdapter;
import org.gradle.nativeplatform.internal.DefaultSharedLibraryBinarySpec;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.nativeplatform.Tool;
import org.gradle.platform.base.ComponentSpec;

public class DefaultJFSharedLibraryBinarySpec extends DefaultSharedLibraryBinarySpec implements JFSharedLibraryBinarySpec, JFNativeLibraryBinarySpecInternal {

  private final Logger                    logger;

  private final FileCollection                        headerDirs;
  private final FileCollection                        linkFiles;
  private final FileCollection                        runtimeFiles;
  private final JFExportedCompileAndLinkConfiguration exportedCompileAndLinkConfiguration;
  private final Tool                                  linker;
  private final PreprocessingTool                     cCompiler;
  private final PreprocessingTool                     cppCompiler;

  protected NativeDependencyResolver  resolver;

  private   String                    flummy;

  public DefaultJFSharedLibraryBinarySpec() {
    this.logger = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFSharedLibraryBinarySpec() [CALLED]");
    @SuppressWarnings("unchecked")
    DomainObjectSet<ComponentSpec> inputs = (DomainObjectSet) this.getInputs();
    this.headerDirs                          = new FileCollectionAdapter(new APIHeadersFileSet(this, inputs));
    this.linkFiles                           = new FileCollectionAdapter(new APILinkFileSet(this, inputs));
    this.runtimeFiles                        = new FileCollectionAdapter(new APIRuntimeFileSet(this, inputs));
    this.exportedCompileAndLinkConfiguration = new JFExportedCompileAndLinkConfigurationImpl(this, inputs);
    this.linker      = new ToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.LinkterToolLocator(), false);
    this.cCompiler   = new PreprocessingToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.CCompilerToolLocator(), false);
    this.cppCompiler = new PreprocessingToolImpl(this, inputs,
      new JFExportedCompileAndLinkConfigurationImpl.CppCompilerToolLocator(), false);
    logger.debug("DefaultJFSharedLibraryBinarySpec() [DONE]");
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.JFNativeLibraryBinary}.

  // Compiler and linker configuration
  @Override
  public JFExportedCompileAndLinkConfiguration getExportedCompileAndLinkConfiguration() {
    logger.debug("getExportedCompileAndLinkConfiguration() [CALLED]");
    return this.exportedCompileAndLinkConfiguration;
  }

  // Implement interface of {@link org.gradle.nativeplatform.NativeLibraryBinary}.

  @Override
  public FileCollection getHeaderDirs() {
    logger.debug("getHeaderDirs() [CALLED]");
    return headerDirs;
  }

  @Override
  public FileCollection getLinkFiles() {
    logger.debug("getLinkFiles() [CALLED]");
    return this.linkFiles;
  }

  @Override
  public FileCollection getRuntimeFiles() {
    logger.debug("getRuntimeFiles() [CALLED]");
    return this.runtimeFiles;
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

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.internal.JFNativeLibraryBinarySpecInternal}.

  @Override
  public boolean                  hasOutputs() {
//  for (LanguageSourceSet languageSourceSet : inputInterfaceSets.withType(LanguageSourceSet.class)) {
//    if (!(languageSourceSet instanceof NativeResourceSet)) {
//      if (!languageSourceSet.getSource().isEmpty()) {
//        return true;
//      }
//    }
//  }
//  return false;
    return this.hasSources();
  }

  /// This must return the file used to link with this library
  @Override
  public File                     getLinkFile() {
    return this.getSharedLibraryLinkFile();
  }

  /// This must return a potential runtime file required to execute programs linked to the library.
  /// If not applicable, null must be returned.
  @Override
  public File                     getRuntimeFile() {
    return this.getSharedLibraryFile();
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.JFSharedLibraryBinarySpec}.

  @Override
  public String getFlummy()
    { return this.flummy; }

  @Override
  public void setFlummy(String flummy)
    { this.flummy = flummy; }

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
