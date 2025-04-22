#!/bin/bash

# Step 1: Get commit ID
commit_hash=$(git log --pretty=oneline | head -n2 | tail -n1 | cut -d' ' -f1)
echo "COMMIT HASH:"
echo "$commit_hash"

# Step 2: Get the list of changed files
changed_files=$(git diff "$commit_hash" --name-only | grep -v "~")
echo "CHANGED FILES:"
echo "$changed_files"

# Step 3: Extract the class names from the file paths
class_names=($(echo "$changed_files" | xargs -n 1 basename))
echo "CHANGED FILES NAMES:"
echo "${class_names[@]}"


# Step 4: Find files that relly on changed classes
CLASSES=()
# Step 4: Find files that relly on changed classes
for class in $class_names; do
   for file in $(grep -r -l --include="*.java" "${class%.java}" src/main/java/); do
     CLASSES+="$(basename "$file" .java) "
   done
done
echo "CLASSES THE USE CHANGED CLASSES:"
echo "${CLASSES[@]}"

#mapfile -t dependency_tree < <(./get_dependency_tree.sh)
# Step 5: For each class name, call get_hierarchy_classes.sh to get the hierarchy
#for class in "${class_names[@]}"; do
   # Use mapfile to capture multi-line output safely
#   mapfile -t temp_classes < <(./get_hierarchy_classes.sh "$class" "${dependency_tree[@]}" | sort | uniq)

   # Append to the main array
#   CLASSES+=("${temp_classes[@]}")
#done

echo "Find src files with path that contain extracted class names ..."
src_files_with_path=$(
	for class in "${class_names[@]}"; do 
		grep -rl "$class" src/main/*; 
	done)

# Extract the file name without the path and .java extension
for file in $src_files_with_path; do
    file_name=$(basename "$file" .java)
    CLASSES+=("$file_name")
done

# Step 5: Sort and remove duplicates
unique_classes=($(printf "%s\n" "${CLASSES[@]}" | sort | uniq))

# Print the sorted and unique values
printf "%s\n" "${unique_classes[@]}"


# Step 4: Find files that names start with class names that were modified
test_files=""

# Loop through each class name and find corresponding test files
for class_name in "${unique_classes[@]}"; do
    # Ensure no leading/trailing spaces in the class name
    class_name=$(echo "$class_name" | xargs)

    # Find matching files, case-insensitive, and ensure it matches the file names
    matching_files=$(find "src/test/java" -type f -iname "*${class_name}*.java")

    # Append found files to the test_files variable, each file on a new line
    if [[ -n "$matching_files" ]]; then
        test_files+="$matching_files"$'\n'
    fi
done

# Final output of all found test files
echo "Test files that start with extracted class names:"
echo -e "$test_files\n\n"


# Step 5: Sort and remove duplicates
unique_test_files=$(echo "$test_files" | sort | uniq)
echo "Sort and remove duplicates ..."
echo -e "$unique_test_files\n\n"


# Step 6: Extract the class names from the test files
final_files_names=$(echo "$unique_test_files" | sed -n 's|.*/\([^/]*\)\.java$|\1|p')
echo "Extract the class names from the test files ..."
echo -e "$final_files_names\n\n"

# Step 7: Combine the class names into a comma-separated list
test_files_to_run=$(echo "$final_files_names" | tr '\n' ',' | sed 's/,$//')
echo "Combine the class names into a comma-separated list ..."
echo -e "$test_files_to_run\n\n"


echo "Run Tests ..."
mvn clean test -Dtest="$test_files_to_run" -DfailIfNoTests=false


echo "The end!"
