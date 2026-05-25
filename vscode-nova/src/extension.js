const vscode = require('vscode');

/**
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
    let disposable = vscode.commands.registerCommand('nova.run', function () {
        const editor = vscode.window.activeTextEditor;
        if (!editor) {
            vscode.window.showErrorMessage('No active file to run!');
            return;
        }

        const document = editor.document;
        if (document.languageId !== 'nova') {
            vscode.window.showErrorMessage('Active file is not a Nova file!');
            return;
        }

        document.save().then(() => {
            const filePath = document.fileName;
            
            // Re-use existing terminal named "Nova" or create a new one
            let terminal = vscode.window.terminals.find(t => t.name === 'Nova');
            if (!terminal) {
                terminal = vscode.window.createTerminal('Nova');
            }
            
            terminal.show();
            // Wrap filePath in quotes to handle spaces in path
            terminal.sendText(`nova "${filePath}"`);
        });
    });

    context.subscriptions.push(disposable);
}

function deactivate() {}

module.exports = {
    activate,
    deactivate
}
