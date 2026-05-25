[Setup]
AppName=Nova Programming Language
AppVersion=1.0.0
DefaultDirName={autopf}\Nova
DefaultGroupName=Nova
OutputDir=..\out
OutputBaseFilename=Nova-Setup
Compression=lzma
SolidCompression=yes
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
; ChangesEnvironment is important so Inno Setup broadcasts the WM_SETTINGCHANGE message
ChangesEnvironment=yes

[Files]
; Copy Nova JAR and batch scripts
Source: "..\out\production\nova.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\nova.bat"; DestDir: "{app}"; Flags: ignoreversion
; Copy bundled JRE (downloaded during CI)
Source: "..\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Nova Shell"; Filename: "cmd.exe"; Parameters: "/k ""{app}\nova.bat"""
Name: "{group}\Uninstall Nova"; Filename: "{uninstallexe}"

[Registry]
; Thêm biến môi trường PATH
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: expandsz; ValueName: "Path"; ValueData: "{olddata};{app}"; Check: NeedsAddPath(ExpandConstant('{app}'))

[Code]
function NeedsAddPath(Param: string): boolean;
var
  OrigPath: string;
begin
  if not RegQueryStringValue(HKEY_LOCAL_MACHINE,
    'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
    'Path', OrigPath)
  then begin
    Result := True;
    exit;
  end;
  // look for the path with leading and trailing semicolon
  // Pos() returns 0 if not found
  Result := Pos(';' + Param + ';', ';' + OrigPath + ';') = 0;
end;
