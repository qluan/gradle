/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.jvm;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.PolymorphicDomainObjectContainer;
import org.gradle.api.internal.AbstractBuildableModelElement;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.jvm.JvmBinaryTasks;
import org.gradle.jvm.internal.DefaultJvmBinaryTasks;
import org.gradle.jvm.internal.toolchain.JavaToolChainInternal;
import org.gradle.jvm.platform.JavaPlatform;
import org.gradle.jvm.toolchain.JavaToolChain;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.platform.base.internal.BinaryBuildAbility;
import org.gradle.platform.base.internal.BinaryNamingScheme;
import org.gradle.platform.base.internal.CompositeBuildAbility;
import org.gradle.platform.base.internal.ConfigurableBuildAbility;
import org.gradle.platform.base.internal.ToolSearchBuildAbility;

import java.io.File;

public class DefaultClassDirectoryBinarySpec extends AbstractBuildableModelElement implements ClassDirectoryBinarySpecInternal {
    private final DefaultDomainObjectSet<LanguageSourceSet> sourceSets = new DefaultDomainObjectSet<LanguageSourceSet>(LanguageSourceSet.class);
    private final BinaryNamingScheme namingScheme;
    private final String name;
    private final JavaToolChain toolChain;
    private final JavaPlatform platform;
    private final DefaultJvmBinaryTasks tasks = new DefaultJvmBinaryTasks(this);
    private File classesDir;
    private File resourcesDir;
    private boolean buildable = true;
    private BinaryBuildAbility buildAbility;

    public DefaultClassDirectoryBinarySpec(String name, JavaToolChain toolChain, JavaPlatform platform) {
        this.name = name;
        this.toolChain = toolChain;
        this.platform = platform;
        this.namingScheme = new ClassDirectoryBinaryNamingScheme(removeClassesSuffix(name));
    }

    private String removeClassesSuffix(String name) {
        if (name.endsWith("Classes")) {
            return name.substring(0, name.length() - 7);
        }
        return name;
    }

    public JvmBinaryTasks getTasks() {
        return tasks;
    }

    public JavaToolChain getToolChain() {
        return toolChain;
    }

    public JavaPlatform getTargetPlatform() {
        return platform;
    }

    public void setTargetPlatform(JavaPlatform platform) {
        throw new UnsupportedOperationException();
    }

    public void setToolChain(JavaToolChain toolChain) {
        throw new UnsupportedOperationException();
    }

    public boolean isBuildable() {
        return getBuildAbility().isBuildable();
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public boolean isLegacyBinary() {
        return true;
    }

    public BinaryNamingScheme getNamingScheme() {
        return namingScheme;
    }

    public String getName() {
        return name;
    }

    public File getClassesDir() {
        return classesDir;
    }

    public void setClassesDir(File classesDir) {
        this.classesDir = classesDir;
    }

    public File getResourcesDir() {
        return resourcesDir;
    }

    public void setResourcesDir(File resourcesDir) {
        this.resourcesDir = resourcesDir;
    }

    public FunctionalSourceSet getBinarySources() {
        throw new UnsupportedOperationException();
    }

    public void setBinarySources(FunctionalSourceSet sources) {
        throw new UnsupportedOperationException();
    }

    public void sources(Action<? super PolymorphicDomainObjectContainer<LanguageSourceSet>> action) {
        throw new UnsupportedOperationException();
    }

    public DomainObjectSet<LanguageSourceSet> getSource() {
        return sourceSets;
    }

    public void source(Object source) {
        throw new UnsupportedOperationException();
    }

    public String getDisplayName() {
        return namingScheme.getDescription();
    }

    public String toString() {
        return getDisplayName();
    }

    @Override
    public BinaryBuildAbility getBuildAbility() {
        if (buildAbility == null) {
            buildAbility = new CompositeBuildAbility(
                    new ConfigurableBuildAbility(buildable),
                    new ToolSearchBuildAbility(((JavaToolChainInternal) getToolChain()).select(getTargetPlatform()))
            );
        }
        return buildAbility;
    }
}
