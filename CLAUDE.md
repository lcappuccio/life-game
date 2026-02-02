# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Project Overview

This is a Java implementation of Conway's Game of Life with multiple cellular automata rules. It uses Java Swing for the main GUI with some JavaFX components for dialogs.

The application simulates cellular automata on a grid where cells evolve according to mathematical rules. Users can interact with the simulation by starting/stopping iterations, stepping through generations, adjusting settings, and loading presets.

# Project Context

When working on this project, prioritize readability over cleverness. Write code that is easy to understand and maintain, even if it means being more verbose. Use clear variable names, add comments where necessary, and structure your code logically.

Do not focus now on optimizations or bugfixes unless explicitly requested. The main goal is to migrate from swing to javafx without altering the existing functionality.

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

## JavaFX Migration

### Effort and Complexity Assessment: Migrating GridGui to JavaFX

Current State Analysis

The GridGui is currently implemented as a Swing JPanel with custom painting logic. It has several key responsibilities:

1. Custom Rendering: Draws the cellular automata grid using Graphics2D
2. Mouse Interaction: Handles cell toggling via mouse clicks
3. Performance Optimization: Tracks changed cells to minimize repaints
4. Theme Support: Manages color schemes for live/dead cells
5. Integration Points: Used by MainGui, FileUtils, and various tests

Migration Complexity: High

### Technical Challenges

1. Rendering Engine Change:
   - Moving from Swing's Graphics2D to JavaFX's Canvas/Scene graph
   - Need to rewrite the entire painting logic
   - Different coordinate systems and rendering approaches
2. Event Handling Differences:
   - Swing mouse listeners vs JavaFX event handlers
   - Different threading models between Swing and JavaFX
3. Performance Considerations:
   - Current optimization for partial repaints needs redesign
   - JavaFX has different performance characteristics
4. Integration Impact:
   - MainGui integrates GridGui as a Swing component
   - Would require significant changes to MainGui to accommodate JavaFX
   - Mixed Swing/JavaFX applications require special handling (JFXPanel)

Effort Estimation: 2-3 weeks

### Detailed Breakdown

#### Phase 1: Core Migration (1-1.5 weeks)

1. Create JavaFX Canvas-based GridGui (3-4 days)
   - Implement Canvas rendering instead of custom painting
   - Convert mouse event handling to JavaFX equivalents
   - Maintain same public API for backward compatibility
2. Performance Optimization (2-3 days)
   - Redesign changed cell tracking for JavaFX
   - Implement efficient redraw strategies

#### Phase 2: Integration & Testing (1-1.5 weeks)

1. MainGui Integration (2-3 days)
   - Modify MainGui to embed JavaFX GridGui (likely using JFXPanel)
   - Handle threading between Swing and JavaFX
2. Testing & Compatibility (2-3 days)
   - Ensure all existing tests pass
   - Verify file loading/saving functionality
   - Test performance with large grids
3. UI Polish & Bug Fixes (1-2 days)
   - Address any rendering differences
   - Fix integration issues

#### Key Considerations

1. Mixed Environment Complexity:
   - The application would become a hybrid Swing/JavaFX app
   - Requires careful thread management (Platform.runLater() vs SwingUtilities.invokeLater())
2. Performance Implications:
   - Need to ensure JavaFX rendering performs as well as current Swing implementation
   - Large grids might have different performance characteristics
3. Backward Compatibility:
   - All public methods and constructors must remain compatible
   - Existing tests should continue to pass with minimal changes

### Recommendation

Given the complexity and effort involved, consider whether a full migration is necessary. The current Swing implementation works well for
this use case. If modernization is desired, a phased approach focusing on specific components might be more practical than migrating the
entire GridGui at once.

The migration would be significantly easier if the entire application were moved to JavaFX, but that would involve much broader changes
beyond just GridGui.