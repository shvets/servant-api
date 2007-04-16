SET PROXY=-DproxyHost=proxy-server.bms.com -DproxyPort=8080

SET MAIN_CLASS=-DmainClass=org.google.code.netapps.scriptrunner.client.SRClient

SET CMD1=-Dcmd1=promote
SET CMD2=-Dcmd2=ScriptExecutor.java

mvn exec:java %PROXY% %MAIN_CLASS% %CMD1% %CMD2%
