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

package de.jfalk.gradle

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.jfalk.gradle.language.cpp.JFCppSourceSet;
import de.jfalk.gradle.nativeplatform.JFSharedLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.internal.DefaultJFSharedLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.JFStaticLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.internal.DefaultJFStaticLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.internal.resolve.DefaultJFNativeDependencyResolver;

import org.gradle.language.base.internal.LanguageSourceSetInternal;

import org.gradle.nativeplatform.internal.resolve.LibraryBinaryLocator;
import org.gradle.api.internal.file.FileCollectionFactory;

import org.gradle.language.base.LanguageSourceSet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Named;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.cpp.plugins.CppLangPlugin;
import org.gradle.model.Defaults;
import org.gradle.model.Each;
import org.gradle.model.Finalize;
import org.gradle.model.Managed;
import org.gradle.model.internal.core.ModelNode;
import org.gradle.model.internal.core.ModelNodes;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.core.NodeBackedModelMap;
import org.gradle.model.internal.registry.ModelRegistry;
//import org.gradle.model.internal.registry.ModelElementNode;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.model.Model;
import org.gradle.nativeplatform.NativeDependencySet;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.internal.configure.NativeComponentRules;
import org.gradle.nativeplatform.internal.configure.NativeBinaries;
import org.gradle.nativeplatform.internal.ProjectNativeLibraryRequirement;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.TargetedNativeComponentInternal;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.nativeplatform.NativeLibraryBinarySpec;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.nativeplatform.platform.internal.NativePlatforms;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.plugins.NativeComponentPlugin;
import org.gradle.nativeplatform.SharedLibraryBinarySpec;
import org.gradle.nativeplatform.StaticLibraryBinarySpec;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal;
import org.gradle.platform.base.BinaryContainer;
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

//trait JFNativeBinarySpecView implements NativeBinarySpec {
//  String internalData;
//
//  String getInternalData() {
//    return this.internalData;
//  }
//  
//  void setInternalData(String internal) {
//     this.internalData = internal;
//  }
//
//}

class JFCppPlugin implements Plugin<Project> {
  private final Logger                    logger;
  private final ModelRegistry             modelRegistry;
//private final Instantiator              instantiator;

  private FlavorContainer                 flavors;
  private NativeToolChainRegistryInternal toolChains;
  private BuildTypeContainer              buildTypes;
  private ServiceRegistry                 serviceRegistry;

  @Inject
  public JFCppPlugin(ModelRegistry modelRegistry, ServiceRegistry serviceRegistry, Instantiator instantiator) {
    this.logger           = LoggerFactory.getLogger(this.class);
    logger.debug("JFCppPlugin(...) [CALLED]")
    this.modelRegistry    = modelRegistry;
//  this.serviceRegistry  = serviceRegistry;
//  this.instantiator     = instantiator;
    logger.debug("JFCppPlugin(...) [DONE]")
  }

//Set<File> exportedHeadersOfLib(Object library) {
//  logger.debug("JFCppPlugin::exportedHeadersOfLib: " + library.getClass());
//  return new HashSet<File>();
//}

