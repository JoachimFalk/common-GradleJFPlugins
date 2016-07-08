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

import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.gradle.language.cpp.CppSourceSet;
import org.gradle.language.nativeplatform.internal.AbstractHeaderExportingDependentSourceSet;

interface JFCppSourceSet extends CppSourceSet {

  /// The libs which have been marked by the exportHeaders flag.
  public Collection<?> getHeaderReexportLibs();

}

public class DefaultJFCppSourceSet extends AbstractHeaderExportingDependentSourceSet implements JFCppSourceSet {

  /// The libs which have been marked by the exportHeaders flag.
  @Override
  public Collection<?> getHeaderReexportLibs() {
    Collection<Object> libs = new ArrayList<Object>();
    for (Object lib : super.getLibs()) {
      if (lib instanceof Map<?>) {
        Object exportHeaders = lib.get("exportHeaders");
        if ((exportHeaders instanceof Boolean && (Boolean) exportHeaders) ||
            (exportHeaders instanceof Integer && (Integer) exportHeaders) ||
            (exportHeaders instanceof String && (String) (exportHeaders).toLowerCase().equals("yes")) ||
            (exportHeaders instanceof String && (String) (exportHeaders).toLowerCase().equals("1"))) {
          Map<Object> entry = new HashMap<Object>(lib);
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
      if (lib instanceof Map<?>) {
        Map<Object> entry = new HashMap<Object>(lib);
        entry.remove("exportHeaders");
        libs.add(entry);
      } else {
        libs.add(lib);
      }
    }
    return libs;
  }

  @Override
  protected String getLanguageName() {
    return "C++";
  }
}
