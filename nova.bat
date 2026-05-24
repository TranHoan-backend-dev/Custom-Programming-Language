@echo off
setlocal
set DIR=%~dp0

:: Uu tien JAVA_HOME neu co
set JAVA_CMD=java
set JAVAC_CMD=javac

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
    %JAVAC_CMD% -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    echo [Nova CLI] Build hoan tat!
    goto :EOF
)

:: Kiem tra lenh test-core
if "%~1"=="test-core" (
    echo [Nova CLI] Dang bien dich va chay test core Java...
    if not exist "%DIR%out" mkdir "%DIR%out"
    %JAVAC_CMD% -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    %JAVAC_CMD% -encoding UTF-8 -cp "%DIR%out" -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%test\nova\parser\ParserAssert.java" "%DIR%test\nova\parser\ParserTest.java" "%DIR%test\nova\interpreter\InterpreterAssert.java" "%DIR%test\nova\interpreter\InterpreterTest.java" "%DIR%test\nova\repl\NovaReplTest.java"
    echo [Nova CLI] Dang chay ParserTest...
    %JAVA_CMD% -cp "%DIR%out" nova.parser.ParserTest
    echo --------------------------------------------------
    echo [Nova CLI] Dang chay InterpreterTest...
    %JAVA_CMD% -cp "%DIR%out" nova.interpreter.InterpreterTest
    echo --------------------------------------------------
    echo [Nova CLI] Dang chay NovaReplTest...
    %JAVA_CMD% -cp "%DIR%out" nova.repl.NovaReplTest

    goto :EOF
)

:: Biên dịch ngầm nếu thư mục out chưa tồn tại
if not exist "%DIR%out" (
    echo [Nova CLI] Khoi tao build lan dau...
    mkdir "%DIR%out"
    %JAVAC_CMD% -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
)

%JAVA_CMD% -cp "%DIR%out" Main %*
endlocal
