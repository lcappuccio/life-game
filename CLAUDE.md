# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Project Context

When working on this project, prioritize readability over cleverness. Write code that is easy to understand and maintain, even if it means being more verbose. Use clear variable names, add comments where necessary, and structure your code logically.

## About this Project

This is a Java implementation of Conway's Game of Life with multiple cellular automata rules. It uses Java Swing for the main GUI with some JavaFX components for dialogs.

The application simulates cellular automata on a grid where cells evolve according to mathematical rules. Users can interact with the simulation by starting/stopping iterations, stepping through generations, adjusting settings, and loading presets.

## Key Architecture Components

- `gui/MainGui` - Main application window and control orchestration
- `gui/GridGui` - Canvas component for rendering the game board
- `model/Board` - Core simulation logic implementing multiple cellular automata rules
- `menu/*` - Menu components providing user interaction options
- `gui/PreferencesGui` - Settings dialog implemented with JavaFX
- `gui/AboutGui` - About dialog implemented with JavaFX

## Development Commands

### Building
```bash
mvn clean compile assembly:single
```

This creates a fat JAR with all dependencies in the target/ directory.

### Running
```bash
mvn javafx:run
```

Or run the generated JAR:
```bash
java -jar target/life-game-*-jar-with-dependencies.jar
```

### Testing
```bash
mvn test
```

To run a specific test:
```bash
mvn test -Dtest=BoardTest#testConwayVerticalOscillator
```

### Code Coverage
```bash
mvn jacoco:report
```

Reports are generated in target/site/jacoco/

## Architecture Overview

The application follows a Model-View separation pattern:

1. **Model Layer** (`model/Board.java`)
   - Implements the core cellular automata algorithms
   - Maintains board state and handles iteration logic
   - Supports multiple automata rules (Conway, HighLife, Maze, etc.)

2. **View Layer** (`gui/` package)
   - MainGui: Main application window with control buttons
   - GridGui: Custom JPanel for rendering the grid visualization
   - PreferencesGui: JavaFX dialog for application settings
   - AboutGui: JavaFX dialog showing application information

3. **Control Layer** (`menu/` package)
   - Menu components that handle user interactions
   - FileMenu: Save/load functionality
   - SpeedMenu: Simulation speed controls
   - PresetsMenu: Loading predefined patterns

The application uses static variables in MainGui for global state sharing, which should be maintained for compatibility.

## Files to ignore

- `.idea/` - IDE configuration files
- `target/` - Compiled output files
- `gpl.txt` - License file
- `LICENSE` - License file
- `resources/` - Resource files such as images and icons

## Notes

- The project uses Java 17 with JavaFX for modern GUI components
- Multiple cellular automata rules are supported through enums and switch statements
- Performance optimizations include only repainting changed cells
- Settings are persisted through static variables in PreferencesGui
- The codebase mixes Swing (main UI) with JavaFX (dialogs) for UI components