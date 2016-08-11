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

package de.jfalk.gradle.nativeplatform.internal.resolve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.ProjectRegistry;
import org.gradle.api.internal.resolve.ProjectModelResolver;
import org.gradle.api.UnknownProjectException;
import org.gradle.model.internal.registry.ModelRegistry;

public class JFProjectModelResolver implements ProjectModelResolver {
  private final Logger                           logger;

  private final String                           currentProjectPath;
  private final ProjectRegistry<ProjectInternal> delegate;

  public JFProjectModelResolver(String currentProjectPath, ProjectRegistry<ProjectInternal> delegate) {
    this.logger             = LoggerFactory.getLogger(this.getClass());
    logger.debug("JFProjectModelResolver('"+currentProjectPath+"', ...) [CALLED]");
    this.currentProjectPath = currentProjectPath;
    this.delegate           = delegate;
    logger.debug("JFProjectModelResolver('"+currentProjectPath+"', ...) [DONE]");
  }

  @Override
  public ModelRegistry resolveProjectModel(String path) {
    String showPath = path == null ? "null" : "\"" + path + "\"";
    if (path == null || path.length() == 0) {
      path = currentProjectPath;
    }
    ProjectInternal projectInternal = delegate.getProject(path);
    if (projectInternal == null) {
      throw new UnknownProjectException("Project with path \"" + path + "\" not found.");
    }
    logger.debug("resolveProjectModel("+showPath+") => resolved to " + projectInternal);

    // TODO This is a brain-dead way to ensure that the reference project's model is ready to access
    projectInternal.evaluate();
//  projectInternal.getTasks().discoverTasks();
    return projectInternal.getModelRegistry();
  }
}
