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

package de.jfalk.gradle.language.cpp.plugins;

import java.util.Map;
import java.util.HashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.language.cpp.JFCppSourceSet;
import de.jfalk.gradle.language.cpp.internal.DefaultJFCppSourceSet;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.base.internal.SourceTransformTaskConfig;
import org.gradle.language.base.internal.registry.LanguageTransformContainer;
import org.gradle.language.base.plugins.ComponentModelBasePlugin;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.language.cpp.tasks.CppPreCompiledHeaderCompile;
import org.gradle.language.nativeplatform.internal.DependentSourceSetInternal;
import org.gradle.language.nativeplatform.internal.NativeLanguageTransform;
import org.gradle.language.nativeplatform.internal.PCHCompileTaskConfig;
import org.gradle.language.nativeplatform.internal.SourceCompileTaskConfig;
import org.gradle.model.Defaults;
import org.gradle.model.Each;
import org.gradle.model.Finalize;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.nativeplatform.internal.DefaultPreprocessingTool;
import org.gradle.nativeplatform.internal.pch.PchEnabledLanguageTransform;
import org.gradle.platform.base.ComponentType;
import org.gradle.platform.base.TypeBuilder;

public class JFCppLangPlugin implements Plugin<Project> {
  private final Logger          logger;
//private final ModelRegistry   modelRegistry;
//private final ServiceRegistry serviceRegistry;
//private final Instantiator    instantiator;

  @Inject
  public JFCppLangPlugin(ModelRegistry modelRegistry, ServiceRegistry serviceRegistry, Instantiator instantiator) {
    this.logger           = LoggerFactory.getLogger(this.getClass());
    logger.debug("JFCppLangPlugin(...) [CALLED]");
//  this.modelRegistry    = modelRegistry;
//  this.serviceRegistry  = serviceRegistry;
//  this.instantiator     = instantiator;
    logger.debug("JFCppLangPlugin(...) [DONE]");
  }

  @Override
  public void apply(final Project project) {
    logger.debug("apply(...) [CALLED]");
    project.getPluginManager().apply(ComponentModelBasePlugin.class);
    logger.debug("apply(...) [DONE]");
  }

  static class Rules extends RuleSource {
    @ComponentType
    void registerLanguage(TypeBuilder<JFCppSourceSet> builder) {
      builder.defaultImplementation(DefaultJFCppSourceSet.class);
      builder.internalView(DependentSourceSetInternal.class);
    }

    @Mutate
    void registerLanguageTransform(LanguageTransformContainer languages) {
      languages.add(new JFCpp());
    }
  }

  private static class JFCpp extends NativeLanguageTransform<JFCppSourceSet> implements PchEnabledLanguageTransform<JFCppSourceSet> {
    @Override
    public Class<JFCppSourceSet> getSourceSetType() {
      return JFCppSourceSet.class;
    }

    @Override
    public Map<String, Class<?> > getBinaryTools() {
      Map<String, Class<?> > retval = new HashMap<String, Class<?> >();
      retval.put("cppCompiler", DefaultPreprocessingTool.class);
      return retval;
    }

    @Override
    public String getLanguageName() {
      return "cpp";
    }

    @Override
    public SourceTransformTaskConfig getTransformTask() {
      return new SourceCompileTaskConfig(this, CppCompile.class);
    }

    @Override
    public SourceTransformTaskConfig getPchTransformTask() {
      return new PCHCompileTaskConfig(this, CppPreCompiledHeaderCompile.class);
    }
  }
}
