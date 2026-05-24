@echo off
echo [Nova CLI] Dang cau hinh Git Hooks...

if not exist .git (
    echo [Nova CLI] Thu muc hien tai khong phai la Git repository (khong tim thay .git^).
    echo [Nova CLI] Vui long chay 'git init' o thu muc goc cua du an (noi chua code Nova^)!
    exit /b 1
)

if not exist .git\hooks mkdir .git\hooks

echo #!/bin/sh > .git\hooks\pre-commit
echo echo "[Nova CLI] Dang chay test-core truoc khi commit..." >> .git\hooks\pre-commit
:: Kiem tra ca 2 kieu goi: nova hoac nova.bat
echo ./nova test-core ^|^| ./nova.bat test-core >> .git\hooks\pre-commit
echo if [ $? -ne 0 ]; then >> .git\hooks\pre-commit
echo   echo "✗ Commit bi huy bo do co test khong vuot qua!" >> .git\hooks\pre-commit
echo   exit 1 >> .git\hooks\pre-commit
echo fi >> .git\hooks\pre-commit

echo [Nova CLI] Da tao file .git/hooks/pre-commit thanh cong!
echo [Nova CLI] Tu gio tro di, moi khi ban 'git commit', du an se tu dong chay test.
