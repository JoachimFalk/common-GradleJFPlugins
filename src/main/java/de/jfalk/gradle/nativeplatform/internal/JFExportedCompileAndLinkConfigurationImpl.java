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

import de.jfalk.gradle.nativeplatform.JFExportedCompileAndLinkConfiguration;
import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;
import de.jfalk.gradle.language.cpp.JFCppInterfaceSet;

import org.gradle.api.DomainObjectSet;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.nativeplatform.Tool;
import org.gradle.platform.base.ComponentSpec;

public class JFExportedCompileAndLinkConfigurationImpl implements JFExportedCompileAndLinkConfiguration {

  protected final Tool              linker;
  protected final PreprocessingTool cCompiler;
  protected final PreprocessingTool cppCompiler;

  public static class LinkterToolLocator implements ToolImpl.ToolLocator {
    @Override
    public Tool locateTool(JFHeaderExportingDependentInterfaceSet interfaceSet) {
      if (interfaceSet instanceof JFCppInterfaceSet) {
        return ((JFCppInterfaceSet) interfaceSet).getExportedLinkerArgs();
      }
      return null;
    }
    @Override
    public Tool locateTool(JFExportedCompileAndLinkConfiguration exportedFlags)
      { return exportedFlags.getLinker(); }
  };

  public static class CCompilerToolLocator implements PreprocessingToolImpl.ToolLocator {
    @Override
    public PreprocessingTool locateTool(JFHeaderExportingDependentInterfaceSet interfaceSet) {
      return null;
    }
    @Override
    public PreprocessingTool locateTool(JFExportedCompileAndLinkConfiguration exportedFlags)
      { return exportedFlags.getcCompiler(); }
  };

  public static class CppCompilerToolLocator implements PreprocessingToolImpl.ToolLocator {
    @Override
    public PreprocessingTool locateTool(JFHeaderExportingDependentInterfaceSet interfaceSet) {
      if (interfaceSet instanceof JFCppInterfaceSet) {
        return ((JFCppInterfaceSet) interfaceSet).getExportedCompilerArgs();
      }
      return null;
    }
    @Override
    public PreprocessingTool locateTool(JFExportedCompileAndLinkConfiguration exportedFlags)
      { return exportedFlags.getCppCompiler(); }
  };

  public JFExportedCompileAndLinkConfigurationImpl(
      final JFNativeBinarySpecEx           owner,
      final DomainObjectSet<ComponentSpec> inputInterfaceSets)
  {
    this.linker      = new ToolImpl(owner, inputInterfaceSets, new LinkterToolLocator(), true);
    this.cCompiler   = new PreprocessingToolImpl(owner, inputInterfaceSets, new CCompilerToolLocator(), true);
    this.cppCompiler = new PreprocessingToolImpl(owner, inputInterfaceSets, new CppCompilerToolLocator(), true);
  }

  @Override
  public Tool getLinker() {
    return linker;
  }

  @Override
  public PreprocessingTool getcCompiler() {
    return cCompiler;
  }

  @Override
  public PreprocessingTool getCppCompiler() {
    return cppCompiler;
  }

}
