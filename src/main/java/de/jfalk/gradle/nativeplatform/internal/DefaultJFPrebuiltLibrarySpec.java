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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFPrebuiltLibraryBinarySpec;

import org.gradle.api.Named;
import org.gradle.api.InvalidUserDataException;
import org.gradle.model.internal.core.ModelMaps;
import org.gradle.model.internal.core.MutableModelNode;
import org.gradle.model.internal.type.ModelType;
import org.gradle.model.ModelMap;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.platform.base.BinarySpec;
import org.gradle.platform.base.component.internal.DefaultComponentSpec;
import org.gradle.platform.base.internal.DefaultPlatformRequirement;
import org.gradle.platform.base.internal.PlatformRequirement;
import org.gradle.platform.base.internal.HasIntermediateOutputsComponentSpec;
import org.gradle.util.GUtil;
import org.gradle.platform.base.TransformationFileType;
import org.gradle.language.base.LanguageSourceSet;

public class DefaultJFPrebuiltLibrarySpec extends DefaultComponentSpec implements JFPrebuiltLibraryInternal, HasIntermediateOutputsComponentSpec {
  // Constants
  private static final ModelType<JFPrebuiltLibraryBinarySpec>             BINARY_MODEL_TYPE    = ModelType.of(JFPrebuiltLibraryBinarySpec.class);
  private static final ModelType<JFHeaderExportingDependentInterfaceSet>  INTERFACE_MODEL_TYPE = ModelType.of(JFHeaderExportingDependentInterfaceSet.class);
  private static final ModelType<LanguageSourceSet>                       SOURCE_MODEL_TYPE    = ModelType.of(LanguageSourceSet.class);

  private final List<PlatformRequirement> targetPlatforms = new ArrayList<PlatformRequirement>();
  private final Set<String>               buildTypes      = new HashSet<String>();
  private final Set<String>               flavors         = new HashSet<String>();

  private final MutableModelNode binaries;
  private final MutableModelNode interfaces;
  private final MutableModelNode sources; // This should be a dummy node which represents an empty map!

  private String baseName;

  public DefaultJFPrebuiltLibrarySpec() {
    MutableModelNode modelNode = getInfo().modelNode;
    this.binaries   = ModelMaps.addModelMapNode(modelNode, BINARY_MODEL_TYPE, "binaries");
    this.interfaces = ModelMaps.addModelMapNode(modelNode, INTERFACE_MODEL_TYPE, "interfaces");
    this.sources    = ModelMaps.addModelMapNode(modelNode, SOURCE_MODEL_TYPE, "sources");
  }

  @Override
  public ModelMap<JFPrebuiltLibraryBinarySpec> getBinaries() {
    return ModelMaps.toView(binaries, BINARY_MODEL_TYPE);
  }

  @Override
  public ModelMap<JFHeaderExportingDependentInterfaceSet> getInterfaces() {
    return ModelMaps.toView(interfaces, INTERFACE_MODEL_TYPE);
  }

  public List<PlatformRequirement> getTargetPlatforms() {
    return Collections.unmodifiableList(targetPlatforms);
  }

  @Override
  public void targetPlatform(String targetPlatform) {
    this.targetPlatforms.add(DefaultPlatformRequirement.create(targetPlatform));
  }

  @Override
  public void targetFlavors(String... flavorSelectors) {
    Collections.addAll(flavors, flavorSelectors);
  }

  @Override
  public void targetBuildTypes(String... buildTypeSelectors) {
    Collections.addAll(buildTypes, buildTypeSelectors);
  }

  @Override
  public Set<Flavor> chooseFlavors(Set<? extends Flavor> candidates) {
    return chooseElements(Flavor.class, candidates, flavors);
  }

  @Override
  public Set<BuildType> chooseBuildTypes(Set<? extends BuildType> candidates) {
    return chooseElements(BuildType.class, candidates, buildTypes);
  }

  protected <T extends Named> Set<T> chooseElements(Class<T> type, Set<? extends T> candidates, Set<String> names) {
    if (names.isEmpty()) {
      return new HashSet<T>(candidates);
    }
    Set<String> unusedNames = new HashSet<String>(names);
    Set<T> chosen = new HashSet<T>();
    for (T candidate : candidates) {
      if (unusedNames.remove(candidate.getName())) {
        chosen.add(candidate);
      }
    }
    if (!unusedNames.isEmpty()) {
      throw new InvalidUserDataException(String.format("Invalid %s: '%s'", type.getSimpleName(), unusedNames.iterator().next()));
    }
    return chosen;
  }

  @Override
  public String getBaseName() {
    if (this.baseName != null) {
      return this.baseName;
    } else
      return getName();
  }

  @Override
  public void setBaseName(String baseName) {
    this.baseName = baseName;
  }

  @Override
  public Set<? extends Class<? extends TransformationFileType>> getIntermediateTypes() {
    return Collections.emptySet();
  }

  @Override
  public ModelMap<LanguageSourceSet> getSources() {
    // This should be an empty map!
    return ModelMaps.toView(sources, SOURCE_MODEL_TYPE);
  }
}
