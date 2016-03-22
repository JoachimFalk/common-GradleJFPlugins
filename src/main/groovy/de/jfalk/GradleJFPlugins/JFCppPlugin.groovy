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

import javax.inject.Inject;
import org.gradle.model.Finalize;
import org.gradle.platform.base.ComponentType;
import org.gradle.platform.base.BinaryType;
import org.gradle.platform.base.TypeBuilder;

import org.gradle.api.Project;
import org.gradle.api.Plugin;

import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.internal.ProjectNativeLibraryRequirement;

import org.gradle.model.RuleSource;

import org.gradle.model.internal.registry.ModelRegistry;

import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.platform.base.PlatformContainer;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.BuildTypeContainer;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal;


import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface JFNativeLibrarySpec extends NativeLibrarySpec {
}

import org.gradle.nativeplatform.internal.AbstractTargetedNativeComponentSpec;

class DefaultJFNativeLibrarySpec extends AbstractTargetedNativeComponentSpec implements JFNativeLibrarySpec {

    public DefaultJFNativeLibrarySpec() {
      println "DefaultJFNativeLibrarySpec::DefaultJFNativeLibrarySpec() [CALLED]";
    }

    public String getDisplayName() {
        return String.format("DefaultJFNativeLibrarySpec native library '%s'", getName());
    }

    public NativeLibraryRequirement getShared() {
        return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "shared");
    }

    public NativeLibraryRequirement getStatic() {
        return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "static");
    }

    public NativeLibraryRequirement getApi() {
        return new ProjectNativeLibraryRequirement(getProjectPath(), this.getName(), "api");
    }

}

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
    @Finalize
    public void flummy(NativeToolChainRegistryInternal toolChains, FlavorContainer flavors, BuildTypeContainer buildTypes) {
//    println "toolChains@flummy: " + toolChains;
//    println "flavors@flummy: " + flavors;
//    println "buildTypes@flummy: " + buildTypes;
    }

//  @Defaults
//  void flummy(ServiceRegistry serviceRegistry, FlavorContainer flavors, PlatformContainer platforms, BuildTypeContainer buildTypes) {
//
//  }

    @ComponentType
    void nativeLibrary(TypeBuilder<JFNativeLibrarySpec> builder) {
      builder.defaultImplementation(DefaultJFNativeLibrarySpec.class);
    }
    
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
