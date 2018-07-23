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

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;
import de.jfalk.gradle.nativeplatform.JFNativeLibraryBinary;

import org.gradle.api.DomainObjectSet;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.Tool;
import org.gradle.platform.base.ComponentSpec;

public class ToolImpl implements Tool {
  protected final Logger logger;

  public static interface ToolLocator {
    Tool locateTool(JFHeaderExportingDependentInterfaceSet interfaceSet);
    Tool locateTool(JFExportedCompileAndLinkConfiguration  exportedFlags);
  }

  protected final ArrayList<String>              args = new ArrayList<String>();
  protected final JFNativeBinarySpecInternal     owner;
  protected final DomainObjectSet<ComponentSpec> inputInterfaceSets;
  protected final ToolLocator                    toolLocator;
  protected final boolean                        exportOrInternalUsage;

  /// @param owner The NativeBinarySpec component that uses this tool definition or
  ///   has a JFExportedCompileAndLinkConfiguration where this tool definition is used in.
  /// @param inputInterfaceSets All {@link de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet}
  ///   that serve as input to the owner NativeBinarySpec.
  /// @param toolLocator Use to get the correct tool (linker, c compiler, c++ compiler, )..
  /// @param exportOrInternalUsage If true, then this Tool is used as part of
  ///  {@link de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet}.
  ///  Otherwise, this is used as a Tool in a JFSharedLibraryBinarySpec or a JFStaticLibraryBinarySpec.
  public ToolImpl(
      final JFNativeBinarySpecInternal     owner,
      final DomainObjectSet<ComponentSpec> inputInterfaceSets,
      final ToolLocator                    toolLocator,
      final boolean                        exportOrInternalUsage)
  {
    this.logger                = LoggerFactory.getLogger(this.getClass());
    this.owner                 = owner;
    this.inputInterfaceSets    = inputInterfaceSets;
    this.toolLocator           = toolLocator;
    this.exportOrInternalUsage = exportOrInternalUsage;
  }

  protected ToolLocator getToolLocator() {
    return this.toolLocator;
  }

  @Override
  public void args(String... args) {
    Collections.addAll(this.args, args);
  }

  @Override
  public ArrayList<String> getArgs() {
    ArrayList<String> args = new ArrayList<String>(this.args);
    for (JFHeaderExportingDependentInterfaceSet interfaceSet : inputInterfaceSets.withType(JFHeaderExportingDependentInterfaceSet.class)) {
      if (this.exportOrInternalUsage) {
        args.addAll(getToolLocator().locateTool(interfaceSet).getArgs());
      }
      NativeBinaryResolveResult resolution = new NativeBinaryResolveResult(owner,
        this.exportOrInternalUsage
        ? interfaceSet.getHeaderReexportLibs()
        : interfaceSet.getLibs());
      owner.getResolver().resolve(resolution);
      for (NativeLibraryBinary dependency : resolution.getAllLibraryBinaries()) {
        if (dependency instanceof JFNativeLibraryBinary) {
//        @SuppressWarnings("unchecked")
          JFNativeLibraryBinary jfNativeLibraryBinary = (JFNativeLibraryBinary) dependency;
          args.addAll(getToolLocator().locateTool(jfNativeLibraryBinary.getExportedCompileAndLinkConfiguration()).getArgs());
        }
      }
    }
    return args;
  }
}
