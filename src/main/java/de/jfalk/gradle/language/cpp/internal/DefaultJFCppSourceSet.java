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

package de.jfalk.gradle.language.cpp.internal;

import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import de.jfalk.gradle.language.cpp.JFCppSourceSet;

import org.gradle.language.nativeplatform.internal.AbstractHeaderExportingDependentSourceSet;
import org.gradle.nativeplatform.Tool;
import org.gradle.nativeplatform.PreprocessingTool;
import org.gradle.nativeplatform.internal.DefaultTool;
import org.gradle.nativeplatform.internal.DefaultPreprocessingTool;

public class DefaultJFCppSourceSet extends AbstractHeaderExportingDependentSourceSet implements JFCppSourceSet {

  private final Tool              linker      = new DefaultTool();
  private final PreprocessingTool cppCompiler = new DefaultPreprocessingTool();

  /// The libs which have been marked by the exportHeaders flag.
  @Override
  public Collection<?> getHeaderReexportLibs() {
    Collection<Object> libs = new ArrayList<Object>();
    for (Object lib : super.getLibs()) {
      if (lib instanceof Map<?,?>) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) lib;
        Object exportHeaders = map.get("exportHeaders");
        if ((exportHeaders instanceof Boolean && (Boolean) exportHeaders) ||
            (exportHeaders instanceof Integer && (Integer) exportHeaders > 0) ||
            (exportHeaders instanceof String && ((String) exportHeaders).toLowerCase().equals("yes")) ||
            (exportHeaders instanceof String && ((String) exportHeaders).toLowerCase().equals("1"))) {
          Map<String, Object> entry = new HashMap<String, Object>(map);
          entry.remove("exportHeaders");
          libs.add(entry);
        }
      }
    }
    return libs;
  }

  @Override
  public Collection<?> getLibs() {
    Collection<Object> libs = new ArrayList<Object>();
    for (Object lib : super.getLibs()) {
      if (lib instanceof Map<?,?>) {
        @SuppressWarnings("unchecked")
        Map<String, Object> entry = new HashMap<String, Object>((Map<String, Object>) lib);
        entry.remove("exportHeaders");
        libs.add(entry);
      } else {
        libs.add(lib);
      }
    }
    return libs;
  }

  @Override
  public String getLanguageName() {
    return "C++";
  }

  @Override
  public Tool getExportedLinkerArgs() {
    return linker;
  }

  @Override
  public PreprocessingTool getExportedCompilerArgs() {
    return cppCompiler;
  }
}
