#!/bin/bash
# compile.sh — compile the Survey System from the project root
# Usage: ./compile.sh
# Output: class files placed in ./out/

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_ROOT"

mkdir -p out

# Collect all .java files under src/ (works on any OS that has bash + find)
SOURCES=$(find src -name "*.java")

if [ -z "$SOURCES" ]; then
    echo "No .java files found under src/."
    exit 1
fi

javac -d out $SOURCES
echo "Compilation successful. Class files written to: out/"
