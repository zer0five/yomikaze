#!/bin/bash

tailwind_build() {
    npx tailwindcss build \
        --config "${TAILWIND_CONFIG_FILE}" \
        --input "${TAILWIND_INPUT_FILE}" \
        --output "${TAILWIND_OUTPUT_FILE_MIN}" \
        --minify
}

TAILWIND_BASE_DIR=$(realpath "$(dirname "$0")")
pushd "$TAILWIND_BASE_DIR" || exit
TAILWIND_CONFIG_FILE="$TAILWIND_BASE_DIR/tailwind.config.js"
TAILWIND_INPUT_FILE="$TAILWIND_BASE_DIR/tailwind.input.css"
TAILWIND_OUTPUT_DIR="../resources/static/asset/css"
TAILWIND_OUTPUT_FILE_MIN="$TAILWIND_OUTPUT_DIR/tailwind.min.css"
#TAILWIND_OUTPUT_FILE="$TAILWIND_OUTPUT_DIR/tailwind.css"

tailwind_build
popd || exit
