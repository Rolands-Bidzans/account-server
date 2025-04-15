#!/bin/bash

# Ensure the target class is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <TargetClass> [DependencyChains...]"
    exit 1
fi

target="$1"
shift

# Capture dependency chains from arguments or stdin
if [ "$#" -gt 0 ]; then
    DEPENDENCY_CHAINS=("$@")
else
    while IFS= read -r line; do
        DEPENDENCY_CHAINS+=("$line")
    done
fi

# Iterate through dependency chains
#for chain in "${DEPENDENCY_CHAINS[@]}"; do
    # Check if the target class exists in the chain
#    if [[ "$chain" == *"$TARGET_CLASS"* ]]; then
        # Extract the part of the chain up to and including the target class
 #       PREFIX=$(echo "$chain" | sed -E "s/(.*$TARGET_CLASS).*/\1/")        

	# Get all classes in the chain and append them to the ALL_CLASSES variable
  #      classes=$(echo "$PREFIX" | tr ' ' '\n' | sort | uniq)

        # Append to ALL_CLASSES variable, avoiding duplicates
   #     for class in $classes; do
    #        if ! [[ "$ALL_CLASSES" =~ (^|[[:space:]])"$class"($|[[:space:]]) ]]; then
      #          ALL_CLASSES="$ALL_CLASSES$class"$'\n'
     #       fi
       # done
    #fi
#done


# Iterate through each dependency chain
for chain in "${DEPENDENCY_CHAINS[@]}"; do
    # Check if the target exists in the chain
    if [[ "$chain" == *" $target"* ]]; then
        # Split the chain into parts
        IFS=' ; ' read -r -a chain_parts <<< "$chain"

        # Check for trimming spaces
        for i in "${!chain_parts[@]}"; do
            chain_parts[$i]=$(echo "${chain_parts[$i]}" | xargs)  # Trim leading/trailing spaces
        done

	result=()

        # Find the target in the chain and process
        for ((i = 0; i < ${#chain_parts[@]}; i++)); do
            if [[ "${chain_parts[i]}" == "$target" ]]; then

                # Always include the target itself
                # result+=("${chain_parts[i]}")

                # Check for a predecessor (caller)
                if ((i > 0)); then
                    prev="${chain_parts[i - 1]}"   # Get the previous element

                    # Ensure no invalid previous values
                    if [[ -n "$prev" ]]; then
                        # If previous starts with 'I' (likely an interface)
                        if [[ "$prev" =~ ^I[A-Z] ]]; then
                            # Include two levels above (if they exist)
                            if ((i > 1)); then
                                result+=("${chain_parts[i-2]}")
				result+=("$prev")
                            else
                                result+=("$prev")
                            fi
                        else
                            # Include only the immediate predecessor
                            result+=("$prev")
                        fi
                    fi
                fi

		# Output data
		for element in "${result[@]}"; do
			echo "$element"
		done
            fi
        done
    fi
done
