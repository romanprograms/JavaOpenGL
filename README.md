# Computer Graphics Programming with OpenGL in Java

```shell
mvn install:install-file \
  -Dfile=lib/jogl-all.jar \
  -DgroupId=com.jogamp \
  -DartifactId=jogl-all \
  -Dversion=2.5.0 \
  -Dpackaging=jar

mvn install:install-file \
  -Dfile=lib/jogl-all-natives-macosx-universal.jar \
  -DgroupId=com.jogamp \
  -DartifactId=jogl-all-natives-macosx-universal \
  -Dversion=2.5.0 \
  -Dpackaging=jar

mvn install:install-file \
  -Dfile=lib/gluegen-rt.jar \
  -DgroupId=com.jogamp \
  -DartifactId=gluegen-rt \
  -Dversion=2.5.0 \
  -Dpackaging=jar

mvn install:install-file \
  -Dfile=lib/gluegen-rt-natives-macosx-universal.jar \
  -DgroupId=com.jogamp \
  -DartifactId=gluegen-rt-natives-macosx-universal \
  -Dversion=2.5.0 \
  -Dpackaging=jar

```

Step 2: Update pom.xml — remove <scope>system</scope> and use this:
```xml
<dependencies>
  <dependency>
    <groupId>com.jogamp</groupId>
    <artifactId>jogl-all</artifactId>
    <version>2.5.0</version>
  </dependency>
  <dependency>
    <groupId>com.jogamp</groupId>
    <artifactId>jogl-all-natives-macosx-universal</artifactId>
    <version>2.5.0</version>
  </dependency>
  <dependency>
    <groupId>com.jogamp</groupId>
    <artifactId>gluegen-rt</artifactId>
    <version>2.5.0</version>
  </dependency>
  <dependency>
    <groupId>com.jogamp</groupId>
    <artifactId>gluegen-rt-natives-macosx-universal</artifactId>
    <version>2.5.0</version>
  </dependency>
</dependencies>

```
Step 3: Refresh Maven
In IntelliJ, open the Maven tab

Click the refresh icon (🔄) or right-click your project → Reload Maven Project

IntelliJ will now:

See the JARs as standard Maven dependencies

Resolve all imports

Fix code completion, symbol lookup, and error highlightin

Step 4: Run the project
```shell
chmod +x run.sh 
./run.sh
```
or
You need to add this JVM argument to your IntelliJ run configuration
open project settings (⌘, or Ctrl+,) → search for "VM options" → add the following line:
```shell
--add-exports java.desktop/sun.awt=ALL-UNNAMED

```