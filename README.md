# High Performance Computing in Education - Java Bytecode Instrumentation

**This repository is continued work on a project by Robert Price: https://gitlab.com/REPIII/hpc-edu-java-instrumentation/*

This library intends to support the flexible instrumentation of Java
Bytecode through "target" classes, which encapsulate the necessary
information that composes an injection target, as well as the
methods that are to be injected into that location by an injector.

While this library maintains a flexible framework, in which
alternative injectors can be used, the default injector is
built on top of [Byteman](https://byteman.jboss.org/) (Byteman
4.0.7), the binaries for which can be found
[here.](https://downloads.jboss.org/byteman/4.0.7/byteman-download-4.0.7-bin.zip)

This library is built using Apache Ant, and is dependent on the
above Byteman directory being added to the top-level directory.

To build:

    ant build

To combine into a jar file that can be used as a Java Agent for
injection:

    ant jar

The jar file will contain the Byteman references this library
requires at runtime along side the library itself and all targets
to be injected.

-----

To develop a target class for a new injection target, extend one of
the abstract classes in injector.target. It will be injected
into its target if it is registered with an injector that injects
its targets into the binaries during the program's premain.
Example code for a target and its associated premain can be found
within testing inside the **source** directory. It is targeting the
test files in the **testing** directory.

Currently only writes can be instrumented, and I hope
writes to annotated members will be supported soon. While
variable writes are supported, variable lookups are not always
successful. Field writes are fully supported with no known issues.
See injector.target.InjectionTargetWritable for more information
about creating a writable injection target.

To utilize this instrumentation, add the jar file created with this
library as the Java Agent when running the code to be instrumented
on the JVM. This can be done using -javaagent:agent\_path.jar.
Currently the ant build files handles this process for the injector
sample code included in this repository.

-----

This library was written as part of a larger research effort into
high performance computer in education at the University of North
Carolina at Chapel Hill, Department of Computer Science.

This repository is licensed under GNU General Public License v3.0.
See LICENSE.txt for more information.
