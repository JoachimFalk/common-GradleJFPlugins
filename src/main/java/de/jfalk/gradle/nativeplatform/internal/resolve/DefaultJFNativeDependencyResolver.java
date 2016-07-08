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

//import javax.inject.Inject;
import java.util.AbstractMap.SimpleEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jfalk.gradle.nativeplatform.internal.DefaultJFNativeLibraryRequirement;
import de.jfalk.gradle.nativeplatform.JFNativeLibraryRequirement;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.internal.file.FileCollectionFactory;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.tasks.Optional;
import org.gradle.internal.exceptions.DiagnosticsVisitor;
import org.gradle.internal.typeconversion.MapKey;
import org.gradle.internal.typeconversion.MapNotationConverter;
import org.gradle.internal.typeconversion.NotationParser;
import org.gradle.internal.typeconversion.NotationParserBuilder;
import org.gradle.internal.typeconversion.TypedNotationConverter;
import org.gradle.language.base.internal.resolve.LibraryResolveException;
import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.internal.resolve.ApiRequirementNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.InputHandlingNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.LibraryBinaryLocator;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryRequirementResolveResult;
import org.gradle.nativeplatform.internal.resolve.NativeBinaryResolveResult;
import org.gradle.nativeplatform.internal.resolve.NativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.SourceSetNativeDependencyResolver;
import org.gradle.nativeplatform.internal.resolve.DefaultNativeDependencySet;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.NativeLibraryBinary;
import org.gradle.nativeplatform.NativeLibraryRequirement;
import org.gradle.nativeplatform.NativeLibrarySpec;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.SharedLibraryBinary;
import org.gradle.nativeplatform.StaticLibraryBinary;

public class DefaultJFNativeDependencyResolver implements NativeDependencyResolver {
  private final Logger                   logger;
  private final NativeDependencyResolver resolver;

//@Inject
  public DefaultJFNativeDependencyResolver(LibraryBinaryLocator locator, FileCollectionFactory fileCollectionFactory) {
    this.logger = LoggerFactory.getLogger(this.getClass());
    logger.debug("DefaultJFNativeDependencyResolver(...) [CALLED]");
    this.resolver =
      new InputHandlingNativeDependencyResolver(
        new SourceSetNativeDependencyResolver(
          new RequirementParsingNativeDependencyResolver(
            new ApiRequirementNativeDependencyResolver(
              new LibraryNativeDependencyResolver(locator),
              fileCollectionFactory)),
          fileCollectionFactory));
    logger.debug("DefaultJFNativeDependencyResolver(...) [DONE]");
  }

  private static class RequirementParsingNativeDependencyResolver implements NativeDependencyResolver {
    private final NotationParser<Object, NativeLibraryRequirement> parser;
    private final NativeDependencyResolver                         delegate;

    public RequirementParsingNativeDependencyResolver(NativeDependencyResolver delegate) {
      this.delegate = delegate;
      this.parser   = NotationParserBuilder
        .toType(NativeLibraryRequirement.class)
        .converter(new LibraryConverter())
        .converter(new NativeLibraryRequirementMapNotationConverter())
        .toComposite();
    }

    private static class LibraryConverter extends TypedNotationConverter<NativeLibrarySpec, NativeLibraryRequirement> {
      private LibraryConverter() {
        super(NativeLibrarySpec.class);
      }

      @Override
      protected NativeLibraryRequirement parseType(NativeLibrarySpec notation) {
        return notation.getShared();
      }
    }
    
    private static class NativeLibraryRequirementMapNotationConverter extends MapNotationConverter<NativeLibraryRequirement> {
      @Override
      public void describe(DiagnosticsVisitor visitor) {
        visitor
          .candidate("Map with mandatory 'library' and optional 'project' and 'linkage' keys")
          .example("[project: ':someProj', library: 'mylib', linkage: 'static']");
      }

      @SuppressWarnings("unused")
      protected NativeLibraryRequirement parseMap(@MapKey("library") String libraryName, @Optional @MapKey("project") String projectPath, @Optional @MapKey("linkage") String linkage, @Optional @MapKey("flavor") String flavor) {
        return new DefaultJFNativeLibraryRequirement(projectPath, libraryName, linkage, flavor);
      }
    }

