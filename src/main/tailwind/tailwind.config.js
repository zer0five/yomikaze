/** @type {import('tailwindcss').Config} */
module.exports = {
    plugins: [require("daisyui")],
    content: [
        "../webapp/**/*.html",
        "../resources/templates/**/*.html",
    ],
    daisyui: {
        darkTheme: "ayu-dark",
        themes: [
            {
                "ayu-light": {
                    "primary": "#55B4D4",
                    "secondary": "#6CBF43",
                    "accent": "#FFAA33",
                    "neutral": "#ACB6BF",
                    "base-100": "#FCFCFC",
                    "base-200": "#F3F4F5",
                    "base-300": "#F8F9FA",
                    "info": "#399EE6",
                    "success": "#6CBF43",
                    "warning": "#F2AE49",
                    "error": "#E65050",
                },
                "ayu-mirage": {
                    "primary": "#5CCFE6",
                    "secondary": "#87D96C",
                    "accent": "#FFCC66",
                    "neutral": "#B8CFE6",
                    "base-100": "#242936",
                    "base-200": "#1F2430",
                    "base-300": "#1C212B",
                    "info": "#73D0FF",
                    "success": "#87D96C",
                    "warning": "#FFD173",
                    "error": "#FF6666",
                },
                "ayu-dark": {
                    "primary": "#39BAE6",
                    "secondary": "#7FD962",
                    "accent": "#E6B450",
                    "neutral": "#ACB6BF",
                    "base-100": "#0F131A",
                    "base-200": "#0D1017",
                    "base-300": "#0B0E14",
                    "info": "#59C2FF",
                    "success": "#7FD962",
                    "warning": "#FFB454",
                    "error": "#D95757",
                },
            },
        ],
    },
}
