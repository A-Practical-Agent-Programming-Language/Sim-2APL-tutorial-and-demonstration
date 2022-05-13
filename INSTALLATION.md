The current repository is a demonstration of Sim-2APL using time-step synchronized simulation environments.
The provided environment is a simple, terminal-based Grid World, in which agents can move around and nothing else.

### Requirements
The current repository is managed with Maven
Make sure a Java JDK (11+) and Maven are installed

### IDE setup
Setup using an IDE depends on what IDE is being used. Most IDEs will offer an option to import a Maven
project from Github. 
The project depends on Sim-2APL, so download the Sim-2APL
[JAR file](https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL/releases/tag/v2.0.0)
and add it as a dependency to the project.

### Terminal setup
First, clone the Sim-2APL repository, and install it to the local .m2 repository:

```bash
git clone https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL.git sim2apl
cd sim2apl
mvn install
```

Then clone this repository and build it with maven

```bashs
git clone https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL-tutorial-and-demonstration.git tutorial
cd tutorial
mvn install
```

You can now run the project with

```bash
java -jar target/sim-2apl-example-1.0-SNAPSHOT-jar-with-dependencies.jar 
```

## License

This library contains free software; The code can be freely used under the Mozilla Public License 2.0. See the 
[license file](LICENSE) for details. 
This code comes with ABSOLUTELY NO WARRANTY, to the extent permitted by applicable law.