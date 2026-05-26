@echo off
echo [Nova CLI] Dang dong goi phien ban phan phoi (Distribution)...

:: Xoa va tao lai thu muc dist
if exist dist rmdir /s /q dist
mkdir dist

:: Build source
call nova.bat build

:: Tao file jar
echo [Nova CLI] Dang tao file nova-lang.jar...
jar cfe dist\nova-lang.jar Main -C out .

:: Tao file nova.bat cho phan phoi
echo @echo off > dist\nova.bat
echo java --enable-preview -jar "%%~dp0nova-lang.jar" %%* >> dist\nova.bat

:: Tao file nova cho bash/linux
echo #!/bin/bash > dist\nova
echo DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" >> dist\nova
echo java --enable-preview -jar "$DIR/nova-lang.jar" "$@" >> dist\nova

echo [Nova CLI] Dong goi thanh cong!
echo [Nova CLI] Toan bo file can thiet nam trong thu muc 'dist'.
echo [Nova CLI] Ban co the zip thu muc 'dist' de phan phoi cho nguoi dung khac.
