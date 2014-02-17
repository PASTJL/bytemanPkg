@echo off
REM ######## These two environnement Variables must be adapted #########
REM ###
SET JAVA_HOME=C:\Program Files\Java\jre7
REM ## workspace directory must be created before launching thi script
SET workspace=C:\eclipse\workspace\workspaceBM
REM #######################################################

REM IF exist %workspace%\nul ( echo %workspace% exists ) ELSE ( echo the workspace : %workspace% is not found. Please create the directory or adapt the variable && exit \B 1)
IF exist %workspace% ( echo %workspace% is found ) ELSE ( echo the workspace : %workspace% is not found. Please create the directory or adapt the variable workspace && exit /B 1)
@echo off
SET root=%~dp0%..
echo root=%root%
SET CLASSPATH=%root%\bytemanPkg.jar
SET CLASSPATH=%CLASSPATH%;%root%\config
SET CLASSPATH=%CLASSPATH%;%root%\lib\jsch-0.1.49.jar
SET CLASSPATH=%CLASSPATH%;"%JAVA_HOME%\lib\jfxrt.jar"
SET CLASSPATH=%CLASSPATH%;%root%\lib\antlr-runtime-4.1.jar
SET CLASSPATH=%CLASSPATH%;%root%\lib\bytemancheck.jar
SET CLASSPATH=%CLASSPATH%;%root%\lib\jfxmessagebox-1.1.0.jar
SET CLASSPATH=%CLASSPATH%;%root%\lib\scaChart.jar

"%JAVA_HOME%\bin\java"  -Dprism.order=es2,j2d -cp %CLASSPATH% -Droot=%root% -Dworkspace=%workspace% -Dconfig.file=%root%\config\scaChart.properties -Xms128M -Xmx128M jlp.byteman.packager.Main
