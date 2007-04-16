SET PROXY=-DproxyHost=proxy-server.bms.com -DproxyPort=8080

mvn exec:java %PROXY%
