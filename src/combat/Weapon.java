package combat;

import entities.Entity;
import entities.Zombie;
import world.Level;

public class Weapon {
    private String name;
    private int damage;
    private float cooldown;
    private String type;
    private String ammoType;
    private float range;

    public Weapon(String name, int damage, float cooldown, String type) {
        this.name = name;
        this.damage = damage;
        this.cooldown = cooldown;
        this.type = type;
        this.range = type.equals("melee") ? 50 : 300;
        this.ammoType = type.equals("ranged") ? "bullet" : null;
    }

    public Weapon(String name, int damage, float cooldown, String type, String ammoType) {
        this(name, damage, cooldown, type);
        this.ammoType = ammoType;
    }

    public void attack(Entity origin, float targetX, float targetY, Level level) {
        if (type.equals("melee")) {
            if (level != null) {
                float reach = 50;
                for (Zombie z : level.getZombies()) {
                    float dx = origin.getX() - z.getX();
                    float dy = origin.getY() - z.getY();
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (z.isAlive() && dist < reach) {
                        z.takeDamage(damage);
                    }
                }
            }
        } else if (type.equals("ranged")) {
            rangedAttack(origin, targetX, targetY, level);
        }
    }

    protected void rangedAttack(Entity origin, float targetX, float targetY, Level level) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(String ammoType) {
        this.ammoType = ammoType;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
