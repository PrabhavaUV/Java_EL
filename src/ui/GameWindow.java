package ui;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import main.Game;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private Game game;

    public GameWindow(Game game) {
        this.game = game;

        setTitle("Project: Zompocalypse");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(game.getWindowWidth(), game.getWindowHeight());
        setLocationRelativeTo(null);

        gamePanel = new GamePanel(game);
        add(gamePanel);

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                game.getInputHandler().keyPressed(e.getKeyCode());
                handleSpecialKeys(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                game.getInputHandler().keyReleased(e.getKeyCode());
            }
        });

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                game.getInputHandler().mousePressed(e);
                game.handleMenuClick(e.getX(), e.getY());
                gamePanel.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                game.getInputHandler().mouseReleased(e);
            }
        });

        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                game.getInputHandler().mouseMoved(e);
                gamePanel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                game.getInputHandler().mouseMoved(e);
                gamePanel.repaint();
            }
        });

        setVisible(true);
        gamePanel.requestFocus();
    }

    private void handleSpecialKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_P:
                if (game.getGameState() == Game.GameState.PLAYING) {
                    game.pause();
                } else if (game.getGameState() == Game.GameState.PAUSED) {
                    game.resume();
                }
                break;
            case KeyEvent.VK_R:
                if (game.getGameState() == Game.GameState.GAME_OVER || game.getGameState() == Game.GameState.WIN) {
                    game.startNewGame();
                }
                break;
            case KeyEvent.VK_Q:
                if (game.getGameState() == Game.GameState.GAME_OVER || game.getGameState() == Game.GameState.WIN) {
                    game.quit();
                }
                break;
            case KeyEvent.VK_SPACE:
                if (game.getGameState() == Game.GameState.CREDITS || game.getGameState() == Game.GameState.MENU) {
                    game.setGameState(Game.GameState.MENU);
                }
                break;
            case KeyEvent.VK_ESCAPE:
                game.quit();
                break;
        }
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void update() {
        gamePanel.render();
    }
}
