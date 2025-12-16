## HOW I ADDED LUGANDA
I tried to follow the instructions for adding a new language to language tool but I faced some errors while trying to do so, therefore I decided to document how I came over those errors.

The first challenge I ecountered was that the command '''mvn clean package''' was producing errors and the solution was to add the following to the top level pom.xml inside the cofigyuration tag 

'''
<!-- Ensure Lombok annotations (@Getter, @Slf4j, etc.) are processed during Maven builds -->
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
</annotationProcessorPaths>
'''

After following the instructions for adding a new language again, I reached the step which says run '''mvn clean package''' and whe I ran it, other errors surfaced.

'''
[ERROR] 'dependencies.dependency.version' for org.languagetool:language-lg:jar is missing.
'''
which indicates that while I had correctly added the language-lg dependency to the pom.xml language-all module, Maven cannot figure out which version of it to use.


So to resolve the build error. I had to add the ${project.version} to the dependency in '''languagetool\languagetool-language-modules\all\pom.xml'''

'''
<dependency>
    <groupId>org.languagetool</groupId>
    <artifactId>language-lg</artifactId>
    <version>${project.version}</version>
</dependency>
'''

By adding <version>${project.version}</version> to the language-lg dependency, you are explicitly telling Maven to use the same version as the other modules in the project, which should resolve the build error.

Then I ran '''mvn clean install -DskipTests''' and the process went smooth for a while and all of a sudden, it crashed.

'''
[INFO] --- compiler:3.8.1:compile (default-compile) @ language-lg ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 1 source file to E:\languagetool\languagetool-language-modules\lg\target\classes
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] /E:/languagetool/languagetool-language-modules/lg/src/main/java/org/languagetool/language/Luganda.java:[86,10] invalid method declaration; return type required
[INFO] 1 error
'''

According to the instructions, we have to copy English.java and adopt it and I also just did so but English.java is so huge for the start and so there were so many English specific rules and according to my opinion, a small language class should be adapted for example Arabic.java which required deleted a few Arabic specific rules and retaining the basic and required rules for the project to build successfully. 




