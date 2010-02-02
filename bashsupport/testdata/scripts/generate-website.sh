#!/bin/bash

#1 == lang, 2 == input dir
function createHtml() {
    export polang=$1
    export in=$2
    export XML_CATALOG_FILES="docbook/docbook-xsl/catalog.xml docbook/docbook-xsl/website/catalog.xml docbook/catalog.xml"

    rm -f tmp/$polang/autolayout.xml
    xsltproc --nonet --output tmp/$polang/autolayout.xml docbook/custom-xsl/autolayout.xsl $in/layout.xml
    xsltproc --nonet --path "$in" --stringparam output-root website-generated/$polang \
        --stringparam l10n.gentext.default.language $polang  \
        docbook/custom-xsl/website.xsl tmp/$polang/autolayout.xml
}

function createTranslation {
    export POLANG=$1
    for f in "$(cd content/de/; ls *.xml)"; do echo "$f"; xml2po -p lang/$POLANG.po content/de/$f > tmp/$POLANG/$f; done
}

function fixTranslation {
    POLANG=b

    files="tmp/$POLANG/snippet_subscribe101.xml tmp/$POLANG/contact.xml tmp/$POLANG/snippet_subscribehandybirthdays.xml"
    for f in $files; do echo "Prostprocessing $f..."; cat $f | sed -e 's/<label/<html:label/' |
        sed -e 's/<\/label/<\/html:label/' |
        sed -e 's/<input/<html:input/' |
        sed -e 's/<\/input/<\/html:input/' |
        sed -e 's/<option/<html:option/' |
        sed -e 's/<\/option/<\/html:option/' |
        sed -e 's/<button/<html:button/' |
        sed -e 's/<\/button/<\/html:button/' > "$f"; done;

    #Fix all files
    for f in tmp/$POLANG/product*.xml; do cat $f | sed -e 's/-de\.jpg/-en.jpg/' > $f; done;
}

function copyResources() {
    cp -Raf website-common/* website-generated/
    find website-generated/ -iname \.svn | xargs rm -rf
}

function createSitemap {
#    Generate the languages files
    echo "<languages>" > tmp/languages.xml
    echo "<language name=\"de\" file=\"de/autolayout.xml\"/>" >> tmp/languages.xml
    echo "<language name=\"en\" file=\"en/autolayout.xml\"/>" >> tmp/languages.xml
    echo "</languages>" >> tmp/languages.xml

    #Generate the sitemap.xml xml file for the search engines
    ./generate-sitemap.sh
}

function createRSS {
    ./generate-rss.sh
}

# THis is the main part of the application

createHtml de content/en
createTranslation "en"
fixTranslation "en"
createHtml en tmp/en/
createSitemap
createRSS

copyResources

#END
