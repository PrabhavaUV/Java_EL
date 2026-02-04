package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import main.Game;
import world.Level;
import world.TileMap;
import entities.Zombie;
import combat.Projectile;
import entities.Player;
import entities.Character;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private Game game;
    private BufferedImage buffer;
    private Graphics2D g2d;

    public GamePanel(Game game) {
        this.game = game;
        setFocusable(true);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (buffer == null) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        g2d = buffer.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        renderGame(g2d);

        g.drawImage(buffer, 0, 0, null);
        g2d.dispose();
    }

    private void renderGame(Graphics2D g) {
        Game.GameState state = game.getGameState();

        switch (state) {
            case LOADING:
                drawLoadingScreen(g);
                break;
            case MENU:
                drawMainMenu(g);
                break;
            case INFO:
                drawInformationScreen(g);
                break;
            case PLAYING:
                drawGameplay(g);
                break;
            case PAUSED:
                drawPausedScreen(g);
                break;
            case GAME_OVER:
                drawGameOverScreen(g);
                break;
            case WIN:
                drawSuccessScreen(g);
                break;
            case CREDITS:
                drawInformationScreen(g);
                break;
        }
    }

    private void drawLoadingScreen(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String text = "LOADING...";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g.drawString(text, x, y);

        g.setColor(Color.GREEN);
        int barWidth = 200;
        int barHeight = 30;
        int barX = (getWidth() - barWidth) / 2;
        int barY = y + 50;
        g.fillRect(barX, barY, barWidth, barHeight);
    }

    private void drawMainMenu(Graphics2D g) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 40), 0, getHeight(), new Color(10, 10, 20));
        g.setPaint(gradient);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 64));
        String title = "PROJECT: ZOMPOCALYPSE";
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(0, 255, 0, 100));
        g.drawString(title, (getWidth() - fm.stringWidth(title)) / 2 + 3, 150 + 3);
        g.setColor(Color.GREEN);
        g.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 150);

        int centerX = getWidth() / 2;
        int startY = getHeight() / 2 - 50;
        int btnWidth = 200;
        int btnHeight = 40;
        float mouseX = game.getInputHandler().getMouseX();
        float mouseY = game.getInputHandler().getMouseY();

        boolean hoverPlay = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2 && mouseY >= startY
                && mouseY <= startY + btnHeight;
        boolean hoverInfo = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2
                && mouseY >= startY + 60 && mouseY <= startY + 60 + btnHeight;
        boolean hoverExit = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2
                && mouseY >= startY + 120 && mouseY <= startY + 120 + btnHeight;

        drawMenuButton(g, "Play Game", centerX, startY, hoverPlay);
        drawMenuButton(g, "Information", centerX, startY + 60, hoverInfo);
        drawMenuButton(g, "Exit", centerX, startY + 120, hoverExit);

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        String footer = "Use WASD to move, Mouse to aim and shoot";
        fm = g.getFontMetrics();
        g.drawString(footer, (getWidth() - fm.stringWidth(footer)) / 2, getHeight() - 30);
    }

    private void drawMenuButton(Graphics2D g, String text, int centerX, int y, boolean isHovered) {
        int width = 200;
        int height = 40;
        int x = centerX - width / 2;

        g.setColor(isHovered ? new Color(70, 70, 100) : new Color(50, 50, 70));
        g.fillRoundRect(x, y, width, height, 10, 10);

        if (isHovered) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
        } else {
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2));
        }
        g.drawRoundRect(x, y, width, height, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, centerX - fm.stringWidth(text) / 2, y + height / 2 + 7);
    }

    private void drawInformationScreen(Graphics2D g) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30), 0, getHeight(), new Color(5, 5, 15));
        g.setPaint(gradient);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "INFORMATION";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] infoLines = {
                "W / S / A / D : Move Character",
                "Left Shift : Sprint (Consumes Stamina)",
                "Mouse Move : Aim",
                "Left Click : Attack / Shoot",
                "1 : Switch to Melee",
                "2 : Switch to Ranged",
                "P : Pause Game",
                "R : Restart (on Game Over)",
                "Q : Quit",
                "",
                "Objective: Survive 4 waves and defeat the Zombie Lord in Wave 5!",
                "",
                "Click anywhere to return to Menu"
        };

        int y = 180;
        for (String line : infoLines) {
            fm = g.getFontMetrics();
            int lx = (getWidth() - fm.stringWidth(line)) / 2;
            g.drawString(line, lx, y);
            y += 30;
        }
    }

    private void drawGameplay(Graphics2D g) {
        Level level = game.getCurrentLevel();
        if (level == null)
            return;

        GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 30), 0, getHeight(), new Color(50, 50, 50));
        g.setPaint(gradient);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(1));
        for (int x = 0; x < getWidth(); x += 32)
            g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32)
            g.drawLine(0, y, getWidth(), y);

        TileMap tileMap = level.getTileMap();
        if (tileMap != null) {
            tileMap.render(g, 0, 0);
        }

        List<Zombie> zombies = new ArrayList<>(level.getZombies());
        for (Zombie zombie : zombies) {
            drawZombie(g, zombie);
        }

        List<Projectile> projectiles = new ArrayList<>(level.getProjectiles());
        for (Projectile p : projectiles) {
            if (p.isActive())
                p.render(g);
        }

        Player player = level.getPlayer();
        drawPlayer(g, player);
        drawHUD(g, player, level);
    }

    private void drawPlayer(Graphics2D g, Player player) {
        int x = (int) player.getX();
        int y = (int) player.getY();
        int width = player.getWidth();
        int height = player.getHeight();

        float scale = 2.0f;
        int drawWidth = (int) (width * scale);
        int drawHeight = (int) (height * scale);
        int drawX = x + (width - drawWidth) / 2;
        int drawY = y + (height - drawHeight) / 2;

        BufferedImage frame = player.getCurrentFrame();
        if (frame != null) {
            if (player.isFacingRight()) {
                g.drawImage(frame, drawX, drawY, drawWidth, drawHeight, null);
            } else {
                g.drawImage(frame, drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
            }
        } else {
            g.setColor(new Color(50, 150, 255));
            g.fillRect(x, y, width, height);
            g.setColor(new Color(100, 200, 255));
            g.fillOval(x + 5, y - 10, 22, 20);
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x, y, width, height);
        }
    }

    private void drawZombie(Graphics2D g, Zombie zombie) {
        int x = (int) zombie.getX();
        int y = (int) zombie.getY();
        int width = zombie.getWidth();
        int height = zombie.getHeight();

        float scale = 2.0f;
        int drawWidth = (int) (width * scale);
        int drawHeight = (int) (height * scale);
        int drawX = x + (width - drawWidth) / 2;
        int drawY = y + (height - drawHeight) / 2;

        BufferedImage frame = zombie.getCurrentFrame();
        if (frame != null) {
            if (zombie.isFacingRight()) {
                g.drawImage(frame, drawX, drawY, drawWidth, drawHeight, null);
            } else {
                g.drawImage(frame, drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
            }
        } else {
            g.setColor(new Color(100, 200, 100));
            g.fillRect(x, y, width, height);
            g.setColor(new Color(150, 220, 150));
            g.fillOval(x + 5, y - 12, 22, 22);
            g.setColor(Color.RED);
            g.fillOval(x + 8, y - 8, 4, 4);
            g.fillOval(x + 18, y - 8, 4, 4);
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x, y, width, height);
        }
        drawEntityHealthBar(g, zombie, x, y);
    }

    private void drawEntityHealthBar(Graphics2D g, Character character, int x, int y) {
        int barWidth = 30;
        int barHeight = 4;
        int barX = x + (character.getWidth() - barWidth) / 2;
        int barY = y - 15;

        g.setColor(Color.BLACK);
        g.fillRect(barX, barY, barWidth, barHeight);

        float healthPercent = (float) character.getHp() / character.getMaxHp();
        g.setColor(healthPercent > 0.5f ? Color.GREEN : (healthPercent > 0.25f ? Color.YELLOW : Color.RED));
        g.fillRect(barX, barY, (int) (barWidth * healthPercent), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private void drawHUD(Graphics2D g, Player player, Level level) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));

        g.drawString("Health: " + player.getHp() + "/" + player.getMaxHp(), 10, 25);
        g.drawString("Lives: " + player.getLives(), 10, 50);

        if (player.getCurrentWeapon() != null) {
            g.drawString("Weapon: " + player.getCurrentWeapon().getName(), 10, 75);
        }

        g.drawString("Wave: " + level.getWave(), getWidth() - 150, 25);
        g.drawString("Zombies: " + level.getZombiesSpawned() + "/" + level.getZombiesRequired(), getWidth() - 150, 50);
        g.drawString("Press P to Pause", getWidth() - 150, 75);

        drawHealthBar(g, player);
        drawStaminaBar(g, player);
    }

    private void drawHealthBar(Graphics2D g, Player player) {
        int barWidth = 200;
        int barHeight = 20;
        int x = 10, y = 120;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        float healthPercent = (float) player.getHp() / player.getMaxHp();
        g.setColor(healthPercent > 0.5f ? Color.GREEN : (healthPercent > 0.25f ? Color.YELLOW : Color.RED));
        g.fillRect(x, y, (int) (barWidth * healthPercent), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);
    }

    private void drawStaminaBar(Graphics2D g, Player player) {
        int barWidth = 150, barHeight = 10;
        int x = 10, y = 145;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        float staminaPercent = player.getStamina() / player.getMaxStamina();
        g.setColor(Color.CYAN);
        g.fillRect(x, y, (int) (barWidth * staminaPercent), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);
    }

    private void drawPausedScreen(Graphics2D g) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 20, 180), 0, getHeight(),
                new Color(0, 0, 0, 220));
        g.setPaint(gradient);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2 - 100;
        g.drawString(text, x, y);

        int centerX = getWidth() / 2;
        int startY = getHeight() / 2 - 20;
        int btnWidth = 200, btnHeight = 40;
        float mouseX = game.getInputHandler().getMouseX();
        float mouseY = game.getInputHandler().getMouseY();

        boolean hoverResume = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2 && mouseY >= startY
                && mouseY <= startY + btnHeight;
        boolean hoverRestart = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2
                && mouseY >= startY + 60 && mouseY <= startY + 60 + btnHeight;
        boolean hoverQuit = mouseX >= centerX - btnWidth / 2 && mouseX <= centerX + btnWidth / 2
                && mouseY >= startY + 120 && mouseY <= startY + 120 + btnHeight;

        drawMenuButton(g, "Resume", centerX, startY, hoverResume);
        drawMenuButton(g, "Restart", centerX, startY + 60, hoverRestart);
        drawMenuButton(g, "Quit", centerX, startY + 120, hoverQuit);
    }

    private void drawGameOverScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2, y = getHeight() / 2 - 50;
        g.drawString(text, x, y);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restart = "Press R to Restart or Q to Quit";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(restart)) / 2;
        g.drawString(restart, x, y + 100);
    }

    private void drawSuccessScreen(Graphics2D g) {
        g.setColor(new Color(0, 50, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 72));
        String victoryText = "VICTORY!";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(victoryText)) / 2, y = getHeight() / 2 - 100;

        g.setColor(new Color(0, 255, 0, 100));
        g.drawString(victoryText, x + 4, y + 4);
        g.setColor(Color.GREEN);
        g.drawString(victoryText, x, y);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 28));
        String msgText = "You have defeated the Zombie Lord!";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(msgText)) / 2;
        g.drawString(msgText, x, y + 80);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restart = "Press R to Restart or Q to Quit";
        fm = g.getFontMetrics();
        x = (getWidth() - fm.stringWidth(restart)) / 2;
        g.drawString(restart, x, y + 180);
    }

    public void render() {
        repaint();
    }
}
