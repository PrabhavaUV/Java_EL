package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Stroke;
import world.Environment;
import world.Level;
import ui.UI;
import inputs.InputHandler;
import combat.Weapon;
import graphics.Animation;

public class Player extends Character {
    private Animation idleAnim;
    private Animation walkAnim;
    private Animation attackAnim;
    private Animation shootAnim;
    private Animation hurtAnim;

    private int lives;
    private List<Weapon> weapons;
    private Weapon currentWeapon;
    private Map<String, Integer> ammo;
    private UI hud;
    private float attackCooldown;
    private float attackTimer;
    private float invulnerabilityTimer;
    private static final float INVULNERABILITY_TIME = 1.0f;
    private Environment environment;

    private float walkSpeed;
    private float runSpeed;
    private float stamina;
    private float maxStamina;
    private float staminaRegen;
    private float staminaDrain;
    private static final float STAMINA_COOLDOWN = 1.0f;
    private float staminaRegenTimer;
    private float aimX, aimY;
    private Level level;

    public Player(float x, float y, int width, int height, int maxHp, int lives) {
        super(x, y, width, height, maxHp);
        this.lives = lives;
        this.weapons = new ArrayList<>();
        this.ammo = new HashMap<>();
        this.attackCooldown = 0.5f;
        this.attackTimer = 0;
        this.invulnerabilityTimer = 0;

        this.walkSpeed = 60;
        this.runSpeed = 200;
        this.speed = walkSpeed;

        this.maxStamina = 100;
        this.stamina = maxStamina;
        this.staminaDrain = 30;
        this.staminaRegen = 20;
        this.staminaRegenTimer = 0;

        loadAnimations();
    }

    private void loadAnimations() {
        idleAnim = loadAnimationFromStrip("assets/skins/player/Idle.png", 0, 200);
        walkAnim = loadAnimationFromStrip("assets/skins/player/Run.png", 0, 100);
        if (walkAnim == null) {
            walkAnim = loadAnimationFromStrip("assets/skins/player/Walk.png", 0, 100);
        }
        if (walkAnim == null && idleAnim != null) {
            walkAnim = idleAnim;
        }

        attackAnim = loadAnimationFromStrip("assets/skins/player/Attack_1.png", 0, 100);
        if (attackAnim != null)
            attackAnim.setLooping(false);

        shootAnim = loadAnimationFromStrip("assets/skins/player/Shot.png", 0, 100);
        if (shootAnim != null)
            shootAnim.setLooping(false);

        hurtAnim = loadAnimationFromStrip("assets/skins/player/Hurt.png", 0, 100);
        if (hurtAnim != null)
            hurtAnim.setLooping(false);

        currentAnimation = idleAnim;
    }

