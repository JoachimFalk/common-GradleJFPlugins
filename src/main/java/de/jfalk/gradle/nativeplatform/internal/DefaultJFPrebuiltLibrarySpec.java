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

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraryBinarySpec;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;

import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.model.ModelMap;
import org.gradle.platform.base.BinarySpec;
import org.gradle.platform.base.component.internal.DefaultComponentSpec;

public class DefaultJFPrebuiltLibrarySpec extends DefaultComponentSpec implements JFPrebuiltLibrarySpec {
  // Constants
  private static final ModelType<JFPrebuiltLibraryBinarySpec>             BINARY_MODEL_TYPE    = ModelType.of(JFPrebuiltLibraryBinarySpec.class);
  private static final ModelType<JFHeaderExportingDependentInterfaceSet>  INTERFACE_MODEL_TYPE = ModelType.of(JFHeaderExportingDependentInterfaceSet.class);

  private final MutableModelNode binaries;
  private final MutableModelNode interfaces;

  public DefaultJFPrebuiltLibrarySpec() {
    MutableModelNode modelNode = getInfo().modelNode;
    binaries   = ModelMaps.addModelMapNode(modelNode, BINARY_MODEL_TYPE, "binaries");
    interfaces = ModelMaps.addModelMapNode(modelNode, INTERFACE_MODEL_TYPE, "interfaces");
  }

  @Override
  public ModelMap<JFPrebuiltLibraryBinarySpec> getBinaries() {
    return ModelMaps.toView(binaries, BINARY_MODEL_TYPE);
  }

  @Override
  public ModelMap<JFHeaderExportingDependentInterfaceSet> getInterfaces() {
    return ModelMaps.toView(interfaces, INTERFACE_MODEL_TYPE);
  }
}
