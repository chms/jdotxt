#!/bin/sh

TEMP_DIR="tmp"
PACKAGE_NAME="jdotxt"
PACKAGE_VERSION="0.1"
DEBIAN_FOLDER="debian"

ant

mkdir -p $TEMP_DIR/DEBIAN
mkdir -p $TEMP_DIR/usr/lib/$PACKAGE_NAME
mkdir -p $TEMP_DIR/usr/bin

cp jar/jdotxt.jar $TEMP_DIR/usr/lib/$PACKAGE_NAME/
echo "java -jar /usr/lib/jdotxt/jdotxt.jar" > $TEMP_DIR/usr/bin/$PACKAGE_NAME

echo "Package: $PACKAGE_NAME" > $TEMP_DIR/DEBIAN/control
echo "Version: $PACKAGE_VERSION" >> $TEMP_DIR/DEBIAN/control
cat $DEBIAN_FOLDER/control >> $TEMP_DIR/DEBIAN/control

PACKAGE_SIZE=`du -bs $TEMP_DIR | cut -f 1`
PACKAGE_SIZE=$((PACKAGE_SIZE/1024))
echo "Installed-Size: $PACKAGE_SIZE" >> $TEMP_DIR/DEBIAN/control

#chown -R root $TEMP_DIR/
#chgrp -R root $TEMP_DIR/
chmod 775 $TEMP_DIR/usr/bin/$PACKAGE_NAME

fakeroot dpkg --build $TEMP_DIR

mv $TEMP_DIR.deb "$DEBIAN_FOLDER/$PACKAGE_NAME-$PACKAGE_VERSION.deb"

rm -rf $TEMP_DIR

