#!/bin/bash

# Create a Morfologik spelling dictionary from a Hunspell dictionary.
#
# LanguageTool 6.8-SNAPSHOT
#
# Run this script from the LanguageTool top-level directory.
#
# Usage:
#   ./create_dict.sh lg UG
# I just have this but it failed to work, you can check the official repo of Luganda Hunspell dictionary to find the working java program> I think it's called chunkedmorfologikbuilder.java

set -e

echo "Create Morfologik spelling dictionary, based on Hunspell dictionary"
echo "LanguageTool version: 6.8-SNAPSHOT"
echo ""

if [ $# -ne 2 ]; then
    SCRIPT=$(basename "$0")
    echo "Usage: $SCRIPT <langCode> <countryCode>"
    echo "Example: $SCRIPT lg UG"
    exit 1
fi

LANG_CODE="$1"
COUNTRY_CODE="$2"
PREFIX="${LANG_CODE}_${COUNTRY_CODE}"

CONTENT_DIR="languagetool-language-modules/${LANG_CODE}/src/main/resources/org/languagetool/resource/${LANG_CODE}/hunspell"

DIC_NO_SUFFIX="${CONTENT_DIR}/${PREFIX}"
DIC_FILE="${DIC_NO_SUFFIX}.dic"
AFF_FILE="${CONTENT_DIR}/${PREFIX}.aff"
INFO_FILE="${CONTENT_DIR}/${PREFIX}.info"

TEMP_FILE="/tmp/lt-${PREFIX}-dictionary.dump"
FINAL_FILE="/tmp/lt-${PREFIX}-dictionary.new"
OUTPUT_FILE="/tmp/${PREFIX}.dict"

# Use the Windows Maven repository because the dependencies were
# installed by the Windows Maven installation.
REPO="/c/Users/JSQUARE/.m2/repository"

TOOLS_JAR="languagetool-tools/target/languagetool-tools-6.8-SNAPSHOT.jar"

# LanguageTool tools
CPATH="$TOOLS_JAR"

# HPPC
CPATH="$CPATH:$REPO/com/carrotsearch/hppc/0.8.2/hppc-0.8.2.jar"

# Morfologik 2.1.9
CPATH="$CPATH:$REPO/org/carrot2/morfologik-fsa/2.1.9/morfologik-fsa-2.1.9.jar"
CPATH="$CPATH:$REPO/org/carrot2/morfologik-fsa-builders/2.1.9/morfologik-fsa-builders-2.1.9.jar"
CPATH="$CPATH:$REPO/org/carrot2/morfologik-stemming/2.1.9/morfologik-stemming-2.1.9.jar"
CPATH="$CPATH:$REPO/org/carrot2/morfologik-tools/2.1.9/morfologik-tools-2.1.9.jar"

# Commons CLI
CPATH="$CPATH:$REPO/commons-cli/commons-cli/1.9.0/commons-cli-1.9.0.jar"

echo "Language:       $LANG_CODE"
echo "Country:        $COUNTRY_CODE"
echo "Dictionary:     $DIC_FILE"
echo "Affix file:     $AFF_FILE"
echo "Output:         $OUTPUT_FILE"
echo ""

# ------------------------------------------------------------
# Check required files
# ------------------------------------------------------------

if [ ! -f "$DIC_FILE" ]; then
    echo "ERROR: Dictionary not found:"
    echo "$DIC_FILE"
    exit 1
fi

if [ ! -f "$AFF_FILE" ]; then
    echo "ERROR: Affix file not found:"
    echo "$AFF_FILE"
    exit 1
fi

if [ ! -f "$TOOLS_JAR" ]; then
    echo "ERROR: LanguageTool tools JAR not found:"
    echo "$TOOLS_JAR"
    exit 1
fi

# ------------------------------------------------------------
# Check Java
# ------------------------------------------------------------

if ! command -v java >/dev/null 2>&1; then
    echo "ERROR: java was not found in PATH."
    exit 1
fi

# ------------------------------------------------------------
# Check required dependency JARs
# ------------------------------------------------------------

REQUIRED_JARS=(
    "$TOOLS_JAR"
    "$REPO/com/carrotsearch/hppc/0.8.2/hppc-0.8.2.jar"
    "$REPO/org/carrot2/morfologik-fsa/2.1.9/morfologik-fsa-2.1.9.jar"
    "$REPO/org/carrot2/morfologik-fsa-builders/2.1.9/morfologik-fsa-builders-2.1.9.jar"
    "$REPO/org/carrot2/morfologik-stemming/2.1.9/morfologik-stemming-2.1.9.jar"
    "$REPO/org/carrot2/morfologik-tools/2.1.9/morfologik-tools-2.1.9.jar"
    "$REPO/commons-cli/commons-cli/1.9.0/commons-cli-1.9.0.jar"
)

for JAR in "${REQUIRED_JARS[@]}"; do
    if [ ! -f "$JAR" ]; then
        echo "ERROR: Required JAR not found:"
        echo "$JAR"
        exit 1
    fi
done

# ------------------------------------------------------------
# Verify SpellDictionaryBuilder
# ------------------------------------------------------------

echo "Checking SpellDictionaryBuilder..."

if ! jar tf "$TOOLS_JAR" | grep -q 'org/languagetool/tools/SpellDictionaryBuilder.class'; then
    echo "ERROR: SpellDictionaryBuilder.class not found."
    exit 1
fi

echo "SpellDictionaryBuilder: OK"
echo ""

# ------------------------------------------------------------
# Generate all Hunspell forms
# ------------------------------------------------------------

echo "Running unmunch..."
echo ""

unmunch "$DIC_FILE" "$AFF_FILE" |
    grep -v "^#" |
    hunspell -d "$DIC_NO_SUFFIX" -G -l > "$TEMP_FILE"

echo ""
echo "Generated forms:"
wc -l "$TEMP_FILE"

# ------------------------------------------------------------
# Sort and remove duplicates
# ------------------------------------------------------------

echo ""
echo "Sorting and removing duplicates..."

sed 's/\r//' "$TEMP_FILE" |
    sort -u > "$FINAL_FILE"

echo ""
echo "Final word count:"
wc -l "$FINAL_FILE"

# ------------------------------------------------------------
# Build Morfologik dictionary
# ------------------------------------------------------------

echo ""
echo "Creating Morfologik dictionary..."
echo ""

java -cp "$CPATH" \
    org.languagetool.tools.SpellDictionaryBuilder \
    -i "$FINAL_FILE" \
    -info "$INFO_FILE" \
    -o "$OUTPUT_FILE"

echo ""
echo "========================================"
echo "Done."
echo "========================================"
echo ""
echo "Morfologik dictionary:"
echo "$OUTPUT_FILE"
echo ""

rm -f "$TEMP_FILE"