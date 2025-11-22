# Snake Game (Personal Project)

A single file Swing implementation of the classic Snake game with a styled home screen, palette picker, and saved high scores. No external libraries beyond the JDK.

## Features
- Home screen with gradient background, rounded buttons, and Color Carnival palette picker (Neon Nibbles, Bubblegum Breeze, Jungle Jive).
- Smooth grid based movement on a 600x600 board (30x30 tiles at 20 px each) with overlays for pause and game over.
- Apple spawning always avoids the snake; score increases by 1 per apple and speed ramps up every 5 points down to a 50 ms delay.
- Arrow keys / WASD controls with guarded direction changes to avoid instant self collisions.
- Pause/resume, in game restart confirmation, and exit to menu confirmation; keyboard focus handled for quick restarts.
- Local high scores (top 5 unique values) written to `highscore.txt` in the working directory.

## Controls
- Move: Arrow keys or WASD
- Pause/Resume: P
- Restart: R (confirms while a game is active)
- Exit to home: Esc (confirms while a game is active)

## How to Play
- Eat apples to grow and increase your score; hitting a wall or your own tail ends the run.
- Speed increases as you score, so plan ahead when steering.
- Use the home screen to tweak the color palette via "Color Carnival."

## Build and Run
Requires JDK 17+ (uses modern switch syntax).

- Compile: `javac SnakeGame.java`
- Run from classes: `java SnakeGame`
- Or run the packaged jar (included): `java -jar SnakeGame.jar`

## Files
- `SnakeGame.java` - source code (UI, game loop, high score persistence).
- `SnakeGame.jar` - packaged build with `manifest.txt` (`Main-Class: SnakeGame`).
- `highscore.txt` - created/updated automatically to store the top scores.

Author: Md Seeam Siddique
Date: 05/09/2025
