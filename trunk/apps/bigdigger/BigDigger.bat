SET URL=http://java.sun.com/index.html

SET PROXY=-DproxyHost=proxy-server.bms.com -DproxyPort=8080

mvn exec:java -Durl=%URL% %PROXY%
