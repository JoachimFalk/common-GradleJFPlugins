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
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;
import de.jfalk.gradle.nativeplatform.JFPrebuiltSharedLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;

import org.gradle.nativeplatform.NativeComponentSpec;
import org.gradle.nativeplatform.NativeDependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.model.ModelMap;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.platform.base.component.internal.DefaultComponentSpec;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.platform.NativePlatform;

public class DefaultJFPrebuiltSharedLibraryBinarySpec extends DefaultComponentSpec implements JFPrebuiltSharedLibraryBinarySpec {
  // Constants
  private static final ModelType<JFHeaderExportingDependentInterfaceSet>  INTERFACE_MODEL_TYPE = ModelType.of(JFHeaderExportingDependentInterfaceSet.class);

  private final Logger                    logger;
  private final JFCommonLibraryBinarySpec commonHelpers;
  private final MutableModelNode          interfaces;
  private final Set<? super Object>       libs = new LinkedHashSet<Object>();

  private final Flavor                    flavor;
  private final NativePlatform            platform;
  private final BuildType                 buildType;

  // Model configuration properties.
  private File libraryFile;
  private File dllFile;

  private   String                    flummy;
  protected NativeDependencyResolver  resolver;


  public DefaultJFPrebuiltSharedLibraryBinarySpec() {
    this.logger        = LoggerFactory.getLogger(this.getClass());
//  this.commonHelpers = new JFCommonLibraryBinarySpec(this);
    this.commonHelpers = null;
    MutableModelNode modelNode = getInfo().modelNode;
    interfaces = ModelMaps.addModelMapNode(modelNode, INTERFACE_MODEL_TYPE, "interfaces");
    this.flavor    = null;
    this.platform  = null;
    this.buildType = null;
  }

  // Implement interface of JFNativeLibraryBinary

  // Compiler and linker configuration
  @Override
  public JFExportedCompileAndLinkConfiguration getExportedCompileAndLinkConfiguration() {
    return null;
  }

  /// Implement interface of NativeLibraryBinary
  @Override
  public FileCollection getHeaderDirs() {
    return null;
//  return commonHelpers.extendHeaderDirs(super.getHeaderDirs(), this.resolver);
  }
  
  @Override
  public FileCollection getLinkFiles() {
    return null;
//  return commonHelpers.extendLinkFiles(super.getLinkFiles(), this.resolver);
  }

  @Override
  public FileCollection getRuntimeFiles() {
    return null;
//  FileCollection retval = super.getRuntimeFiles();
//  return retval;
  }

  /// Implement interface of {@link org.gradle.nativeplatform.NativeLibraryBinary}.

  /// The {@link org.gradle.nativeplatform.Flavor} that this binary was built with.
  @Override
  public Flavor getFlavor() { return flavor; }

  /// Returns the {@link org.gradle.nativeplatform.platform.NativePlatform} that this binary is targeted to run on.
  @Override
  public NativePlatform getTargetPlatform() { return platform; }

  /// Returns the {@link org.gradle.nativeplatform.BuildType} used to construct this binary.
  @Override
  public BuildType getBuildType() { return buildType; }

  /// Implement interface of {@link org.gradle.nativeplatform.SharedLibraryBinary}.

  /// The shared library file.
  @Override
  public File getSharedLibraryFile() {
    logger.debug("getSharedLibraryFile() [CALLED]");
    return dllFile;
  }

  /// The shared library link file.
  @Override
  public File getSharedLibraryLinkFile() {
    logger.debug("getSharedLibraryLinkFile() [CALLED]");
    return libraryFile;
  }

  /// The shared library file.
  @Override
  public void setSharedLibraryFile(final File dllFile) {
    logger.debug("setSharedLibraryFile() [CALLED]");
    this.dllFile = dllFile;
  }

  /// The shared library link file.
  @Override
  public void setSharedLibraryLinkFile(final File libraryFile) {
    logger.debug("getSharedLibraryLinkFile() [CALLED]");
    this.libraryFile = libraryFile;
  }

  /// Unfortunately, AbstractNativeBinarySpec.this.resolver is private and, thus, we
  /// have to store our own reference to the resolver.
  @Override
  public void setResolver(NativeDependencyResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void lib(Object notation) {
    libs.add(notation);
  }

  @Override
  public Collection<NativeDependencySet> getLibs() {
    return null;
  }

  @Override
  public ModelMap<JFHeaderExportingDependentInterfaceSet> getInterfaces() {
    return ModelMaps.toView(interfaces, INTERFACE_MODEL_TYPE);
  }

  @Override
  public NativeComponentSpec getComponent() {
    return null;
  }

  @Override
  public String getFlummy()
    { return this.flummy; }

  @Override
  public void setFlummy(String flummy)
    { this.flummy = flummy; }

}
