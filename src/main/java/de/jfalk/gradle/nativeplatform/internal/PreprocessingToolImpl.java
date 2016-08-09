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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;
import de.jfalk.gradle.nativeplatform.JFNativeLibraryBinary;

import org.gradle.api.DomainObjectSet;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.platform.base.ComponentSpec;

public class PreprocessingToolImpl extends ToolImpl implements PreprocessingTool {
  protected final Map<String, String>      definitions = new LinkedHashMap<String, String>();

  public static interface ToolLocator extends ToolImpl.ToolLocator {
    @Override
    PreprocessingTool locateTool(JFHeaderExportingDependentInterfaceSet interfaceSet);

    @Override
    PreprocessingTool locateTool(JFExportedCompileAndLinkConfiguration  exportedFlags);
  }

  public PreprocessingToolImpl(
      final JFNativeBinarySpecEx           owner,
      final DomainObjectSet<ComponentSpec> inputInterfaceSets,
      final ToolLocator                    toolLocator,
      final boolean                        exportOrInternalUsage)
  {
    super(owner, inputInterfaceSets, toolLocator, exportOrInternalUsage);
  }

  @Override
  protected ToolLocator getToolLocator() {
    @SuppressWarnings("unchecked")
    ToolLocator toolLocator = (ToolLocator) this.toolLocator;
    return toolLocator;
  }

  @Override
  public Map<String, String> getMacros() {
    logger.debug("getMacros() [CALLED]");
    Map<String, String> definitions = new LinkedHashMap<String, String>(this.definitions);
    for (JFHeaderExportingDependentInterfaceSet interfaceSet : inputInterfaceSets.withType(JFHeaderExportingDependentInterfaceSet.class)) {
      if (this.exportOrInternalUsage) {
        definitions.putAll(getToolLocator().locateTool(interfaceSet).getMacros());
      }
      NativeBinaryResolveResult resolution = new NativeBinaryResolveResult(owner,
        this.exportOrInternalUsage
        ? interfaceSet.getHeaderReexportLibs()
        : interfaceSet.getLibs());
      owner.getResolver().resolve(resolution);
      for (NativeLibraryBinary dependency : resolution.getAllLibraryBinaries()) {
        if (dependency instanceof JFNativeLibraryBinary) {
          @SuppressWarnings("unchecked")
          JFNativeLibraryBinary jfNativeLibraryBinary = (JFNativeLibraryBinary) dependency;
          definitions.putAll(getToolLocator().locateTool(jfNativeLibraryBinary.getExportedCompileAndLinkConfiguration()).getMacros());
        }
      }
    }
    logger.debug("getMacros() [DONE] => "+definitions);
    return definitions;
  }

  @Override
  public void define(String def) {
    definitions.put(def, null);
  }

  @Override
  public void define(String def, String value) {
    definitions.put(def, value);
  }
}