    public void spawn(float x, float y) {
        this.x = x;
        this.y = y;
        this.alive = true;
        this.hp = maxHp;
        this.invulnerabilityTimer = INVULNERABILITY_TIME;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void handleInput(InputHandler input) {
        if (input.isSwitchingToMelee()) {
            for (Weapon w : weapons) {
                if (w.getType().equals("melee")) {
                    setCurrentWeapon(w);
                    break;
                }
            }
        }
        if (input.isSwitchingToRanged()) {
            for (Weapon w : weapons) {
                if (w.getType().equals("ranged")) {
                    setCurrentWeapon(w);
                    break;
                }
            }
        }

        velocityX = 0;
        velocityY = 0;

        boolean isMoving = input.isMovingUp() || input.isMovingDown() || input.isMovingLeft() || input.isMovingRight();
        boolean isRunning = input.isRunning() && isMoving;

        if (isRunning && stamina > 0) {
            speed = runSpeed;
            stamina -= staminaDrain * (1.0f / 60.0f);
            staminaRegenTimer = STAMINA_COOLDOWN;
            if (stamina < 0)
                stamina = 0;
        } else {
            speed = walkSpeed;
            if (staminaRegenTimer > 0) {
                staminaRegenTimer -= (1.0f / 60.0f);
            } else if (stamina < maxStamina) {
                stamina += staminaRegen * (1.0f / 60.0f);
                if (stamina > maxStamina)
                    stamina = maxStamina;
            }
        }

        if (input.isMovingUp())
            velocityY = -speed;
        if (input.isMovingDown())
            velocityY = speed;
        if (input.isMovingLeft()) {
            velocityX = -speed;
            facingRight = false;
        }
        if (input.isMovingRight()) {
            velocityX = speed;
            facingRight = true;
        }

        boolean isAttacking = (currentAnimation == attackAnim && attackAnim != null && !attackAnim.isFinished()) ||
                (currentAnimation == shootAnim && shootAnim != null && !shootAnim.isFinished());
        boolean isHurting = (currentAnimation == hurtAnim && hurtAnim != null && !hurtAnim.isFinished());

        if (!isAttacking && !isHurting) {
            if (velocityX == 0 && velocityY == 0) {
                currentAnimation = idleAnim;
            } else {
                currentAnimation = walkAnim;
            }
        }

        if (input.getMouseX() > x + width / 2) {
            facingRight = true;
        } else {
            facingRight = false;
        }

        aimX = input.getMouseX();
        aimY = input.getMouseY();
        if (input.isAttacking() && attackTimer <= 0) {
            attack(input.getMouseX(), input.getMouseY());
            attackTimer = attackCooldown;
        }
    }

    public float getStamina() {
        return stamina;
    }

    public float getMaxStamina() {
        return maxStamina;
    }

    public void attack(float targetX, float targetY) {
        if (currentWeapon != null) {
            currentWeapon.attack(this, targetX, targetY, level);

            Animation animToPlay = null;
            if (currentWeapon.getType() != null
                    && (currentWeapon.getType().equals("melee") || currentWeapon.getName().contains("Sword"))) {
                animToPlay = attackAnim;
            } else {
                animToPlay = shootAnim;
            }

            if (animToPlay != null) {
                currentAnimation = animToPlay;
                currentAnimation.reset();
            }
        }
    }

    public void die() {
        lives--;
        hp = 0;
        alive = false;
        if (lives > 0) {
            spawn(x, y);
        }
    }

    public void respawn() {
        this.alive = true;
        this.hp = maxHp;
        this.invulnerabilityTimer = INVULNERABILITY_TIME;
    }

    @Override
    public void takeDamage(int damage) {
        if (invulnerabilityTimer <= 0) {
            hp -= damage;
            if (hp < 0)
                hp = 0;
            invulnerabilityTimer = INVULNERABILITY_TIME;

            if (hurtAnim != null) {
                currentAnimation = hurtAnim;
                currentAnimation.reset();
            }

            if (hp <= 0)
                die();
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
        if (currentWeapon == null)
            currentWeapon = weapon;
    }

    public void removeWeapon(Weapon weapon) {
        weapons.remove(weapon);
        if (currentWeapon == weapon && !weapons.isEmpty()) {
            currentWeapon = weapons.get(0);
        }
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon weapon) {
        if (weapons.contains(weapon))
            this.currentWeapon = weapon;
    }

    public Map<String, Integer> getAmmo() {
        return ammo;
    }

    public void setAmmo(String ammoType, int amount) {
        ammo.put(ammoType, amount);
    }

    public void addAmmo(String ammoType, int amount) {
        ammo.put(ammoType, ammo.getOrDefault(ammoType, 0) + amount);
    }

    public UI getHud() {
        return hud;
    }

    public void setHud(UI hud) {
        this.hud = hud;
    }

    public float getInvulnerabilityTimer() {
        return invulnerabilityTimer;
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0;
    }

    public void setEnvironment(Environment env) {
        this.environment = env;
    }

    @Override
    public void update(float dt) {
        float nextX = x + velocityX * dt;
        float nextY = y + velocityY * dt;

        if (environment != null) {
            if (canMove(nextX, y))
                x = nextX;
            if (canMove(x, nextY))
                y = nextY;

            int windowWidth = environment.getWidth();
            int windowHeight = environment.getHeight();

            if (x < 0)
                x = 0;
            if (y < 0)
                y = 0;
            if (x + width > windowWidth)
                x = windowWidth - width;
            if (y + height > windowHeight)
                y = windowHeight - height;
        } else {
            x = nextX;
            y = nextY;
        }

        attackTimer -= dt;
        invulnerabilityTimer -= dt;
        super.update(dt);
    }

    private boolean canMove(float newX, float newY) {
        return environment.isWalkable(newX, newY) &&
                environment.isWalkable(newX + width - 1, newY) &&
                environment.isWalkable(newX, newY + height - 1) &&
                environment.isWalkable(newX + width - 1, newY + height - 1);
    }

    @Override
    public void render(Object g) {
        super.render(g);
        if (g instanceof Graphics2D && alive) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke oldStroke = g2d.getStroke();
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
            g2d.drawLine((int) (x + width / 2), (int) (y + height / 2), (int) aimX, (int) aimY);
            g2d.setStroke(oldStroke);
        }
    }

    @Override
    public Object getBounds() {
        bounds.x = x;
        bounds.y = y;
        return bounds;
    }
}
