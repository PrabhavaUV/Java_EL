package ui;

import entities.Player;
import world.Level;

public class UI {
    public enum UIState {
        LOADING, MAIN_MENU, IN_GAME, PAUSE_MENU, DEATH_SCREEN, SUCCESS_SCREEN, INFO, CREDITS
    }

    private UIState currentState;
    private Player player;
    private Level level;

    public UI() {
        this.currentState = UIState.LOADING;
    }

    public void draw(Object g) {
    }

    public void update(float dt) {
    }

    public UIState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(UIState state) {
        this.currentState = state;
    }

    public void showLoadingScreen() {
        currentState = UIState.LOADING;
    }

    public void showMainMenu() {
        currentState = UIState.MAIN_MENU;
    }

    public void showInGame() {
        currentState = UIState.IN_GAME;
    }

    public void showPauseMenu() {
        currentState = UIState.PAUSE_MENU;
    }

    public void showDeathScreen() {
        currentState = UIState.DEATH_SCREEN;
    }

    public void showSuccessScreen() {
        currentState = UIState.SUCCESS_SCREEN;
    }

    public void showInfo() {
        currentState = UIState.INFO;
    }

    public void showCredits() {
        currentState = UIState.CREDITS;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
