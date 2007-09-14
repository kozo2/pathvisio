; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
AppName=PathVisio
AppVerName=PathVisio 1.0
AppPublisher=BiGCaT group, University of Maastricht
AppPublisherURL=http://www.genmapp.org
AppSupportURL=http://www.genmapp.org
AppUpdatesURL=http://www.genmapp.org
DefaultDirName={pf}\PathVisio
DefaultGroupName=PathVisio
AllowNoIcons=yes
OutputBaseFilename=PathVisio-Setup
Compression=lzma
SolidCompression=yes
OutputDir=dist


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "pathvisio.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "swt-win32-3232.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "swt-awt-win32-3232.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "swt-gdip-win32-3232.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "swt-wgl-win32-3232.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "gdiplus.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "images\protein.bmp"; DestDir: "{app}\images"; Flags: ignoreversion
Source: "images\logo.jpg"; DestDir: "{app}\images"; Flags: ignoreversion
Source: "images\logo-fullsize.png"; DestDir: "{app}\images"; Flags: ignoreversion
Source: "images\logo-tiny.jpg"; DestDir: "{app}\images"; Flags: ignoreversion
Source: "images\mRNA.bmp"; DestDir: "{app}\images"; Flags: ignoreversion
Source: "icons\minimize.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\close.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\select.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\colorset.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\edit.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\legend.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\new.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newarc.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newarrow.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newbrace.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newdashedarrow.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newdashedline.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newgeneproduct.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newlabel.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newligandround.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newligandsquare.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newline.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newlinemenu.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newlineshapemenu.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newoval.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newreceptorround.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newreceptorsquare.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newrectangle.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\newtbar.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\open.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\save.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\sample_checked.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "icons\sample_unchecked.gif"; DestDir: "{app}\icons"; Flags: ignoreversion
Source: "backpage\header.html"; DestDir: "{app}\backpage"; Flags: ignoreversion
Source: "lib\hsqldb.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\jdom.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\JRI.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\jri.dll"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\org.eclipse.swt.win32.win32.x86_3.2.0.v3232m.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\org.eclipse.swt_3.2.0.v3232o.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\org.eclipse.jface_3.2.0.I20060605-1400.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\org.eclipse.core.commands_3.2.0.I20060605-1400.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "lib\org.eclipse.equinox.common_3.2.0.v20060603.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\PathVisio"; Filename: "{app}\pathvisio.jar"; WorkingDir: "{app}"
Name: "{group}\{cm:UninstallProgram,PathVisio}"; Filename: "{uninstallexe}"
Name: "{userdesktop}\PathVisio"; Filename: "{app}\pathvisio.jar"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}\pathvisio.exe"; Description: "{cm:LaunchProgram,PathVisio}"; Flags: nowait postinstall skipifsilent

