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
import de.jfalk.gradle.nativeplatform.JFPrebuiltStaticLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.internal.file.collections.FileCollectionAdapter;
import org.gradle.api.internal.file.collections.SimpleFileCollection;
import org.gradle.api.Nullable;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.model.ModelMap;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.NativeComponentSpec;
import org.gradle.nativeplatform.NativeDependencySet;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.nativeplatform.Tool;
import org.gradle.nativeplatform.toolchain.NativeToolChain;
import org.gradle.platform.base.BinaryTasksCollection;
import org.gradle.platform.base.component.internal.DefaultComponentSpec;
import org.gradle.platform.base.ComponentSpec;

public class DefaultJFPrebuiltStaticLibraryBinarySpec extends DefaultComponentSpec implements JFPrebuiltStaticLibraryBinarySpec, JFPrebuiltLibraryBinaryInternal, JFNativeLibraryBinarySpecInternal {
  // Constants
  private static final ModelType<JFHeaderExportingDependentInterfaceSet>  INTERFACE_MODEL_TYPE = ModelType.of(JFHeaderExportingDependentInterfaceSet.class);

  private final Logger                    logger;
  private final MutableModelNode          modelNode;
  private final MutableModelNode          interfaces;
  private final Set<? super Object>       libs = new LinkedHashSet<Object>();

  private final DomainObjectSet<JFHeaderExportingDependentInterfaceSet> inputInterfaceSets =
    new DefaultDomainObjectSet<JFHeaderExportingDependentInterfaceSet>(JFHeaderExportingDependentInterfaceSet.class);

  private final FileCollection                        headerDirs;
  private final FileCollection                        linkFiles;
  private final FileCollection                        runtimeFiles;
  private final JFExportedCompileAndLinkConfiguration exportedCompileAndLinkConfiguration;

  // Injected internal stuff
  private NativeDependencyResolver    resolver;

  // Model configuration properties.
  private Flavor                      flavor;
  private NativePlatform              platform;
  private BuildType                   buildType;
  private File                        staticLibraryFile;
  
  public DefaultJFPrebuiltStaticLibraryBinarySpec() {
    @SuppressWarnings("unchecked")
    DomainObjectSet<ComponentSpec> inputs = (DomainObjectSet) inputInterfaceSets;
    this.logger                              = LoggerFactory.getLogger(this.getClass());
    this.modelNode                           = getInfo().modelNode;
    this.interfaces                          = ModelMaps.addModelMapNode(modelNode, INTERFACE_MODEL_TYPE, "interfaces");
    this.headerDirs                          = new FileCollectionAdapter(new APIHeadersFileSet(this, inputs));
    this.linkFiles                           = new FileCollectionAdapter(new APILinkFileSet(this, inputs));
    this.runtimeFiles                        = new FileCollectionAdapter(new APIRuntimeFileSet(this, inputs));
    this.exportedCompileAndLinkConfiguration = new JFExportedCompileAndLinkConfigurationImpl(this, inputs);
    this.flavor                              = null;
    this.platform                            = null;
    this.buildType                           = null;
    this.staticLibraryFile                   = null;
  }

  /// Returns a human-consumable display name for this binary.
  @Override
  public String getDisplayName() {
    return super.getDisplayName()+":"+getTargetPlatform()+":"+getFlavor()+":"+getBuildType();
  }

  // Implement interface of {@link org.gradle.nativeplatform.StaticLibraryBinary}.

  /// The static library file. 
  @Override
  public File getStaticLibraryFile() {
    logger.debug("getStaticLibraryFile() [CALLED] => " + staticLibraryFile);
    return this.staticLibraryFile;
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.JFPrebuiltStaticLibraryBinarySpec}.

  @Override
  public void setStaticLibraryFile(final File staticLibraryFile) {
    logger.debug("setStaticLibraryFile(" + staticLibraryFile + ") [CALLED]");
    this.staticLibraryFile = staticLibraryFile;
  }

  /// Implement interface of {@link de.jfalk.gradle.nativeplatform.JFNativeLibraryBinary}.

  /// Compiler and linker configuration
  @Override
  public JFExportedCompileAndLinkConfiguration getExportedCompileAndLinkConfiguration() {
    return this.exportedCompileAndLinkConfiguration;
  }

  // Implement interface of {@link org.gradle.nativeplatform.NativeLibraryBinary}.

