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

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrariesSpec;

import org.gradle.model.ModelMap;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.type.ModelType;

import org.gradle.platform.base.component.internal.DefaultComponentSpec;

public class DefaultJFPrebuiltLibrariesSpec extends DefaultComponentSpec implements JFPrebuiltLibrariesSpec {

  private static final ModelType<JFPrebuiltLibrarySpec> JFPREBUILT_LIBRARY_SPEC_MODEL_TYPE = ModelType.of(JFPrebuiltLibrarySpec.class);
  private final MutableModelNode prebuiltLibs;

  public DefaultJFPrebuiltLibrariesSpec() {
    MutableModelNode modelNode = getInfo().modelNode;
    prebuiltLibs = ModelMaps.addModelMapNode(modelNode, JFPREBUILT_LIBRARY_SPEC_MODEL_TYPE, "prebuiltLibs");
  }

  @Override
  public ModelMap<JFPrebuiltLibrarySpec> getPrebuiltLibs() {
    return ModelMaps.toView(prebuiltLibs, JFPREBUILT_LIBRARY_SPEC_MODEL_TYPE);
  }

}
