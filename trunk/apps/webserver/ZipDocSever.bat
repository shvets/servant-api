SET PROXY=-DproxyHost=proxy-server.bms.com -DproxyPort=8080

SET MAIN_CLASS=-DmainClass=org.google.code.netapps.zipdoc.ZipDocServer

mvn exec:java %PROXY% %MAIN_CLASS%