  @Override
  void apply(final Project project) {
    logger.debug("apply(...) [CALLED]")
    // This should create the extensions used below.
    project.getPluginManager().apply(NativeComponentPlugin.class);
    project.getPluginManager().apply(JFCppLangPlugin.class);
    // Get the extensions created by the previous plugins.
//  JFHelperFunctions.analysis("modelRegistry.getRoot()", modelRegistry.getRoot())
    this.flavors         = project.getExtensions().getByType(FlavorContainer.class);
    this.toolChains      = project.getExtensions().getByType(NativeToolChainRegistryInternal.class);
    this.buildTypes      = project.getExtensions().getByType(BuildTypeContainer.class);
    this.serviceRegistry = project.getServices();
//  PlatformContainer platforms = project.getExtensions().getByType(PlatformContainer.class);
    assert this.modelRegistry == project.getModelRegistry();
//  assert this.serviceRegistry == project.getServices(); this fails!

//  Instantiator instantiator = project.getServices().get(Instantiator.class);
//  JFHelperFunctions.analysis("instantiator", instantiator)

//  serviceRegistry.add(JFNativeDependencyResolver.class,
//    new JFNativeDependencyResolver(
//      serviceRegistry.get(LibraryBinaryLocator.class),
//      serviceRegistry.get(FileCollectionFactory.class)));

//  println "flavors@apply: "    + flavors;
//  println "toolChains@apply: " + toolChains;
//  println "buildTypes@apply: " + buildTypes;
    
//  project.task('hello') << {
//    println "Hello from the GreetingPlugin"
//  }

    // Fake imports in the project applying this plugin.
    project.ext.JFCppSourceSet               = JFCppSourceSet.class;
    project.ext.JFNativeLibrarySpec          = JFNativeLibrarySpec.class;
    project.ext.JFSharedLibraryBinarySpec    = JFSharedLibraryBinarySpec.class;
    project.ext.JFStaticLibraryBinarySpec    = JFStaticLibraryBinarySpec.class;
    project.ext.JFNativeExecutableSpec       = JFNativeExecutableSpec.class;
    project.ext.JFNativeExecutableBinarySpec = JFNativeExecutableBinarySpec.class;

    // new JFalkPrebuiltLibrary(...) does not work in a ```build.gradle'' file! Why?
    project.ext.JFalkPrebuiltLibrary = JFalkPrebuiltLibrary.class; 

//  project.ext.exportedHeadersOfLib = this.&exportedHeadersOfLib;
    logger.debug("apply(...) [DONE]")
  }

  static class Rules extends RuleSource {

    private static final Logger logger = LoggerFactory.getLogger(Rules.class);

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

    @ComponentType
    void nativeExecutable(TypeBuilder<JFNativeExecutableSpec> builder) {
      builder.defaultImplementation(DefaultJFNativeExecutableSpec.class);
    }

    @ComponentType
    void executableBinary(TypeBuilder<JFNativeExecutableBinarySpec> builder) {
      builder.defaultImplementation(DefaultJFNativeExecutableBinarySpec.class);
    }

//  @ComponentType
//  void nativeBinary(TypeBuilder<NativeBinarySpec> builder) {
//    builder.internalView(JFNativeBinarySpecView.class);
//  }

    @Model
    NativeDependencyResolver createNativeDependencyResolver(ServiceRegistry serviceRegistry) {
      new DefaultJFNativeDependencyResolver(
        serviceRegistry.get(LibraryBinaryLocator.class),
        serviceRegistry.get(FileCollectionFactory.class));
    }

