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

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Named;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.model.Defaults;
import org.gradle.model.Each;
import org.gradle.model.Finalize;
import org.gradle.model.internal.core.ModelNodes;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.core.NodeBackedModelMap;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.internal.configure.NativeComponentRules;
import org.gradle.nativeplatform.internal.configure.NativeBinaries;
import org.gradle.nativeplatform.internal.ProjectNativeLibraryRequirement;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.TargetedNativeComponentInternal;
import org.gradle.nativeplatform.NativeLibraryBinarySpec;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.nativeplatform.platform.internal.NativePlatforms;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.SharedLibraryBinarySpec;
import org.gradle.nativeplatform.StaticLibraryBinarySpec;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal;
import org.gradle.platform.base.BinaryType;
import org.gradle.platform.base.ComponentBinaries;
import org.gradle.platform.base.ComponentSpec;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.ComponentType;
import org.gradle.platform.base.PlatformContainer;
import org.gradle.platform.base.TypeBuilder;
import org.gradle.platform.base.internal.BinaryNamingScheme;
import org.gradle.platform.base.internal.PlatformRequirement;
import org.gradle.platform.base.internal.PlatformResolvers;
import org.gradle.platform.base.internal.DefaultBinaryNamingScheme;

class JFCppPlugin implements Plugin<Project> {
  private final Logger          logger;
  private final ModelRegistry   modelRegistry;
  private final ServiceRegistry serviceRegistry;
  private final Instantiator    instantiator;

  private FlavorContainer                 flavors;
  private NativeToolChainRegistryInternal toolChains;
  private BuildTypeContainer              buildTypes;

  @Inject
  public JFCppPlugin(ModelRegistry modelRegistry, ServiceRegistry serviceRegistry, Instantiator instantiator) {
    this.logger           = LoggerFactory.getLogger(this.class);
    logger.debug("JFCppPlugin::JFCppPlugin(ModelRegistry modelRegistry, ServiceRegistry serviceRegistry, Instantiator instantiator) [CALLED]")
    this.modelRegistry    = modelRegistry;
    this.serviceRegistry  = serviceRegistry;
    this.instantiator     = instantiator;
  }

  void apply(Project project) {
    logger.debug("JFCppPlugin::apply(Project project) [CALLED]")
//  project.analysis("modelRegistry.getRoot()", modelRegistry.getRoot())
    this.flavors    = project.getExtensions().getByType(FlavorContainer.class);
    this.toolChains = project.getExtensions().getByType(NativeToolChainRegistryInternal.class);
    this.buildTypes = project.getExtensions().getByType(BuildTypeContainer.class);
    assert this.modelRegistry == project.getModelRegistry();
//  assert this.serviceRegistry == project.getServices(); this fails!

//  PlatformContainer platforms = project.getExtensions().getByType(PlatformContainer.class);

//  println "flavors@apply: "    + flavors;
//  println "toolChains@apply: " + toolChains;
//  println "buildTypes@apply: " + buildTypes;
    
//  project.task('hello') << {
//    println "Hello from the GreetingPlugin"
//  }

    // Fake imports in the project applying this plugin.
    project.ext.JFNativeLibrarySpec  = JFNativeLibrarySpec.class;
    // new JFalkPrebuiltLibrary(...) does not work in the build.grald! Why?
    project.ext.JFalkPrebuiltLibrary = JFalkPrebuiltLibrary.class; 
  }

  static class Rules extends RuleSource {
    @ComponentType
    void nativeLibrary(TypeBuilder<JFNativeLibrarySpec> builder) {
      builder.defaultImplementation(DefaultJFNativeLibrarySpec.class);
    }

    @ComponentType
    void sharedLibraryBinary(TypeBuilder<JFSharedLibraryBinarySpec> builder) {
      builder.defaultImplementation(DefaultJFSharedLibraryBinarySpec.class);
    }

    @ComponentType
    void staticLibraryBinary(TypeBuilder<JFStaticLibraryBinarySpec> builder) {
      builder.defaultImplementation(DefaultJFStaticLibraryBinarySpec.class);
    }

