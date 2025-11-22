# KIVI Game (Academic Project)

> IMPORTANT NOTICE: THIS IS AN ACADEMIC PROJECT FOR COURSEWORK/ASSIGNMENT USE ONLY. CODE IS PROVIDED SOLELY FOR EDUCATIONAL DEMONSTRATION AND IS NOT AUTHORIZED FOR COMMERCIAL OR PRODUCTION USE.

KIVI is a 7x7 dice-placement board game built with Java Swing. Roll six dice, match poker-style combinations to claim tiles, and chain streaks for points against up to four human or AI players.

## Features
- 1-4 players with any mix of humans and AI (Easy/Hard).
- Four visual "world" themes that recolor tiles and player stones. (Each of the colored worlds are to help the users with their respective color deficiencies)
- Save/load support (up to three slots) plus a winner screen leaderboard.
- Bundled `Game.jar` for quick play; source is plain Swing with no external libraries.

## Requirements
- Java 17+.
- Keep `Grid_Icons/` next to the runnable so tile icons load; `saves/` holds save files.

## Run it
- From source (repo root, since there are no packages):
  - `javac *.java`
  - `java WelcomeScreen`
- From the jar: `java -jar Game.jar`

## Gameplay loop
- Welcome screen -> `NEW GAME` -> choose players/AI difficulty and a world theme -> board.
- On your turn:
  - `Roll Dice` to roll six dice (first roll is free), then up to two rerolls per turn. After the first roll, uncheck dice you want to hold before rerolling.
  - Click a board tile whose icon/label matches your dice combination, then press `Confirm Tile`.
  - If the placement is valid the stone is placed; `Next Player` hands off the turn. Invalid attempts show a warning.
- The game ends once any player finishes 10 turns; the winner screen ranks scores.

## Dice combinations on the board
Two pairs; Three of a kind; Little straight (any 4 in a row); Full house; Four of a kind; Large straight (any 5 in a row); All even; All odd; 12 or fewer; 30 or more; Three pairs; Two times three of a kind; Four of a kind and a pair. Tile icons in `Grid_Icons/` mirror these requirements.

## Scoring
- Tiles carry 1/2/3 points by color (see the board).
- At the end of the game, every contiguous streak of your tiles in each row and column scores `(sum of tile points in the streak) * (streak length)`. The highest total wins.

## Controls & buttons
- `Roll Dice`: roll or reroll the selected dice.
- Dice checkboxes: enabled after the first roll of a turn to hold dice between rerolls.
- `Confirm Tile`: place on the selected tile if the dice match its requirement.
- `Next Player`: move to the next turn (AI turns trigger this automatically).
- `Save Game`: writes `saves/save#.txt` (maximum of three files). `Quit Game` exits.

## AI behavior
- Easy: rolls once and claims the first valid tile found scanning left-to-right, top-to-bottom.
- Hard: same search pattern, but performs one extra reroll if the first roll cannot place a tile.

## Saving and loading
- Saves live in `saves/` and are numbered as created (slots are capped at three).
- Load via the Welcome screen -> `LOAD GAME`, pick a slot, and `CONTINUE GAME`.
- Keep save files in place; renaming/moving them will break the loader.

## Key classes
- `GameManager`: orchestrates turn order, dice values, players/AI, saving/loading, and win detection.
- `Grid`: main board UI handling dice UI, placement validation, and turn controls.
- `Tile`: board cell visuals, combo/point metadata, and stone drawing.
- `GameLogic`: validates dice combinations against tile requirements.
- `ScoreCalculator`: end-game row/column streak scoring.
- `Player` / `AIPlayer`: player state and automated turns (Easy/Hard).
- `WelcomeScreen`, `GameSettingScreen`, `LoadGameGUI`, `WinnerGUI`: navigation between screens.

## Project layout
- `*.java`: source files (no packages).
- `Grid_Icons/`: icons for tile requirements.
- `saves/`: save files generated at runtime.
- `Game.jar`: built artifact with `Main-Class: WelcomeScreen`.

Author: Md Seeeam Siddique, Aishna Gupta, Syeda Tasnim Ezaz, Nick Farrel, Mohammad Rajin Hasan
Date: 31/03/2025