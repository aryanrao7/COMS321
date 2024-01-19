#!/bin/bash

# Remove all .class files in the current directory
find . -name "*.class" -type f -delete

echo "Cleaned all .class files."
