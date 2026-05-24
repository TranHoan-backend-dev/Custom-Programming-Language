#!/bin/bash
echo "[Nova CLI] Dang cau hinh Git Hooks..."

if [ ! -d ".git" ]; then
    echo "[Nova CLI] Thu muc hien tai khong phai la Git repository (khong tim thay .git)."
    echo "[Nova CLI] Vui long chay 'git init' o thu muc goc cua du an!"
    exit 1
fi

mkdir -p .git/hooks

cat << 'EOF' > .git/hooks/pre-commit
#!/bin/sh
echo "[Nova CLI] Dang chay test-core truoc khi commit..."
./nova test-core || ./nova.bat test-core
if [ $? -ne 0 ]; then
  echo "✗ Commit bi huy bo do co test khong vuot qua!"
  exit 1
fi
EOF

chmod +x .git/hooks/pre-commit

echo "[Nova CLI] Da tao file .git/hooks/pre-commit thanh cong!"
echo "[Nova CLI] Tu gio tro di, moi khi ban 'git commit', du an se tu dong chay test."
