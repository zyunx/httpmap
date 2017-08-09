setlocal enabledelayedexpansion

set DEPLOY_HOME=%cd%
set MAIN_CLASS=net.zyunx.httpmap.Main

set LIB_JARS="."

cd lib
for %%i in (*) do set LIB_JARS=!LIB_JARS!;%DEPLOY_HOME%\lib\%%i
cd ..

java -classpath %DEPLOY_HOME%\conf;%LIB_JARS% %MAIN_CLASS%
goto end

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:end