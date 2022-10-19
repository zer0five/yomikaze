#!/bin/sh

npx tailwindcss build \
    --config ./tailwind/tailwind.config.js \
    --content "./{webapp,resources/templates}/**/*.{html,js}" \
    --input ./tailwind/tailwind.input.css \
    --output ./webapp/asset/css/tailwind.min.css \
    --watch \
    --verbose
