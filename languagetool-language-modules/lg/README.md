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

So the command ''' mvn clean install -DskipTests -rf: language-lg''' worked but when I tried to run  '''languagetool-standalone\src\main\java\org\languagetool\gui\Main.java''' , it failed and returned the errors below. 

''' 
Exception in thread "AWT-EventQueue-0" java.lang.ExceptionInInitializerError
        at org.languagetool.language.identifier.LanguageIdentifierService.getDefaultLanguageIdentifier(LanguageIdentifierService.java:54)
        at org.languagetool.gui.LanguageToolSupport.<init>(LanguageToolSupport.java:105)
        at org.languagetool.gui.Main.createGUI(Main.java:464)
        at org.languagetool.gui.Main$7.run(Main.java:1199)
        at java.desktop/java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:318)
        at java.desktop/java.awt.EventQueue.dispatchEventImpl(EventQueue.java:773)
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:720)
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:714)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
        at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
        at java.desktop/java.awt.EventQueue.dispatchEvent(EventQueue.java:742)
        at java.desktop/java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:203)
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:124) 
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:113)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:109)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
        at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:90)
Caused by: java.lang.RuntimeException: java.io.IOException: Common words file not found for Luganda: lg/common_words.txt
        at org.languagetool.language.identifier.LanguageIdentifier.<clinit>(LanguageIdentifier.java:59)
        ... 17 more
Caused by: java.io.IOException: Common words file not found for Luganda: lg/common_words.txt
        at org.languagetool.language.identifier.detector.CommonWordsDetector.<init>(CommonWordsDetector.java:68)
        at org.languagetool.language.identifier.LanguageIdentifier.<clinit>(LanguageIdentifier.java:57)
        ... 17 more
PS E:\languagetool> 
'''

So the file common_words.txt was required but still after it was create, I ran into errors 

'''
java.lang.RuntimeException: Could not set up language identifier
        at org.languagetool.language.identifier.DefaultLanguageIdentifier.<init>(DefaultLanguageIdentifier.java:118)
        at org.languagetool.language.identifier.DefaultLanguageIdentifier.<init>(DefaultLanguageIdentifier.java:89)
        at org.languagetool.language.identifier.LanguageIdentifierService.getDefaultLanguageIdentifier(LanguageIdentifierService.java:54)
        at org.languagetool.gui.LanguageToolSupport.<init>(LanguageToolSupport.java:105) 
        at org.languagetool.gui.Main.createGUI(Main.java:464)
        at org.languagetool.gui.Main$7.run(Main.java:1199)
        at java.desktop/java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:318)
        at java.desktop/java.awt.EventQueue.dispatchEventImpl(EventQueue.java:773)       
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:720)
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:714)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
        at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
        at java.desktop/java.awt.EventQueue.dispatchEvent(EventQueue.java:742)
        at java.desktop/java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:203)
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:124)
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:113)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:109)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
        at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:90)    
Caused by: java.io.IOException: No language file available named lg at languages/lg!     
        at com.optimaize.langdetect.profiles.LanguageProfileReader.read(LanguageProfileReader.java:53)
        at com.optimaize.langdetect.profiles.LanguageProfileReader.read(LanguageProfileReader.java:78)
        at org.languagetool.language.identifier.DefaultLanguageIdentifier.loadProfiles(DefaultLanguageIdentifier.java:194)
        at org.languagetool.language.identifier.DefaultLanguageIdentifier.<init>(DefaultLanguageIdentifier.java:102)
        ... 18 more
'''
To resolve this, I added Luganda to the list of ignored languages in "languagetool-core\src\main\java\org\languagetool\language\identifier\DefaultLanguageIdentifier.java"
as: ''' private static final List<String> ignoreLangCodes = Arrays.asList("ast", "gl", "lg");'''

Then I ran into another error, though the GUI had launched

