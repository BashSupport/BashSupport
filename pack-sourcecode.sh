#!/bin/sh

# Creates a tar.gz archive of the project's source code

TEMP=/tmp/bashsupport-src
SRC=/home/jansorg/Projekte/JavaProjekte/BashSupport-googlecode
DIRS="doc lib META-INF resources src test testdata"
FILES="BashSupport.iml BashSupport.ipr BashSupport.iws build.xml Changelog.txt LICENSE.txt NOTICE pack-sourcecode.sh README.txt TODO.txt"
REMOVE_PATTERNS="testdata/mockJDK Changelog.txt BashSupport.zip pack-sourcecode.sh doc"

rm -rf $TEMP
mkdir $TEMP

echo ${TEMP}

cd $TEMP
for d in $DIRS; do
    echo "Copying $d"
    cp -Ra $SRC/$d $TEMP
done

for f in $FILES; do
    echo "Copying $f"
    cp "$SRC/$f" $TEMP
done

for pattern in $REMOVE_PATTERNS; do
    echo "Removing $TEMP/$pattern"
    rm -rf "$TEMP/$pattern"
done

find $TEMP -iname .svn | xargs rm -rf

echo "Creating source code archive ..."
#tar --create -z --file=$SRC/BashSupport-src.tar.gz .
rm -f $SRC/BashSupport-src.zip
zip -r $SRC/BashSupport-src.zip .


