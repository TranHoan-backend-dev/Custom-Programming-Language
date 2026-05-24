#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "[Nova CLI] Dang cai dat Nova vao ~/.bashrc va ~/.zshrc..."

add_to_path() {
    local rc_file=$1
    if [ -f "$rc_file" ]; then
        if ! grep -q "$DIR" "$rc_file"; then
            echo "export PATH=\"\$PATH:$DIR\"" >> "$rc_file"
            echo "[Nova CLI] Da them vao $rc_file"
        else
            echo "[Nova CLI] Thu muc da ton tai trong $rc_file"
        fi
    fi
}

add_to_path "$HOME/.bashrc"
add_to_path "$HOME/.zshrc"

# Set executable permissions
chmod +x "$DIR/nova"
chmod +x "$DIR/install.sh"
chmod +x "$DIR/build-dist.sh"

echo "[Nova CLI] Cai dat thanh cong!"
echo "[Nova CLI] Vui long chay 'source ~/.bashrc' (hoac ~/.zshrc) hay khoi dong lai Terminal."
echo "[Nova CLI] Bay gio ban co the go 'nova init' o bat ky dau."