    @ComponentBinaries
    void createBinariesForJFNativeLibrarySpec(
        ModelMap<NativeLibraryBinarySpec> binaries, // Create this
        JFNativeLibrarySpec               nativeComponent, // From this input
        // Via usage of the following factories and stuff.
        PlatformResolvers                 platforms,
        BuildTypeContainer                buildTypes,
        FlavorContainer                   flavors,
        NativeDependencyResolver          nativeDependencyResolver,
        ServiceRegistry                   serviceRegistry
    ) {
      logger.debug("createBinariesForJFNativeLibrarySpec(...) for " + nativeComponent + " [CALLED]");
//    binaries.create("${component.name}Binary", SampleBinary)
//    JFHelperFunctions.analysis("nativeComponent", nativeComponent);
//    JFHelperFunctions.analysis("nativeComponent.getBackingNode(): ", nativeComponent.getBackingNode());
//    JFHelperFunctions.analysis("binaries", binaries);
//    JFHelperFunctions.analysis("binaries.getBackingNode()", binaries.getBackingNode());

//    L1:{
//      println nativeComponent.getBackingNode();
//      Set<String> links = new HashSet<String>(nativeComponent.getBackingNode().getLinkNames(ModelNodes.all()));
//      for (String link : links) {
//        println "  ZZZZ: " + link;
//      }
//    }
//    L2:{
//      NodeBackedModelMap<NativeLibraryBinarySpec> bins = (NodeBackedModelMap<NativeLibraryBinarySpec>) binaries;
//
////    for (MutableModelNode node : bins.getBackingNode().getLinks(ModelNodes.all())) {
////      node.modelRegistry.transition(node, ModelNode.State.Created, true);
////      if (node.getPrivateData() instanceof SharedLibraryBinarySpec) {
////        JFSharedLibraryBinarySpec.create(JFSharedLibraryBinarySpec.class, DefaultJFSharedLibraryBinarySpec.class, node.getPrivateData().getIdentifier(), node);
//////      node.setPrivateData(new DefaultJFSharedLibraryBinarySpec());
////      } else if (node.getPrivateData() instanceof StaticLibraryBinarySpec) {
////        JFStaticLibraryBinarySpec.create(JFStaticLibraryBinarySpec.class, DefaultJFStaticLibraryBinarySpec.class, node.getPrivateData().getIdentifier(), node);
//////      node.setPrivateData(new DefaultJFStaticLibraryBinarySpec());
////      } else {
////        assert "Oops, unknown node type" + node.getPrivateData().getClass();
////      }
//////    JFHelperFunctions.analysis("node.getPrivateData()", node.getPrivateData());
////    }
//      for (String link : bins.getBackingNode().getLinkNames(ModelNodes.all())) {
//        bins.getBackingNode().removeLink(link);
//        logger.debug("Removing model element '" + bins.getBackingNode().getPath().child(link)+"'");
//      }
//    }
      nativeComponent.getBackingNode().getPrivateData().enableFlavorsAndBuildTypes = true;

      NativePlatforms nativePlatforms = serviceRegistry.get(NativePlatforms.class);
//    NativeDependencyResolver nativeDependencyResolver = serviceRegistry.get(NativeDependencyResolver.class);
      FileCollectionFactory fileCollectionFactory = serviceRegistry.get(FileCollectionFactory.class);
      List<NativePlatform> resolvedPlatforms = NativeComponentRules.resolvePlatforms(nativeComponent, nativePlatforms, platforms);

      for (NativePlatform platform : resolvedPlatforms) {
        BinaryNamingScheme namingScheme = DefaultBinaryNamingScheme.component(nativeComponent.getName());
        namingScheme = namingScheme.withVariantDimension(platform, resolvedPlatforms);
        Set<BuildType> targetBuildTypes = nativeComponent.chooseBuildTypes(buildTypes);
        for (BuildType buildType : targetBuildTypes) {
          BinaryNamingScheme namingSchemeWithBuildType = namingScheme.withVariantDimension(buildType, targetBuildTypes);
          Set<Flavor> targetFlavors = nativeComponent.chooseFlavors(flavors);
          for (Flavor flavor : targetFlavors) {
            BinaryNamingScheme namingSchemeWithFlavor = namingSchemeWithBuildType.withVariantDimension(flavor, targetFlavors);
            NativeBinaries.createNativeBinary(
              JFSharedLibraryBinarySpec.class, binaries, nativeDependencyResolver, fileCollectionFactory,
              namingSchemeWithFlavor.withBinaryType("SharedLibrary").withRole("shared", false),
              platform, buildType, flavor);
            NativeBinaries.createNativeBinary(
              JFStaticLibraryBinarySpec.class, binaries, nativeDependencyResolver, fileCollectionFactory,
              namingSchemeWithFlavor.withBinaryType("StaticLibrary").withRole("static", false),
              platform, buildType, flavor);
          }
        }
      }
      logger.debug("createBinariesForJFNativeLibrarySpec(...) for " + nativeComponent + " [DONE]");
    }

