[Setup]
; Basic Setup Information
AppName=Historical Artifact Catalog
AppVersion=1.0
DefaultDirName={pf}\Historical Artifact Catalog
DefaultGroupName=Historical Artifact Catalog
OutputDir=.\Output
OutputBaseFilename=HistoricalArtifactCatalog
DisableDirPage=no
DisableProgramGroupPage=no
Uninstallable=yes
ShowLanguageDialog=yes

[Files]
; Copy everything inside the javafx-sdk-24.0.1 folder to the installation directory
Source: "C:\Users\hmaca\Desktop\JUSTINCASE\HistoricalArtifactCatalog.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\hmaca\Desktop\JUSTINCASE\javafx-sdk-24.0.1\*"; DestDir: "{app}\javafx-sdk-24.0.1"; Flags: ignoreversion recursesubdirs
Source: "C:\Users\hmaca\Desktop\JUSTINCASE\jdk-24.0.1+9-jre\*"; DestDir: "{app}\jdk-24.0.1+9-jre"; Flags: ignoreversion recursesubdirs
Source: "C:\Users\hmaca\Desktop\JUSTINCASE\run.bat"; DestDir: "{app}"; Flags: ignoreversion

[Run]
Filename: "{app}\run.bat"; WorkingDir: "{app}"; Flags: nowait postinstall skipifsilent

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Additional icons:"

[Icons]
Name: "{group}\Historical Artifact Catalog"; Filename: "{app}\run.bat"
Name: "{commondesktop}\Historical Artifact Catalog"; Filename: "{app}\run.bat"; Tasks: desktopicon
