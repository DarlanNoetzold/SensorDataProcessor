@echo off
setlocal

:: Configurar caminho do JDK
set JAVA_HOME=C:\Program Files\Java\jdk-22

:: Verificar se JAVA_HOME está configurado corretamente
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo JAVA_HOME is not set correctly.
    exit /b 1
)

:: Compilar data_filter.c
gcc -shared -o libdata_filter.dll -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" data_filter.c

:: Compilar data_compression.c
gcc -shared -o libdata_compression.dll -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" data_compression.c

:: Compilar data_aggregation.c
gcc -shared -o libdata_aggregation.dll -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" data_aggregation.c

endlocal
echo Compilation complete.