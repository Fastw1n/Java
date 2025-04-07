@echo off
echo Generate javadoc

set linkpath=https://docs.oracle.com/en/java/javase/20/docs/api/
set sp="../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar"
set fp="../java-solutions/info/kgeorgiy/ja/belugan/implementor/Implementor.java"

javadoc -link  %linkpath% -d javadoc -private -cp  %sp% %fp%
pause