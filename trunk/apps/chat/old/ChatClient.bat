set JAVA_HOME=d:\Java\jdk1.3

set INSTALL_DIR=d:\Work\NetApps\Chat

set CLASSPATH=%INSTALL_DIR%\lib\netlib.jar;%INSTALL_DIR%\classes

%JAVA_HOME%\bin\java -classpath %CLASSPATH% org.javalobby.netapps.chat.ConsoleChatInteractor localhost 4646 1000 %1 %2 aaa

