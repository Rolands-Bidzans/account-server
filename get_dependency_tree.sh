#!/bin/bash
DEPENDENCY_CHAINS=()

ALL_TAGS=$(find . -type f -name "*Controller.java")
TAGS_NAMES=$(echo $ALL_TAGS | xargs -n1 basename | sed 's/.java$//')
MAIN_DIR=($(find . -type d -path "*/src/main"))

# Function to extract dependencies from the 'tags' file
extract_dependencies() {
    local target=$1
    grep "$target" tags | awk '{
        if ($1 ~ /^[a-z]/) {
            first = toupper(substr($1, 1, 1));
            rest = substr($1, 2);
            print first rest;
        } else {
            print $1;
        }
    }' | grep -v '\.' | grep -v "^$target$" | sort | uniq
}

form_dependencies() {
 local SEARCH_DIR=$1
 # Generate the ctags for the search directory
 ctags -R --languages=java --java-kinds=cfi --fields=+l --extras=+q "$SEARCH_DIR"

 # Process each tag
 for TAG in $TAGS_NAMES; do

    # Initialize a queue of tuples (tag, current chain)
    queue=("$TAG;$TAG")

    # Process the queue
    while [ ${#queue[@]} -gt 0 ]; do
        # Dequeue the first item (split tag and chain)
        IFS=";" read -r current_tag current_chain <<< "${queue[0]}"
        queue=("${queue[@]:1}")

        # Extract dependencies for the current tag
        dependencies=$(extract_dependencies "$current_tag")

        # If dependencies are found, process each one
        if [[ -n "$dependencies" ]]; then
            while IFS= read -r next_dep; do

                # Avoid circular dependencies
                if [[ "$current_chain" == *",$next_dep"* ]]; then
                    continue
                fi

                # Create a new chain for this branch
                new_chain="$current_chain ; $next_dep"

                # Enqueue the next dependency and its chain
                queue+=("$next_dep;$new_chain")

            done <<< "$dependencies"
        else
            # Store the completed chain (leaf node)
            DEPENDENCY_CHAINS+=("$current_chain")
        fi
    done
  done
}

for path in "${MAIN_DIR[@]}"; do
   form_dependencies $path
done

# Print updated array
for chain in "${DEPENDENCY_CHAINS[@]}"; do
    echo "$chain"
done
