package main;

public class App {
    public static void main(String[] args) {
        Game game = new Game(800, 600);
        game.init();
        game.gameLoop();
    }
}