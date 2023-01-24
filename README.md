
# Develop
## Windows
https://medium.com/graalvm/using-graalvm-and-native-image-on-windows-10-9954dc071311  
### Build
see 'C:\Program Files\Microsoft Visual Studio\2022\Community\Developer PowerShell for VS 2022.lnk'

example build command
```shell
pwsh.exe -noe -c "&{Import-Module """C:\Program Files\Microsoft Visual Studio\2022\Community\Common7\Tools\Microsoft.VisualStudio.DevShell.dll"""; Enter-VsDevShell 963dc767 -DevCmdArguments -arch=x64;}" & ./gradlew nativeCompile
```
