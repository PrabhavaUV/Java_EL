package main;

import entities.Player;
import world.Level;
import world.Environment;
import world.TileMap;
import ui.UI;
import ui.GameWindow;
import inputs.InputHandler;
import combat.MeleeWeapon;
import combat.RangedWeapon;

public class Game {
    public enum GameState {
        LOADING, MENU, INFO, PLAYING, PAUSED, GAME_OVER, WIN, CREDITS
    }

    private int windowWidth;
    private int windowHeight;
    private Level currentLevel;
    private UI ui;
    private GameState gameState;
    private InputHandler inputHandler;
    private GameWindow gameWindow;
    private boolean running;
    private float deltaTime;
    private long lastFrameTime;

    public Game(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.gameState = GameState.LOADING;
        this.inputHandler = new InputHandler();
        this.running = false;
        this.deltaTime = 0.016f;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void init() {
        gameWindow = new GameWindow(this);
        running = true;
        ui = new UI();

        gameState = GameState.MENU;
        ui.setCurrentState(UI.UIState.MAIN_MENU);
    }

    public void startNewGame() {
        Environment environment = new Environment(windowWidth, windowHeight, 32);
        TileMap tileMap = new TileMap(environment.getTileSize());
        try {
            tileMap.loadMap("assets/maps/map1.txt");

            int[][] envGrid = environment.getCollisionTiles();
            int envRows = envGrid.length;
            int envCols = envGrid[0].length;
            int[][] mapGrid = tileMap.getCollisionGrid();
            int[][] finalGrid = new int[envRows][envCols];
            for (int r = 0; r < envRows; r++) {
                for (int c = 0; c < envCols; c++) {
                    if (r < mapGrid.length && c < mapGrid[0].length) {
                        finalGrid[r][c] = mapGrid[r][c];
                    } else {
                        finalGrid[r][c] = 0;
                    }
                }
            }
            environment.setCollisionTiles(finalGrid);
            environment.ensureBorders();
        } catch (Exception e) {
            initializeEnvironmentCollisions(environment);
        }

        Player player = new Player(windowWidth / 2, windowHeight / 2, 32, 32, 100, 3);

        MeleeWeapon melee = new MeleeWeapon("Melee", 15, 0.5f, 50);
        RangedWeapon gun = new RangedWeapon("Gun", 20, 0.2f, "bullet", 1, 500);
        player.addWeapon(melee);
        player.addWeapon(gun);
        player.setCurrentWeapon(melee);
        player.setAmmo("bullet", 10);

        currentLevel = new Level(1, environment, player);
        currentLevel.setTileMap(tileMap);

        player.setLevel(currentLevel);

        ui.setPlayer(player);
        ui.setLevel(currentLevel);
        ui.showInGame();

        gameState = GameState.PLAYING;
    }

    private void initializeEnvironmentCollisions(Environment environment) {
        int[][] collisionTiles = environment.getCollisionTiles();
        int mapWidth = environment.getWidth() / environment.getTileSize();
        int mapHeight = environment.getHeight() / environment.getTileSize();

        for (int x = 0; x < mapWidth; x++) {
            collisionTiles[0][x] = 1;
            collisionTiles[mapHeight - 1][x] = 1;
        }
        for (int y = 0; y < mapHeight; y++) {
            collisionTiles[y][0] = 1;
            collisionTiles[y][mapWidth - 1] = 1;
        }
        environment.ensureBorders();
    }

    public void gameLoop() {
        while (running) {
            updateDeltaTime();
            update(deltaTime);
            gameWindow.update();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.exit(0);
    }

    private void updateDeltaTime() {
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;

        if (deltaTime > 0.033f) {
            deltaTime = 0.033f;
        }
    }

    public void update(float dt) {
        switch (gameState) {
            case PLAYING:
                updatePlaying(dt);
                break;
            default:
                break;
        }

        if (ui != null) {
            ui.update(dt);
        }

        inputHandler.reset();
    }

    private void updatePlaying(float dt) {
        if (currentLevel != null) {
            Player player = currentLevel.getPlayer();

            player.handleInput(inputHandler);

            currentLevel.update(dt);

            if (player.getLives() <= 0) {
                gameState = GameState.GAME_OVER;
                ui.showDeathScreen();
            }

            if (currentLevel.isLevelComplete() && currentLevel.getZombies().isEmpty()) {
                if (player.getLives() > 0) {
                    if (currentLevel.getWave() > 5) {
                        gameState = GameState.WIN;
                        ui.setCurrentState(UI.UIState.SUCCESS_SCREEN);
                    } else {
                        currentLevel.startNextWave();
                        player.setAmmo("bullet", 10);
                    }
                }
            }
        }
    }

    public void render() {
        Object g = null;

        switch (gameState) {
            case PLAYING:
                if (currentLevel != null) {
                    currentLevel.render(g);
                }
                break;
            default:
                break;
        }

        if (ui != null) {
            ui.draw(g);
        }
    }

    public void pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            ui.showPauseMenu();
        }
    }

    public void resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            ui.showInGame();
        }
    }

    public void quit() {
        running = false;
        System.exit(0);
    }

    public void handleMenuClick(int mouseX, int mouseY) {
        if (gameState != GameState.MENU && gameState != GameState.PAUSED
                && ui.getCurrentState() != UI.UIState.SUCCESS_SCREEN
                && ui.getCurrentState() != UI.UIState.DEATH_SCREEN) {
            return;
        }

        int centerX = windowWidth / 2;
        int btnWidth = 200;
        int btnHeight = 40;

        int midY = windowHeight / 2;
        int startY = midY - 50;
        int btnX = centerX - btnWidth / 2;

        if (gameState == GameState.MENU) {
            if (ui.getCurrentState() == UI.UIState.MAIN_MENU) {
                if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= startY && mouseY <= startY + btnHeight) {
                    startNewGame();
                } else if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= startY + 60
                        && mouseY <= startY + 60 + btnHeight) {
                    gameState = GameState.INFO;
                    ui.setCurrentState(UI.UIState.INFO);
                } else if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= startY + 120
                        && mouseY <= startY + 120 + btnHeight) {
                    quit();
                }
            } else if (gameState == GameState.INFO || ui.getCurrentState() == UI.UIState.INFO) {
                gameState = GameState.MENU;
                ui.setCurrentState(UI.UIState.MAIN_MENU);
            }
        } else if (gameState == GameState.PAUSED) {
            int pauseStartY = midY - 20;

            if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= pauseStartY
                    && mouseY <= pauseStartY + btnHeight) {
                resume();
            } else if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= pauseStartY + 60
                    && mouseY <= pauseStartY + 60 + btnHeight) {
                startNewGame();
            } else if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= pauseStartY + 120
                    && mouseY <= pauseStartY + 120 + btnHeight) {
                gameState = GameState.MENU;
                ui.setCurrentState(UI.UIState.MAIN_MENU);
            }
        }
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level level) {
        this.currentLevel = level;
    }

    public UI getUI() {
        return ui;
    }

    public void setUI(UI ui) {
        this.ui = ui;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }
}
