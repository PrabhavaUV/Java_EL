package entities;

import graphics.Animation;
import graphics.ResourceManager;
import java.awt.image.BufferedImage;

public class Character extends Entity {
    protected int hp;
    protected int maxHp;
    protected boolean facingRight = true;
    protected BufferedImage currentFrame;
    protected Animation currentAnimation;

    public Character(float x, float y, int width, int height, int maxHp) {
        super(x, y, width, height);
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0)
            hp = 0;
        if (hp <= 0)
            alive = false;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp >= maxHp)
            this.hp = maxHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public BufferedImage getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(BufferedImage frame) {
        this.currentFrame = frame;
    }

    @Override
    public void update(float dt) {
        if (currentAnimation != null) {
            currentAnimation.update();
            currentFrame = currentAnimation.getCurrentFrame();
        }
    }

    @Override
    public void render(Object g) {
    }

    @Override
    public Object getBounds() {
        return super.getBounds();
    }

    protected Animation loadAnimationFromStrip(String path, int frameCount, int speed) {
        try {
            BufferedImage strip = ResourceManager.getTexture(path);
            if (strip != null) {
                if (frameCount <= 0) {
                    if (strip.getHeight() > 0) {
                        frameCount = strip.getWidth() / strip.getHeight();
                    } else {
                        frameCount = 1;
                    }
                }

                if (frameCount < 1)
                    frameCount = 1;

                int frameWidth = strip.getWidth() / frameCount;
                int frameHeight = strip.getHeight();

                BufferedImage[] frames = new BufferedImage[frameCount];
                for (int i = 0; i < frameCount; i++) {
                    frames[i] = strip.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
                }
                return new Animation(speed, frames);
            }
        } catch (Exception e) {
            System.err.println("Error loading animation from " + path + ": " + e.getMessage());
        }
        return null;
    }
}
