#!/bin/bash

# Path to the ProGuard usage file
USAGE_FILE="./app/build/outputs/mapping/freeRelease/usage.txt"

# Root directory of your project
PROJECT_ROOT="."
# Make sure the usage file exists
if [ ! -f "$USAGE_FILE" ]; then
  echo "Usage file not found: $USAGE_FILE"
  exit 1
fi

# Function to remove a class or method from a file
remove_code() {
  local file_path="$1"
  local pattern="$2"

  # Use sed to remove the class or method definition
  sed -i.bak "/$pattern/d" "$file_path"
}

# Read the usage file and process the listed classes and methods
while IFS= read -r line
do
  # Skip empty lines and comments
  if [[ -z "$line" || "$line" == \#* ]]; then
    continue
  fi

  # Extract the package, class, and method information
  package=$(echo "$line" | awk -F'.' '{OFS="."; $(NF--)=""; print}')
  class_or_method=$(echo "$line" | awk -F'.' '{print $NF}')

  # Check if the package starts with com.yogeshpaliyal
  if [[ "$package" != com.yogeshpaliyal* ]]; then
    continue
  fi

  # Handle constructor notation `<init>`
  if [[ "$class_or_method" == "<init>" ]]; then
    method_pattern=".*$class_or_method.*"
  else
    method_pattern="$class_or_method"
  fi

  # Find all Java and Kotlin files in the project directory, excluding build directories
  files=$(find "$PROJECT_ROOT" \( -name "*.java" -o -name "*.kt" \) -not -path "*/build/*")

  # Check each file for the package and method/class
  for file in $files; do
    if grep -q "package $package" "$file"; then
      echo "Removing $method_pattern from $file"
      remove_code "$file" "$method_pattern"
    fi
  done
done < "$USAGE_FILE"

echo "Dead code removal complete."
