@echo off
setlocal enabledelayedexpansion

echo [Nova CLI] Dang cai dat Nova vao bien moi truong PATH...
echo [Nova CLI] (Hien tai script nay ho tro User PATH tren Windows)

:: Kiem tra xem duong dan hien tai da co trong PATH chua
echo %PATH% | findstr /C:"%~dp0" >nul
if %errorlevel%==0 (
    echo [Nova CLI] Thu muc %~dp0 da ton tai trong PATH!
    exit /b 0
)

:: Su dung PowerShell de them vao User PATH an toan, khong bi gioi han 1024 ky tu nhu lenh setx
powershell -Command "$userPath = [Environment]::GetEnvironmentVariable('PATH', 'User'); if ($userPath -notmatch [regex]::Escape('%~dp0')) { [Environment]::SetEnvironmentVariable('PATH', $userPath + ';%~dp0', 'User') }"

echo [Nova CLI] Cai dat thanh cong!
echo [Nova CLI] Vui long khoi dong lai Terminal (hoac mo tab moi) de ap dung thay doi.
echo [Nova CLI] Bay gio ban co the go 'nova init' hoac 'nova run' o bat ky thu muc nao!