    @ComponentBinaries
    void createBinariesForJFNativeLibrarySpec(
        ModelMap<NativeLibraryBinarySpec> binaries, // Create this
        JFNativeLibrarySpec               nativeComponent, // From this input
        // Via usage of the following factories and stuff.
        PlatformResolvers                 platforms,
        BuildTypeContainer                buildTypes,
        FlavorContainer                   flavors,
        ServiceRegistry                   serviceRegistry
    ) {
//    binaries.create("${component.name}Binary", SampleBinary)
      println "createBinariesForJFNativeLibrarySpec: " + nativeComponent;
//    JFHelperFunctions.analysis("binaries", binaries);

      NodeBackedModelMap<NativeLibraryBinarySpec> bins = (NodeBackedModelMap<NativeLibraryBinarySpec>) binaries;
      Set<String> links = new HashSet<String>(bins.getBackingNode().getLinkNames(ModelNodes.all()));
      for (String link : links) {
        bins.getBackingNode().removeLink(link);
//      println "YYYY: " + link;
      }
      NativePlatforms nativePlatforms = serviceRegistry.get(NativePlatforms.class);
      NativeDependencyResolver nativeDependencyResolver = serviceRegistry.get(NativeDependencyResolver.class);
      FileCollectionFactory fileCollectionFactory = serviceRegistry.get(FileCollectionFactory.class);
//    NativeComponentRules.createBinariesImpl(nativeComponent, platforms, buildTypes, flavors, nativePlatforms, nativeDependencyResolver, fileCollectionFactory);
      List<NativePlatform> resolvedPlatforms = NativeComponentRules.resolvePlatforms(nativeComponent, nativePlatforms, platforms);

      for (NativePlatform platform : resolvedPlatforms) {
        BinaryNamingScheme namingScheme = DefaultBinaryNamingScheme.component(nativeComponent.getName());
        namingScheme = namingScheme.withVariantDimension(platform, resolvedPlatforms);
        Set<BuildType> targetBuildTypes = nativeComponent.chooseBuildTypes(buildTypes);
        for (BuildType buildType : targetBuildTypes) {
          BinaryNamingScheme namingSchemeWithBuildType = namingScheme.withVariantDimension(buildType, targetBuildTypes);
          Set<Flavor> targetFlavors = nativeComponent.chooseFlavors(flavors);
          for (Flavor flavor : targetFlavors) {
            BinaryNamingScheme namingSchemeWithFlavor = namingScheme.withVariantDimension(flavor, targetFlavors);
            NativeBinaries.createNativeBinary(
              JFSharedLibraryBinarySpec.class, binaries, nativeDependencyResolver, fileCollectionFactory,
              namingScheme.withBinaryType("SharedLibrary").withRole("shared", false),
              platform, buildType, flavor);
            NativeBinaries.createNativeBinary(
              JFStaticLibraryBinarySpec.class, binaries, nativeDependencyResolver, fileCollectionFactory,
              namingScheme.withBinaryType("StaticLibrary").withRole("static", false),
              platform, buildType, flavor);
          }
        }
      }
    }

//  @Finalize
//  public void flummy(NativeToolChainRegistryInternal toolChains, FlavorContainer flavors, BuildTypeContainer buildTypes) {
//    println "toolChains@flummy: " + toolChains;
//    println "flavors@flummy: " + flavors;
//    println "buildTypes@flummy: " + buildTypes;
//  }

//  @Defaults
//  void flummy(ServiceRegistry serviceRegistry, FlavorContainer flavors, PlatformContainer platforms, BuildTypeContainer buildTypes) {
//
//  }

//  @Defaults
//  void flammy(@Each final JFNativeLibrarySpec component) {
//    println "flammy: " + component;
//    for (NativeLibraryBinarySpec lib : component.getBinaries()) {
//      println "flammy:   " + lib;
//    }
//  }

//  @Finalize
//  void flimmy(@Each final JFNativeLibrarySpec component) {
//    println "flimmy: " + component;
//    for (NativeLibraryBinarySpec lib : component.getBinaries()) {
//      println "flimmy:   " + lib;
//    }
//  }

//  @Mutate
//  void flummy(ComponentSpecContainer components) {
//    println "========================= flummy ========================="
//    for (ComponentSpec component: components) {
//      println "flummy: " + component;
//    }
//    println "========================= flummy ========================="
//  }
    
//  @ComponentType
//  void register(TypeBuilder<ComponentWithValue> builder) {
//    println "register(ComponentTypeBuilder<ComponentWithValue> builder) [CALLED]"
//  }
//  @BinaryType
//  void register(BinaryTypeBuilder<BinaryWithValue> builder) {
//    println "register(BinaryTypeBuilder<BinaryWithValue> builder) [CALLED]"
//  }
//    
//  @ComponentBinaries
//  void createBinaries(ModelMap<BinaryWithValue> binaries, ComponentWithValue component) {
//    println "createBinaries(ModelMap<BinaryWithValue> binaries, ComponentWithValue component) [CALLED]"
//    assert component.valueForBinary == "configured-value"
//    binaries.create("myBinary") {
//      assert component.valueForBinary == "configured-value"
//      valueFromComponent = component.valueForBinary
//    }
//  }
  }

}
