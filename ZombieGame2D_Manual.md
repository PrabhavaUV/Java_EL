# Project: Zompocalypse 🧟‍♂️

**Zompocalypse** is a professional, high-octane 2D action-survival game developed in core Java. Players must fight off endless hordes of zombies, manage stamina, and defeat the Zombie Lord to survive the apocalypse.

This project demonstrates advanced Object-Oriented Programming (OOP) concepts, custom 2D rendering, and game state management without relying on external game engines.

---

## 🎮 Gameplay Features

*   **Wave-Based Survival**: Survive through 5 progressively difficult waves.
*   **Epic Boss Fight**: Confront the **Zombie Lord** in Wave 5—a massive boss with high health and damage.
*   **Dynamic Combat**:
    *   **Melee**: High damage sword attacks.
    *   **Ranged**: Unlimited ammo gun for ranged crowd control.
*   **Stamina System**: Sprinting consumes stamina, requiring strategic movement.
*   **Custom Game Engine**:
    *   Built from scratch using `Java AWT` and `Swing`.
    *   Custom **TileMap** collision system.
    *   **A* Pathfinding** AI for zombie navigation.
*   **Visual Polish**:
    *   Smooth animations for Idle, Run, Attack, Shoot, and Death.
    *   HUD rendering for Health, Stamina, Wave info, and Ammo.
    *   Game States (Menu, Playing, Pause, Game Over, Victory).

---

## 🕹️ Controls

| Key / Input | Action |
| :--- | :--- |
| **W, A, S, D** | Move Character (Up, Left, Down, Right) |
| **Left Shift** | Sprint (Consumes Stamina) |
| **Mouse Cursor** | Aim Character / Look Direction |
| **Left Click** | Attack (Melee) or Shoot (Ranged) |
| **1** | Equip Melee Weapon (Sword) |
| **2** | Equip Ranged Weapon (Gun) |
| **P** | Pause / Resume Game |
| **ESC** | Immediate Quit |
| **R** | Restart Game (Only on Game Over/Victory Screen) |
| **Q** | Quit Game (Only on Game Over/Victory Screen) |
| **Space** | Return to Menu (From Credits) |

---

## 🛠️ Installation & How to Run

### Prerequisites
*   **Java Development Kit (JDK) 8** or higher (Recommended: JDK 17 or 21).

### Running via VS Code (Recommended)
1.  Open the project folder in **VS Code**.
2.  Navigate to `src/main/App.java`.
3.  Press **F5** or click **Run**.

### Running via Command Line
1.  Open your terminal in the project root folder.
2.  Create a `bin` directory for compiled classes:
    ```bash
    mkdir -p bin
    ```
3.  Compile the source code:
    ```bash
    javac -d bin -sourcepath src src/main/App.java
    ```
    *(Note: You can also use `find src -name "*.java" > sources.txt` and `javac -d bin @sources.txt` if needed)*
4.  Run the game:
    ```bash
    java -cp bin main.App
    ```

---

## 📂 Project Structure

The codebase is organized into modular packages to ensure separation of concerns:

*   **`main`**: Contains the entry point `App.java` and total game coordination in `Game.java`.
*   **`world`**: Manages the `Level`, `Environment` (bounds), and `TileMap` (collision grid).
*   **`entities`**: Inheritance hierarchy for `Entity` -> `Character` -> `Player`, `Zombie`, `Boss`.
*   **`combat`**: Logic for `Weapon`, `MeleeWeapon`, `RangedWeapon`, and `Projectile`.
*   **`ui`**: Handles all rendering via `GamePanel` and window management in `GameWindow`.
*   **`inputs`**: Centralized `InputHandler` for keyboard and mouse state tracking.
*   **`graphics`**: Sprite-sheet loading (`SpriteSheet`) and frame-based `Animation` logic.
*   **`utils`**: Helper math classes like `Rectangle` (collisions) and `Pathfinder` (A* algorithm).

---

## 🎓 Educational Value

This project was built to master:
*   **Game Loops**: Implementing a fixed time-step game loop.
*   **Double Buffering**: Preventing screen flickering using `BufferStrategy` logic (Custom implementation in `GamePanel`).
*   **State Machines**: Handling transitions between Menu, Game, and Pause states.
*   **Collision Detection**: AABB (Axis-Aligned Bounding Box) collision against a tile grid.
*   **Artificial Intelligence**: Implementing basic pathfinding for enemy tracking.

---

### Credits
**Developed by Prabhava** for Java Semester 1 EL.
*Assets and graphical resources are custom-curated for this experience.*
