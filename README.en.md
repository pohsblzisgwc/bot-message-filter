# Bot Message Filter

**AI disclosure: This project was developed entirely by OpenAI Codex.**

[简体中文](README.md)

A Minecraft Java 26.3 Fabric client mod that automatically hides join, leave and death messages for suspected groups of numbered fake players. It is independent of Fake Player TBM and Carpet, and changes only the local client's display.

## Detection

During a server connection, the mod observes online player names and vanilla join messages. Names with the same prefix and a trailing number are grouped automatically; for example, `TBM_1` and `TBM_2` belong to one group. No fixed prefix is required. Once ten different numeric suffixes have been observed in a group, join, leave and death messages for names matching that group are hidden, including numbers not previously observed. Names must meet Minecraft's player-name character and length limits. Prefix matching is case-insensitive and leading zeroes do not count as a different number.

Messages displayed before a group qualifies are not removed retroactively. Detection resets on disconnect; the mod learns again from online players and subsequent joins. Because detection is based on name structure, real players with a shared numbered prefix can also be classified as a group.

Only vanilla translation-component join, leave and death messages are filtered, checking the player who died rather than the killer. Ordinary chat, the player list and the action bar remain visible. Plain-text or custom messages from servers or other mods may not be recognised. Network data and server logs are not modified, and other mods can still read online-player information.

## Installation

Place `build/libs/bot-message-filter-1.0.0.jar` in the `mods` folder of a Minecraft 26.3 Fabric instance and install Fabric API. Java 25 and Fabric Loader 0.19.5 or later are required. Install this mod only on the client.

## Build and test

Set `MC_DIR` to the Minecraft game directory and run `./build.sh`; `./test.sh` builds and runs filtering tests. The scripts require JDK 25 and the instance's Minecraft, Fabric Loader and Fabric API files. The tests do not replace in-game validation.

This project is licensed under the [MIT License](LICENSE).
