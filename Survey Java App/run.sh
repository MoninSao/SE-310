#!/bin/bash
# run.sh — run the Survey System from the project root
# Usage: ./run.sh
# The JVM is launched from the project root so that relative paths
# (surveys/, responses/) resolve correctly regardless of where the
# shell is when the script is invoked.

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_ROOT"

if [ ! -d out ]; then
    echo "out/ directory not found. Run ./compile.sh first."
    exit 1
fi

java -cp out survey.Main
