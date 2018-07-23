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

package de.jfalk.gradle.language.nativeplatform.base;

import de.jfalk.gradle.language.nativeplatform.JFHeaderExportingDependentInterfaceSet;

import org.gradle.api.internal.AbstractBuildableComponentSpec;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.reflect.ObjectInstantiationException;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.platform.base.internal.ComponentSpecIdentifier;
import org.gradle.platform.base.ModelInstantiationException;

/**
 * Base class that may be used for custom {@link JFHeaderExportingDependentInterfaceSet} implementations.
 * However, it is generally better to use an interface annotated with {@link org.gradle.model.Managed}
 * and not use an implementation class at all.
 */
public class BaseJFHeaderExportingDependentInterfaceSet extends AbstractBuildableComponentSpec {
  // This is here as a convenience for subclasses to create additional SourceDirectorySets
  protected final SourceDirectorySetFactory sourceDirectorySetFactory;

  private static final ThreadLocal<InterfaceSetInfo> NEXT_SOURCE_SET_INFO = new ThreadLocal<InterfaceSetInfo>();

  public static <T extends JFHeaderExportingDependentInterfaceSet> T create(Class<? extends JFHeaderExportingDependentInterfaceSet> publicType, Class<T> implementationType, ComponentSpecIdentifier componentId, SourceDirectorySetFactory sourceDirectorySetFactory) {
    NEXT_SOURCE_SET_INFO.set(new InterfaceSetInfo(componentId, publicType, sourceDirectorySetFactory));
    try {
      try {
        return DirectInstantiator.INSTANCE.newInstance(implementationType);
      } catch (ObjectInstantiationException e) {
        throw new ModelInstantiationException(String.format("Could not create JFHeaderExportingDependentInterfaceSet of type %s", publicType.getSimpleName()), e.getCause());
      }
    } finally {
      NEXT_SOURCE_SET_INFO.set(null);
    }
  }

  public BaseJFHeaderExportingDependentInterfaceSet() {
    this(NEXT_SOURCE_SET_INFO.get());
  }

  private BaseJFHeaderExportingDependentInterfaceSet(InterfaceSetInfo info) {
    super(validate(info).identifier, info.publicType);
    this.sourceDirectorySetFactory = info.sourceDirectorySetFactory;
  }

  private static InterfaceSetInfo validate(InterfaceSetInfo info) {
    if (info == null) {
      throw new ModelInstantiationException("Direct instantiation of a BaseJFHeaderExportingDependentInterfaceSet is not permitted. Use a @ComponentType rule instead.");
    }
    return info;
  }

  private static class InterfaceSetInfo {
    private final ComponentSpecIdentifier identifier;
    private final Class<? extends JFHeaderExportingDependentInterfaceSet> publicType;
    final SourceDirectorySetFactory sourceDirectorySetFactory;

    private InterfaceSetInfo(ComponentSpecIdentifier identifier, Class<? extends JFHeaderExportingDependentInterfaceSet> publicType, SourceDirectorySetFactory sourceDirectorySetFactory) {
      this.identifier = identifier;
      this.publicType = publicType;
      this.sourceDirectorySetFactory = sourceDirectorySetFactory;
    }
  }
}
