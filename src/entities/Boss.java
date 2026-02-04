package entities;

import world.Environment;

public class Boss extends Zombie {

    public Boss(float x, float y, int hp, float speed, int attackDamage) {
        super(x, y, 128, 128, hp, speed, attackDamage);
    }

    @Override
    protected void loadAnimations() {
        idleAnim = loadAnimationFromStrip("assets/skins/boss/Idle.png", 0, 200);
        moveAnim = loadAnimationFromStrip("assets/skins/boss/Walk.png", 0, 150);
        attackAnim = loadAnimationFromStrip("assets/skins/boss/Attack.png", 0, 150);

        hurtAnim = loadAnimationFromStrip("assets/skins/boss/Hurt.png", 0, 100);
        if (hurtAnim != null)
            hurtAnim.setLooping(false);

        deadAnim = loadAnimationFromStrip("assets/skins/boss/Dead.png", 0, 100);
        if (deadAnim != null)
            deadAnim.setLooping(false);

        if (idleAnim != null) {
            currentAnimation = idleAnim;
        }
    }

    @Override
    public void updateAI(Player player, Environment environment, float dt) {
        if (!alive)
            return;

        this.targetPlayer = player;

        float centerX = x + width / 2;
        float centerY = y + height / 2;
        float playerCenterX = player.getX() + player.getWidth() / 2;
        float playerCenterY = player.getY() + player.getHeight() / 2;

        float distanceToPlayer = (float) Math
                .sqrt(Math.pow(playerCenterX - centerX, 2) + Math.pow(playerCenterY - centerY, 2));

        float attackRangeThreshold = 135.0f;

        if (distanceToPlayer < attackRangeThreshold) {
            state = "attacking";
            velocityX = 0;
            velocityY = 0;
            facingRight = (playerCenterX > centerX);

            if (attackTimer <= 0) {
                attack(player);
                attackTimer = attackCooldown;
            }
        } else {
            moveDirectly(playerCenterX, playerCenterY, dt, environment);
        }

        attackTimer -= dt;
    }

    private void moveDirectly(float targetX, float targetY, float dt, Environment environment) {
        float centerX = x + width / 2;
        float centerY = y + height / 2;
        float dirX = targetX - centerX;
        float dirY = targetY - centerY;

        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (length > 0) {
            float vx = (dirX / length) * speed;
            float vy = (dirY / length) * speed;

            velocityX = vx;
            velocityY = vy;
            state = "moving";
            facingRight = (vx > 0);
        }
    }
}
