set JAVA_HOME=d:\Java\jdk1.3

set INSTALL_DIR=d:\Work\NetApps\Chat

set CLASSPATH=%INSTALL_DIR%\lib\netlib.jar;%INSTALL_DIR%\classes

start %JAVA_HOME%\bin\java -classpath %CLASSPATH% org.javalobby.netapps.chat.ConsoleChatServer
