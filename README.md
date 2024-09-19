<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>json-xml-converter</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>json-xml-converter</name>
    <description>Maven project for converting JSON to XML and XML to JSON</description>

    <properties>
        <java.version>17</java.version> <!-- Set to your desired Java version -->
    </properties>

    <dependencies>
        <!-- Jackson XML support -->
        <dependency>
            <groupId>com.fasterxml.jackson.dataformat</groupId>
            <artifactId>jackson-dataformat-xml</artifactId>
        </dependency>

        <!-- Jakarta XML Bind dependency for XML marshalling/unmarshalling -->
        <dependency>
            <groupId>org.glassfish.jaxb</groupId>
            <artifactId>jaxb-runtime</artifactId>
        </dependency>

        <!-- Jettison for BadgerFish conversion -->
        <dependency>
            <groupId>org.codehaus.jettison</groupId>
            <artifactId>jettison</artifactId>
            <version>1.4.1</version>
        </dependency>

        <!-- Dependency for exception handling (custom library) -->
        <dependency>
            <groupId>com.infosys</groupId>
            <artifactId>exception</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

        <!-- Unit Testing dependencies (use JUnit or any other testing library) -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler Plugin for Java 17 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
