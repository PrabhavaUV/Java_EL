# Project: Zompocalypse

A professional, Java-based 2D action game where players fight waves of zombies and survive the apocalypse. Developed as a showcase of Object-Oriented Programming (OOP) and game development in core Java.

## Features

### Gameplay Mechanics
- **Dynamic Combat**: Fast-paced combat system with both melee and ranged options.
- **Wave System**: Progressively difficult waves with increasing zombie stats.
- **Boss Encounter**: A final boss challenge in Wave 5 with unique AI and massive HP.
- **Atmospheric World**: A grid-based environment with custom textures and semi-transparent UI overlays.
- **Advanced AI**: Entities use a custom A* (Pathfinder) system for navigation through the tile-based world.

### Technical Highlights
- **Architecture**: Organized into a clean, modular package structure (main, ui, entities, world, combat, inputs, utils, graphics).
- **Core Java**: Built entirely using standard Java libraries (AWT/Swing) with no external dependencies.
- **Animation System**: Custom frame-based animation system supporting idle, walk, attack, and death states.
- **Collision Engine**: Custom AABB collision logic with environment clamping and entity interaction.

## Project Structure

The project is organized into the following packages:

- `main`: Entry point (`App.java`) and core game loop/state management (`Game.java`).
- `ui`: Rendering engine (`GamePanel.java`), frame management (`GameWindow.java`), and high-level UI states (`UI.java`).
- `entities`: Hierarchy of game characters (`Player.java`, `Zombie.java`, `Boss.java`) inheriting from a base `Entity.java`.
- `world`: World representation (`Level.java`, `Environment.java`) and `TileMap.java` for grid data.
- `combat`: Weapon mechanics (`Weapon.java`, `MeleeWeapon.java`, `RangedWeapon.java`) and `Projectile.java`.
- `inputs`: Centralized `InputHandler.java` for keyboard and mouse management.
- `graphics`: Resource management (`ResourceManager.java`) and `Animation.java` systems.
- `utils`: Utility classes including the A* `Pathfinder.java` and geometric `Rectangle.java`.

## Getting Started

### Prerequisites
- **Java Development Kit (JDK) 8 or higher** (Recommended: OpenJDK 17 or 21).

### Setup and Running

1. **Clone the project** or copy the source folder.
2. **Open the project** in your preferred IDE (VS Code, IntelliJ IDEA, or Eclipse).
3. **Compile and Run**:
   - In VS Code: Open `src/main/App.java` and press **F5** or the **Run** button.
   - Using Command Line (from the project root):
     ```bash
     mkdir -p bin
     find src -name "*.java" > sources.txt
     javac -d bin @sources.txt
     java -cp bin main.App
     rm sources.txt
     ```

## Controls

| Key | Action |
|-----|--------|
| **W, A, S, D** | Movement |
| **Left Shift** | Sprint (Uses Stamina) |
| **Left Click** | Attack / Shoot |
| **1** | Switch to Melee Weapon |
| **2** | Switch to Ranged Weapon |
| **P** | Pause / Resume |
| **ESC** | Immediate Exit |

## License

This project is open-source and intended for educational use.

## Credits

**Project: Zompocalypse**
Developed as a comprehensive Java Game Project.
Graphics and assets are custom-tailored for this experience.
