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

package de.jfalk.GradleJFPlugins;

import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.internal.DefaultNativeExecutableBinarySpec;

interface JFNativeExecutableBinarySpec extends NativeExecutableBinarySpec {

  String getFlammy();

  void setFlammy(String flammy);
}

class DefaultJFNativeExecutableBinarySpec extends DefaultNativeExecutableBinarySpec implements JFNativeExecutableBinarySpec {

  private String flammy;

  @Override
  String getFlammy()
    { return this.flammy; }

  @Override
  void setFlammy(String flammy)
    { this.flammy = flammy; }

}