    @Override
    public void resolve(NativeBinaryResolveResult nativeBinaryResolveResult) {
      for (NativeBinaryRequirementResolveResult resolution : nativeBinaryResolveResult.getPendingResolutions()) {
        NativeLibraryRequirement requirement = parser.parseNotation(resolution.getInput());
        resolution.setRequirement(requirement);
      }
      delegate.resolve(nativeBinaryResolveResult);
    }
  }

  private static class LibraryNativeDependencyResolver implements NativeDependencyResolver {
    private final LibraryBinaryLocator libraryBinaryLocator;

    public LibraryNativeDependencyResolver(final LibraryBinaryLocator locator) {
      libraryBinaryLocator = locator;
    }

    private static SimpleEntry<String, Class<? extends NativeLibraryBinary> > getTypeForLinkage(String linkage) {
      if ("static".equals(linkage)) {
        return new SimpleEntry<String, Class<? extends NativeLibraryBinary> >("static", StaticLibraryBinary.class);
      }
      if ("shared".equals(linkage) || linkage == null) {
        return new SimpleEntry<String, Class<? extends NativeLibraryBinary> >("shared", SharedLibraryBinary.class);
      }
      throw new InvalidUserDataException("Not a valid linkage: " + linkage);
    }

    private NativeLibraryBinary resolveLibraryBinary(NativeLibraryRequirement requirement, NativeBinarySpec context) {
      DomainObjectSet<NativeLibraryBinary> allBinaries = libraryBinaryLocator.getBinaries(requirement);
      if (allBinaries == null) {
        throw new LibraryResolveException(requirement.getProjectPath() == null
          ? String.format("Could not locate library '%s'.", requirement.getLibraryName())
          : String.format("Could not locate library '%s' for project '%s'.", requirement.getLibraryName(), requirement.getProjectPath()));
      }
      SimpleEntry<String, Class<? extends NativeLibraryBinary> > linkageInfo =
        getTypeForLinkage(requirement.getLinkage());
      DomainObjectSet<? extends NativeLibraryBinary> candidates =
        allBinaries.withType(linkageInfo.getValue());
      
      String flavor    = context.getFlavor() != null ? context.getFlavor().getName() : null;
      String platform  = context.getTargetPlatform() != null ? context.getTargetPlatform().getName() : null;
      String buildType = context.getBuildType() != null ? context.getBuildType().getName() : null;
      if (requirement instanceof JFNativeLibraryRequirement) {
        @SuppressWarnings("unchecked")
        JFNativeLibraryRequirement jfRequirement = (JFNativeLibraryRequirement) requirement;
        if (jfRequirement.getFlavor() != null) {
          flavor = jfRequirement.getFlavor();
        }
      }

      for (NativeLibraryBinary candidate : candidates) {
        if (flavor != null && !flavor.equals(candidate.getFlavor().getName())) {
          continue;
        }
        if (platform != null && !platform.equals(candidate.getTargetPlatform().getName())) {
          continue;
        }
        if (buildType != null && !buildType.equals(candidate.getBuildType().getName())) {
          continue;
        }
        return candidate;
      }
      throw new LibraryResolveException(String.format("No %s library binary available for library '%s' with [flavor: '%s', platform: '%s', buildType: '%s']",
        linkageInfo.getKey(), requirement.getLibraryName(), flavor, platform, buildType));
    }
   
    @Override
    public void resolve(NativeBinaryResolveResult resolution) {
      for (NativeBinaryRequirementResolveResult requirementResolution : resolution.getPendingResolutions()) {
        NativeLibraryBinary libraryBinary = resolveLibraryBinary(requirementResolution.getRequirement(), resolution.getTarget());
        requirementResolution.setLibraryBinary(libraryBinary);
        requirementResolution.setNativeDependencySet(new DefaultNativeDependencySet(libraryBinary));
      }
    }
  }

  @Override
  public void resolve(NativeBinaryResolveResult resolution) {
    logger.debug("resolve(...) [CALLED]");
    resolver.resolve(resolution);
    logger.debug("resolve(...) [DONE]");
  }
}
