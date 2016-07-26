package de.jfalk.gradle.nativeplatform.internal;

import java.util.List;
import java.util.Set;

import de.jfalk.gradle.nativeplatform.JFPrebuiltLibrarySpec;

import org.gradle.nativeplatform.BuildType;
import org.gradle.nativeplatform.Flavor;
import org.gradle.platform.base.internal.PlatformRequirement;

public interface JFPrebuiltLibraryInternal extends JFPrebuiltLibrarySpec {
  List<PlatformRequirement> getTargetPlatforms();
  Set<Flavor>               chooseFlavors(Set<? extends Flavor> candidates);
  Set<BuildType>            chooseBuildTypes(Set<? extends BuildType> candidates);
}
