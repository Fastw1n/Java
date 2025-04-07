set sf="info/kgeorgiy/ja/belugan/implementor"
set ap="../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar"

cd ..

javac  -cp %ap% java-solutions/%sf%/

cd java-solutions/

jar --create --manifest=../scripts/MANIFEST.MF --file=../scripts/implementor.jar %sf%/*.class

rmdir %sf%/*.class

cd ../scripts