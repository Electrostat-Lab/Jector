#!/bin/bash

source "./helper-scripts/abstract/abstract-sonatype-publish.sh"
source "./helper-scripts/project-impl/variables.sh"

# obtain dependencies in the form 'groupId:artifact:version'
version=${1}
desktop_artifact="${build_dir}/${desktop_artifactId_release}-${version}.jar"

sources_jar="${build_dir}/${desktop_artifactId_release}-${version}-sources.jar"
javadoc_jar="${build_dir}/${desktop_artifactId_release}-${version}-javadoc.jar"

generateGenericPom "${groupId}" \
                   "${desktop_artifactId_release}" \
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
                   "${desktop_pomFile}"

# publish 'android' and 'desktop' builds to maven sonatype
publishBuild "${desktop_artifactId_release}" "${desktop_artifact}" "${version}" "${javadoc_jar}" "${sources_jar}" "${desktop_pomFile}"