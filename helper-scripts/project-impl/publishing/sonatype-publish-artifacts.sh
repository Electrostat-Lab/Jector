#!/bin/bash

source "./helper-scripts/abstract/abstract-sonatype-publish.sh"
source "./helper-scripts/project-impl/variables.sh"

# obtain dependencies in the form 'groupId:artifact:version'
version=${1}

lib_artifact="${lib_build_dir}/${lib_artifactId_release}-${version}.jar"
lib_sources_jar="${lib_build_dir}/${lib_artifactId_release}-${version}-sources.jar"
lib_javadoc_jar="${lib_build_dir}/${lib_artifactId_release}-${version}-javadoc.jar"

ext_artifact="${ext_build_dir}/${ext_artifactId_release}-${version}.jar"
ext_sources_jar="${ext_build_dir}/${ext_artifactId_release}-${version}-sources.jar"
ext_javadoc_jar="${ext_build_dir}/${ext_artifactId_release}-${version}-javadoc.jar"

generateGenericPom "${groupId}" \
                   "${lib_artifactId_release}" \
                   "${version}" \
                   "The Jector Framework" \
                   "A Dependency Injection framework designed for JVM and Android applications with an Open-Ended API and Submit-Callback features." \
                   "https://github.com/Software-Hardware-Codesign/Jector" \
                   "The AvrSandbox Project, Jector Framework, BSD-3 Clause License" \
                   "https://github.com/Software-Hardware-Codesign/Jector/blob/master/LICENSE" \
                   "Pavly Gerges (aka. pavl_g)" \
                   "pepogerges33@gmail.com" \
                   "The AvrSandbox" \
                   "https://github.com/Software-Hardware-Codesign" \
                   "scm:git:git://github.com/Software-Hardware-Codesign/Jector.git" \
                   "${lib_pomFile}"

generateGenericPom "${groupId}" \
                   "${ext_artifactId_release}" \
                   "${version}" \
                   "The JectorMonkey API" \
                   "A Specialized implementation of the Jector Framework for jMonkeyEngine." \
                   "https://github.com/Software-Hardware-Codesign/Jector" \
                   "The AvrSandbox Project, Jector Framework, BSD-3 Clause License" \
                   "https://github.com/Software-Hardware-Codesign/Jector/blob/master/LICENSE" \
                   "Pavly Gerges (aka. pavl_g)" \
                   "pepogerges33@gmail.com" \
                   "The AvrSandbox" \
                   "https://github.com/Software-Hardware-Codesign" \
                   "scm:git:git://github.com/Software-Hardware-Codesign/Jector.git" \
                   "${ext_pomFile}"

# publish 'android' and 'desktop' builds to maven sonatype
publishBuild "${lib_artifactId_release}" "${lib_artifact}" "${version}" "${lib_javadoc_jar}" "${lib_sources_jar}" "${lib_pomFile}"
publishBuild "${ext_artifactId_release}" "${ext_artifact}" "${version}" "${ext_javadoc_jar}" "${ext_sources_jar}" "${ext_pomFile}"