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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class JFHelperFunctions implements Plugin<Project> {

  static void analysis(String prefix, Object obj) {
    println "================================================================================"
    println prefix+" of " + obj.getClass() + ": " + obj 
    try {
      obj.properties.sort().each  { prop, val ->
        println prefix+"."+prop+": " + val
      //if(prop in ["metaClass","class"]) return
      //if(f.hasProperty(prop)) f[prop] = val
      }
    } catch (all) {
      println all
    }
    try {
      obj.metaClass.methods.sort { it.name }.each { method ->
    //  println prefix+"."+method.name+" of type "+method.descriptor
        println prefix+"."+method.signature
      }
    } catch (all) {
      println all
    }
    println "================================================================================"
  }

  void apply(Project project) {
    project.ext.set("analysis", this.&analysis);
  }

}