  @Override
  public FileCollection getHeaderDirs() {
    logger.debug("getHeaderDirs() [CALLED]");
    return this.headerDirs;
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

  // Implement interface of {@link org.gradle.nativeplatform.NativeBinary}.

  /// The {@link org.gradle.nativeplatform.Flavor} that this binary was built with.
  @Override
  public Flavor getFlavor() { return flavor; }

  /// Returns the {@link org.gradle.nativeplatform.platform.NativePlatform} that this binary is targeted to run on.
  @Override
  public NativePlatform getTargetPlatform() { return platform; }

  /// Returns the {@link org.gradle.nativeplatform.BuildType} used to construct this binary.
  @Override
  public BuildType getBuildType() { return buildType; }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.internal.JFPrebuiltLibraryBinaryInternal}.

  @Override
  public void setFlavor(Flavor flavor) {
    this.flavor = flavor;
  }

  @Override
  public void setTargetPlatform(NativePlatform nativePlatform) {
    this.platform = nativePlatform;
  }

  @Override
  public void setBuildType(BuildType buildType) {
    this.buildType = buildType;
  }

  @Override
  public void setResolver(NativeDependencyResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public DomainObjectSet<JFHeaderExportingDependentInterfaceSet> getInterfaceSets() {
    return this.inputInterfaceSets;
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.JFPrebuiltLibraryBinarySpec}.

  @Override @Nullable
  public JFPrebuiltLibrarySpec getComponent() {
    MutableModelNode componentNode = modelNode.getParent();
    // componentNode should now point to the model node of ModelMap<JFPrebuiltLibraryBinarySpec> 
    componentNode = componentNode != null
      ? componentNode.getParent()
      : null;
    // componentNode should now point to the model node of a JFPrebuiltLibrarySpec.
    logger.debug("getComponent() [CALLED] => " + componentNode);
    return componentNode != null && componentNode.canBeViewedAs(ModelType.of(JFPrebuiltLibrarySpec.class))
      ? componentNode.asImmutable(ModelType.of(JFPrebuiltLibrarySpec.class), componentNode.getDescriptor()).getInstance()
      : null;
  }

  @Override
  public void lib(Object notation) {
    libs.add(notation);
  }

  @Override
  public ModelMap<JFHeaderExportingDependentInterfaceSet> getInterfaces() {
    return ModelMaps.toView(interfaces, INTERFACE_MODEL_TYPE);
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.internal.JFNativeBinarySpecInternal}.

  @Override
  public NativeDependencyResolver getResolver() {
    return this.resolver;
  }

  // Implement interface of {@link de.jfalk.gradle.nativeplatform.internal.JFNativeLibraryBinarySpecInternal}.

  @Override
  public boolean                  hasOutputs() {
    return this.getStaticLibraryFile() != null;
  }

  /// This must return the file used to link with this library
  @Override
  public File                     getLinkFile() {
    return this.getStaticLibraryFile();
  }

  /// This must return a potential runtime file required to execute programs linked to the library.
  /// If not applicable, null must be returned.
  @Override
  public File                     getRuntimeFile() {
    return null;
  }

//// Flummy

  @Override
  public Collection<NativeDependencySet> getLibs() {
    return null;
  }

  @Override
  public DomainObjectSet<LanguageSourceSet> getInputs() {
    return null;
  }

  @Override
  public NativeToolChain getToolChain()
    { return null; }

  @Override
  public Tool getLinker()
    { return null; }

  @Override
  public Tool getStaticLibArchiver()
    { return null; }

  @Override
  public Tool getAssembler()
    { return null; }

  @Override
  public PreprocessingTool getcCompiler()
    { return null; }

  @Override
  public PreprocessingTool getCppCompiler()
    { return null; }

  @Override
  public PreprocessingTool getObjcCompiler()
    { return null; }

  @Override
  public PreprocessingTool getObjcppCompiler()
    { return null; }

  @Override
  public PreprocessingTool getRcCompiler()
    { return null; }

  @Override
  public boolean isBuildable()
    { return true; }

  @Override
  public ModelMap<LanguageSourceSet> getSources()
    { return null; }

  @Override
  public BinaryTasksCollection getTasks()
    { return null; }

  @Nullable @Override
  public Task getBuildTask()
    { return null; }

  @Override
  public void setBuildTask(Task buildTask)
    {}

  @Override
  public void builtBy(Object... tasks)
    {}

  @Override
  public boolean hasBuildDependencies()
    { return false; }

  @Override
  public TaskDependency getBuildDependencies()
    { return null; }



}
