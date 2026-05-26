@echo off
setlocal
set DIR=%~dp0

:: Uu tien JAVA_HOME neu co
set JAVA_CMD=java
set JAVAC_CMD=javac

:: Check bundled JRE first
if exist "%DIR%jre\bin\java.exe" (
    set JAVA_CMD="%DIR%jre\bin\java.exe"
    if exist "%DIR%jre\bin\javac.exe" (
        set JAVAC_CMD="%DIR%jre\bin\javac.exe"
    )
    goto :check_args
)

if defined JAVA_HOME (
    set JAVA_CMD="%JAVA_HOME%\bin\java.exe"
    set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"
    goto :check_args
)

for %%I in (javac.exe) do set JAVAC_PATH=%%~dp$PATH:I
if defined JAVAC_PATH (
    set JAVA_CMD="%JAVAC_PATH%java.exe"
    set JAVAC_CMD="%JAVAC_PATH%javac.exe"
)

:check_args

:: Lay phien ban javac de xac dinh --release cho --enable-preview
set JAVA_RELEASE=21
for /f "tokens=2" %%i in ('%JAVAC_CMD% -version 2^>^&1') do (
    set JAVAC_VER=%%i
)
if defined JAVAC_VER (
    for /f "delims=.+- tokens=1" %%a in ("%JAVAC_VER%") do (
        set JAVA_RELEASE=%%a
    )
)

:: Kiem tra lenh clean
if "%~1"=="clean" (
    echo [Nova CLI] Dang don dep thu muc out...
    if exist "%DIR%out" rd /s /q "%DIR%out"
    echo [Nova CLI] Clean hoan tat!
    goto :EOF
)

:: Kiem tra lenh build
if "%~1"=="build" (
    echo [Nova CLI] Dang bien dich ma nguon Interpreter...
    if not exist "%DIR%out" mkdir "%DIR%out"
    %JAVAC_CMD% --release %JAVA_RELEASE% --enable-preview -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    echo [Nova CLI] Build hoan tat!
    goto :EOF
)

:: Kiem tra lenh test-core
if "%~1"=="test-core" (
    echo [Nova CLI] Dang bien dich va chay test core Java...
    if not exist "%DIR%out" mkdir "%DIR%out"
    %JAVAC_CMD% --release %JAVA_RELEASE% --enable-preview -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    %JAVAC_CMD% --release %JAVA_RELEASE% --enable-preview -encoding UTF-8 -cp "%DIR%out" -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%test\nova\parser\ParserAssert.java" "%DIR%test\nova\parser\ParserTest.java" "%DIR%test\nova\interpreter\InterpreterAssert.java" "%DIR%test\nova\interpreter\InterpreterTest.java" "%DIR%test\nova\repl\NovaReplTest.java"
    echo [Nova CLI] Dang chay ParserTest...
    %JAVA_CMD% --enable-preview -cp "%DIR%out" nova.parser.ParserTest
    echo --------------------------------------------------
    echo [Nova CLI] Dang chay InterpreterTest...
    %JAVA_CMD% --enable-preview -cp "%DIR%out" nova.interpreter.InterpreterTest
    echo --------------------------------------------------
    echo [Nova CLI] Dang chay NovaReplTest...
    %JAVA_CMD% --enable-preview -cp "%DIR%out" nova.repl.NovaReplTest

    goto :EOF
)

:: Chạy từ nova.jar nếu đã đóng gói, ngược lại chạy từ thư mục out
if exist "%DIR%nova.jar" (
    %JAVA_CMD% --enable-preview -cp "%DIR%nova.jar" Main %*
) else (
    if not exist "%DIR%out" (
        echo [Nova CLI] Khoi tao build lan dau...
        mkdir "%DIR%out"
        %JAVAC_CMD% --release %JAVA_RELEASE% --enable-preview -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    )
    %JAVA_CMD% --enable-preview -cp "%DIR%out" Main %*
)

endlocal
