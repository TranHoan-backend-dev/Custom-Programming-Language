@echo off
setlocal
set DIR=%~dp0

:: Tìm thư mục chứa javac (phiên bản 17) để sử dụng đúng java.exe tương ứng
set JAVA_CMD=java
set JAVAC_CMD=javac
for %%I in (javac.exe) do set JAVAC_PATH=%%~dp$PATH:I
if defined JAVAC_PATH (
    set JAVA_CMD="%JAVAC_PATH%java.exe"
    set JAVAC_CMD="%JAVAC_PATH%javac.exe"
)

:: Kiểm tra lệnh build
if "%~1"=="build" (
    echo [Nova CLI] Dang bien dich ma nguon Interpreter...
    if not exist "%DIR%out" mkdir "%DIR%out"
    %JAVAC_CMD% -encoding UTF-8 -d "%DIR%out" -sourcepath "%DIR%src" "%DIR%src\Main.java"
    echo [Nova CLI] Build hoan tat!
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
