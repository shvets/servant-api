SET PROXY=-DproxyHost=proxy-server.bms.com -DproxyPort=8080

SET MAIN_CLASS=-DmainClass=org.google.code.netapps.chat.ConsoleChatInteractor

SET CMD1=-Dcmd1=localhost
SET CMD2=-Dcmd2=8182
SET CMD3=-Dcmd3=1000

start mvn exec:java %PROXY% %MAIN_CLASS% %CMD1% %CMD2% %CMD3%

rem %JAVA_HOME%\bin\java -classpath %CLASSPATH% org.javalobby.netapps.chat.ConsoleChatInteractor localhost 4646 1000 %1 %2 aaa

