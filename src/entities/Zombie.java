package entities;

import java.util.List;
import java.util.ArrayList;
import java.awt.Point;
import world.Environment;
import utils.Pathfinder;
import graphics.Animation;

public class Zombie extends Character {
    protected int attackDamage;
    protected List<Point> path;
    protected int pathIndex;
    protected String state;
    protected float attackCooldown;
    protected float attackTimer;
    protected float detectionRange;
    protected Player targetPlayer;
    private Pathfinder pathfinder;
    private float pathfindingTimer;
    private static final float PATHFINDING_UPDATE_INTERVAL = 0.5f;
    private static final int TILE_SIZE = 32;

    protected Animation idleAnim;
    protected Animation moveAnim;
    protected Animation attackAnim;
    protected Animation hurtAnim;
    protected Animation deadAnim;

    public Zombie(float x, float y, int width, int height, int maxHp, float speed, int attackDamage) {
        super(x, y, width, height, maxHp);
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.state = "idle";
        this.path = new ArrayList<>();
        this.pathIndex = 0;
        this.pathfinder = new Pathfinder();
        this.attackCooldown = 1.0f;
        this.attackTimer = 0;
        this.detectionRange = 500;
        this.pathfindingTimer = 0;

        loadAnimations();
    }

    protected void loadAnimations() {
        idleAnim = loadAnimationFromStrip("assets/skins/zombie/Idle.png", 0, 200);
        moveAnim = loadAnimationFromStrip("assets/skins/zombie/Walk.png", 0, 150);
        attackAnim = loadAnimationFromStrip("assets/skins/zombie/Attack.png", 0, 150);

        hurtAnim = loadAnimationFromStrip("assets/skins/zombie/Hurt.png", 0, 100);
        if (hurtAnim != null)
            hurtAnim.setLooping(false);

        deadAnim = loadAnimationFromStrip("assets/skins/zombie/Dead.png", 0, 100);
        if (deadAnim != null)
            deadAnim.setLooping(false);

        if (idleAnim != null) {
            currentAnimation = idleAnim;
        } else if (moveAnim != null) {
            currentAnimation = moveAnim;
        }
    }

    public void updateAI(Player player, Environment environment, float dt) {
        if (!alive)
            return;

        this.targetPlayer = player;
        float distanceToPlayer = (float) Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2));

        pathfindingTimer -= dt;

        if (distanceToPlayer < detectionRange) {
            if (path.isEmpty() || pathfindingTimer <= 0) {
                computePath(player, environment);
                pathfindingTimer = PATHFINDING_UPDATE_INTERVAL;
                pathIndex = 0;
            }

            if (!path.isEmpty() && pathIndex < path.size()) {
                Point nextNode = path.get(pathIndex);
                float nextX = nextNode.x * TILE_SIZE + TILE_SIZE / 2;
                float nextY = nextNode.y * TILE_SIZE + TILE_SIZE / 2;

                float dirX = nextX - x;
                float dirY = nextY - y;
                float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

                if (distance < 10) {
                    pathIndex++;
                    if (pathIndex >= path.size()) {
                        pathIndex = 0;
                        state = "idle";
                    }
                } else if (distance > 0) {
                    float vx = (dirX / distance) * speed;
                    float vy = (dirY / distance) * speed;

                    float nextPosX = x + vx * dt;
                    float nextPosY = y + vy * dt;
                    boolean canMoveDiag = environment.isWalkable(nextPosX, nextPosY);

                    if (canMoveDiag) {
                        velocityX = vx;
                        velocityY = vy;
                        state = "moving";
                        facingRight = (vx > 0);
                    } else {
                        boolean canMoveX = environment.isWalkable(x + vx * dt, y);
                        boolean canMoveY = environment.isWalkable(x, y + vy * dt);
                        if (canMoveX) {
                            velocityX = vx;
                            velocityY = 0;
                            state = "moving";
                            facingRight = (vx > 0);
                        } else if (canMoveY) {
                            velocityX = 0;
                            velocityY = vy;
                            state = "moving";
                        } else {
                            path.clear();
                            pathIndex = 0;
                            velocityX = 0;
                            velocityY = 0;
                            state = "idle";
                        }
                    }
                }

                if (distanceToPlayer < 50) {
                    state = "attacking";
                    velocityX = 0;
                    velocityY = 0;
                    facingRight = (player.getX() > x);

                    if (attackTimer <= 0) {
                        attack(player);
                        attackTimer = attackCooldown;
                    }
                }
            } else {
                state = "idle";
                velocityX = 0;
                velocityY = 0;
            }
        } else {
            state = "idle";
            velocityX = 0;
            velocityY = 0;
            path.clear();
            pathIndex = 0;
        }
    }

    private void computePath(Player player, Environment environment) {
        int[][] collisionMap = environment.getCollisionTiles();
        if (collisionMap == null)
            return;

        Point start = new Point((int) (x / TILE_SIZE), (int) (y / TILE_SIZE));
        Point goal = new Point((int) (player.getX() / TILE_SIZE), (int) (player.getY() / TILE_SIZE));

        path = pathfinder.findPath(start, goal, collisionMap);
    }

    public void attack(Player player) {
        if (player != null && !player.isDead()) {
            player.takeDamage(attackDamage);
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (!alive)
            return;
        hp -= damage;
        if (hurtAnim != null) {
            currentAnimation = hurtAnim;
            currentAnimation.reset();
        }
        if (hp <= 0)
            die();
    }

    public void die() {
        alive = false;
        state = "dead";
        velocityX = 0;
        velocityY = 0;
        if (deadAnim != null) {
            currentAnimation = deadAnim;
            currentAnimation.reset();
        }
    }

    public boolean isDeathAnimationFinished() {
        if (!state.equals("dead"))
            return false;
        if (deadAnim == null)
            return true;
        return deadAnim.isFinished();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getDetectionRange() {
        return detectionRange;
    }

    public void setDetectionRange(float range) {
        this.detectionRange = range;
    }

    @Override
    public void update(float dt) {
        if (!alive) {
            velocityX = 0;
            velocityY = 0;
        }

        x += velocityX * dt;
        y += velocityY * dt;
        attackTimer -= dt;

        boolean isHurting = (currentAnimation == hurtAnim && hurtAnim != null && !hurtAnim.isFinished());
        boolean isDying = (currentAnimation == deadAnim && deadAnim != null && !deadAnim.isFinished());

        if (!isHurting && !isDying) {
            if (state.equals("attacking")) {
                if (attackAnim != null)
                    currentAnimation = attackAnim;
            } else if (state.equals("moving") || velocityX != 0 || velocityY != 0) {
                if (moveAnim != null)
                    currentAnimation = moveAnim;
            } else {
                if (idleAnim != null)
                    currentAnimation = idleAnim;
            }
        }

        super.update(dt);
    }

    @Override
    public void render(Object g) {
    }

    @Override
    public Object getBounds() {
        bounds.x = x;
        bounds.y = y;
        return bounds;
    }
}