    @ComponentBinaries
    void createBinariesForJFNativeExecutableSpec(
        ModelMap<NativeExecutableBinarySpec> binaries, // Create this
        JFNativeExecutableSpec               nativeComponent, // From this input
        // Via usage of the following factories and stuff.
        PlatformResolvers                    platforms,
        BuildTypeContainer                   buildTypes,
        FlavorContainer                      flavors,
        NativeDependencyResolver             nativeDependencyResolver,
        ServiceRegistry                      serviceRegistry
    ) {
      logger.debug("createBinariesForJFNativeExecutableSpec(...) for " + nativeComponent + " [CALLED]");
      nativeComponent.getBackingNode().getPrivateData().enableFlavorsAndBuildTypes = true;

      NativePlatforms nativePlatforms = serviceRegistry.get(NativePlatforms.class);
//    NativeDependencyResolver nativeDependencyResolver = serviceRegistry.get(NativeDependencyResolver.class);
      FileCollectionFactory fileCollectionFactory = serviceRegistry.get(FileCollectionFactory.class);
      List<NativePlatform> resolvedPlatforms = NativeComponentRules.resolvePlatforms(nativeComponent, nativePlatforms, platforms);

      for (NativePlatform platform : resolvedPlatforms) {
        BinaryNamingScheme namingScheme = DefaultBinaryNamingScheme.component(nativeComponent.getName());
        namingScheme = namingScheme.withVariantDimension(platform, resolvedPlatforms);
        Set<BuildType> targetBuildTypes = nativeComponent.chooseBuildTypes(buildTypes);
        for (BuildType buildType : targetBuildTypes) {
          BinaryNamingScheme namingSchemeWithBuildType = namingScheme.withVariantDimension(buildType, targetBuildTypes);
          Set<Flavor> targetFlavors = nativeComponent.chooseFlavors(flavors);
          for (Flavor flavor : targetFlavors) {
            BinaryNamingScheme namingSchemeWithFlavor = namingSchemeWithBuildType.withVariantDimension(flavor, targetFlavors);
            NativeBinaries.createNativeBinary(
              JFNativeExecutableBinarySpec.class, binaries, nativeDependencyResolver, fileCollectionFactory,
              namingSchemeWithFlavor.withBinaryType("Executable").withRole("executable", true),
              platform, buildType, flavor);
          }
        }
      }
      logger.debug("createBinariesForJFNativeExecutableSpec(...) for " + nativeComponent + " [DONE]");
    }

    @Finalize
    void dumpNativeBinaryInputs(@Each final NativeBinarySpec nativeBinarySpec) {
      for (Object input : nativeBinarySpec.getInputs()) {
        logger.debug("dumpNativeBinaryInputs: " + nativeBinarySpec + " has input " + input);
      }
    }

//  @Defaults
//  void defaultsForJFNativeLibrarySpec(@Each final JFNativeLibrarySpec nativeComponent) {
//    logger.debug("defaultsForJFNativeLibrarySpec(...) for " + nativeComponent + " [CALLED]");
//    for (NativeLibraryBinarySpec lib : nativeComponent.getBinaries()) {
//      println "defaultsForJFNativeLibrarySpec:   " + lib;
//    }
//    logger.debug("defaultsForJFNativeLibrarySpec(...) for " + nativeComponent + " [DONE]");
//  }

    @Finalize
    void finalizeForLanguageSourceSet(@Each final LanguageSourceSet langSourceSet) {
      logger.debug("finalizeForLanguageSourceSet(...) for " + langSourceSet + " [CALLED]");
      langSourceSet.source.exclude "**/*.sw*"
      langSourceSet.source.exclude "**/*~"
      langSourceSet.source.exclude "**/*.bak"
      logger.debug("finalizeForLanguageSourceSet(...) for " + langSourceSet + " [DONE]");
    }

  }

}
