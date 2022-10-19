#!/bin/sh

TAILWIND_BASE_DIR="./"
TAILWIND_CONFIG_FILE="$TAILWIND_BASE_DIR/tailwind.config.js"
TAILWIND_INPUT_FILE="$TAILWIND_BASE_DIR/tailwind.input.css"
TAILWIND_OUTPUT_DIR="../webapp/asset/css"
TAILWIND_OUTPUT_FILE_MIN="$TAILWIND_OUTPUT_DIR/tailwind.min.css"
TAILWIND_OUTPUT_FILE="$TAILWIND_OUTPUT_DIR/tailwind.css"

npx tailwindcss build \
    --config $TAILWIND_CONFIG_FILE \
    --input $TAILWIND_INPUT_FILE \
    --output $TAILWIND_OUTPUT_FILE \
    --watch &

npx tailwindcss build \
    --config $TAILWIND_CONFIG_FILE \
    --input $TAILWIND_INPUT_FILE \
    --output $TAILWIND_OUTPUT_FILE_MIN \
    --watch \
    --minify &

