#!/bin/bash
echo "[Nova CLI] Dang dong goi phien ban phan phoi (Distribution)..."

# Xoa va tao lai thu muc dist
rm -rf dist
mkdir dist

# Build source
./nova build

# Tao file jar
echo "[Nova CLI] Dang tao file nova-lang.jar..."
jar cfe dist/nova-lang.jar Main -C out .

# Tao file nova.bat cho phan phoi (Windows users)
cat << 'EOF' > dist/nova.bat
@echo off
java --enable-preview -jar "%~dp0nova-lang.jar" %*
EOF

# Tao file nova cho bash/linux (Linux/Mac users)
cat << 'EOF' > dist/nova
#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java --enable-preview -jar "$DIR/nova-lang.jar" "$@"
EOF

chmod +x dist/nova

echo "[Nova CLI] Dong goi thanh cong!"
echo "[Nova CLI] Toan bo file can thiet nam trong thu muc 'dist'."
echo "[Nova CLI] Ban co the zip thu muc 'dist' de phan phoi cho nguoi dung khac."
