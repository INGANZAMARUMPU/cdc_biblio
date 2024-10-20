# Define the old and new extensions
oldExtension=".jsp"
newExtension=".xhtml"

# Find and rename files in subfolders
find . -type f -name "*$oldExtension" | while read file; do
    mv "$file" "${file%$oldExtension}$newExtension"
done
