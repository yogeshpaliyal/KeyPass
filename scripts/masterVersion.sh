#!/bin/bash

# Read the current versionCode and versionName from the build.gradle.kts.kts file
VERSION_NAME=$(grep "versionName" app/build.gradle | awk '{print $2}' | tr -d '\r''"')


NEW_VERSION_NAME="$VERSION_NAME-master"

# Update the build.gradle.kts.kts file with the new versionCode and versionName values
sed -i "s/versionName \"$VERSION_NAME\"/versionName \"$NEW_VERSION_NAME\"/" app/build.gradle

# Output the new versionCode and versionName values
echo "v$NEW_VERSION_NAME"