'''
WARN  o.l.l.i.DefaultLanguageIdentifier fastText not configured - language detection performance will be degraded. See https://dev.languagetool.org/http-server#starting-from-command-line for instructions.
Exception in thread "AWT-EventQueue-0" java.lang.RuntimeException: java.lang.RuntimeException: Could not activate rules
        at org.languagetool.gui.LanguageToolSupport.reloadLanguageTool(LanguageToolSupport.java:258)
        at org.languagetool.gui.LanguageToolSupport.setLanguage(LanguageToolSupport.java:418)
        at org.languagetool.gui.Main$1.itemStateChanged(Main.java:483)
        at java.desktop/javax.swing.JComboBox.fireItemStateChanged(JComboBox.java:1257)  
        at java.desktop/javax.swing.JComboBox.selectedItemChanged(JComboBox.java:1318)   
        at java.desktop/javax.swing.JComboBox.contentsChanged(JComboBox.java:1365)       
        at java.desktop/javax.swing.AbstractListModel.fireContentsChanged(AbstractListModel.java:127)
        at java.desktop/javax.swing.DefaultComboBoxModel.setSelectedItem(DefaultComboBoxModel.java:94)
        at java.desktop/javax.swing.JComboBox.setSelectedItem(JComboBox.java:609)        
        at java.desktop/javax.swing.JComboBox.setSelectedIndex(JComboBox.java:654)       
        at java.desktop/javax.swing.plaf.basic.BasicComboPopup$Handler.mouseReleased(BasicComboPopup.java:946)
        at java.desktop/java.awt.AWTEventMulticaster.mouseReleased(AWTEventMulticaster.java:298)
        at java.desktop/java.awt.Component.processMouseEvent(Component.java:6626)        
        at java.desktop/javax.swing.JComponent.processMouseEvent(JComponent.java:3389)   
        at java.desktop/javax.swing.plaf.basic.BasicComboPopup$1.processMouseEvent(BasicComboPopup.java:551)
        at java.desktop/java.awt.Component.processEvent(Component.java:6391)
        at java.desktop/java.awt.Container.processEvent(Container.java:2266)
        at java.desktop/java.awt.Component.dispatchEventImpl(Component.java:5001)        
        at java.desktop/java.awt.Container.dispatchEventImpl(Container.java:2324)        
        at java.desktop/java.awt.Component.dispatchEvent(Component.java:4833)
        at java.desktop/java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:4948)
        at java.desktop/java.awt.LightweightDispatcher.processMouseEvent(Container.java:4575)
        at java.desktop/java.awt.LightweightDispatcher.dispatchEvent(Container.java:4516)
        at java.desktop/java.awt.Container.dispatchEventImpl(Container.java:2310)        
        at java.desktop/java.awt.Window.dispatchEventImpl(Window.java:2780)
        at java.desktop/java.awt.Component.dispatchEvent(Component.java:4833)
        at java.desktop/java.awt.EventQueue.dispatchEventImpl(EventQueue.java:775)       
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:720)
        at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:714)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
        at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
        at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:97)
        at java.desktop/java.awt.EventQueue$5.run(EventQueue.java:747)
        at java.desktop/java.awt.EventQueue$5.run(EventQueue.java:745)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
        at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
        at java.desktop/java.awt.EventQueue.dispatchEvent(EventQueue.java:744)
        at java.desktop/java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:203)
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:124)
        at java.desktop/java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:113)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:109)
        at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
        at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:90)    
Caused by: java.lang.RuntimeException: Could not activate rules
        at org.languagetool.JLanguageTool.<init>(JLanguageTool.java:364)
        at org.languagetool.JLanguageTool.<init>(JLanguageTool.java:321)
        at org.languagetool.JLanguageTool.<init>(JLanguageTool.java:299)
        at org.languagetool.JLanguageTool.<init>(JLanguageTool.java:278)
Caused by: java.io.FileNotFoundException: \org\languagetool\rules\lg\grammar.xml (??? ??? ?? ? ????)
        at java.base/java.io.FileInputStream.open0(Native Method)
        at java.base/java.io.FileInputStream.open(FileInputStream.java:216)
        at java.base/java.io.FileInputStream.<init>(FileInputStream.java:157)
        at java.base/java.io.FileInputStream.<init>(FileInputStream.java:111)
        at org.languagetool.Language.initializePatternRules(Language.java:691)
        at org.languagetool.Language.getPatternRules(Language.java:674)
        at org.languagetool.JLanguageTool.activateDefaultPatternRules(JLanguageTool.java:720)
        at org.languagetool.JLanguageTool.<init>(JLanguageTool.java:355)
        ... 49 more
'''


