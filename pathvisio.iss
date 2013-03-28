; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

; requires ISPP (InnoSetup Pre-Processor) to be installed.
#define AppVerStr "3.0.0"

[Setup]
AppName=PathVisio
AppVerName=PathVisio {#AppVerStr}
AppPublisher=BiGCaT Bioinformatics, University of Maastricht
AppPublisherURL=http://www.genmapp.org
AppSupportURL=http://www.pathvisio.org
AppUpdatesURL=http://www.pathvisio.org
DefaultDirName={pf}\PathVisio
DefaultGroupName=PathVisio
AllowNoIcons=yes
OutputBaseFilename=PathVisio-{#AppVerStr}-Setup
Compression=lzma
SolidCompression=yes
OutputDir=release
ChangesAssociations=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "pathvisio.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "pvicon.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "pathvisio.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "readme.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "LICENSE-2.0.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "NOTICE.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "CONTRIBUTORS.txt"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\PathVisio"; Filename: "{app}\pathvisio.bat"; IconFilename: "{app}\pvicon.ico";
Name: "{group}\{cm:UninstallProgram,PathVisio}"; Filename: "{uninstallexe}"
Name: "{userdesktop}\PathVisio"; Filename: "{app}\pathvisio.bat"; IconFilename: "{app}\pvicon.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\pathvisio.bat"; Description: "{cm:LaunchProgram,PathVisio}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKCR; Subkey: ".gpml"; ValueType: string; ValueName: ""; ValueData: "GpmlPathway"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "GpmlPathway"; ValueType: string; ValueName: ""; ValueData: "GPML Pathway for PathVisio"; Flags: uninsdeletekey
Root: HKCR; Subkey: "GpmlPathway\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\pvicon.ico"
Root: HKCR; Subkey: "GpmlPathway\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\pathvisio.bat"" ""%1"""
