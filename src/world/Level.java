package world;

import java.util.ArrayList;
import java.util.List;
import entities.Player;
import entities.Zombie;
import entities.Boss;
import utils.Pathfinder;
import utils.Rectangle;
import combat.Projectile;

public class Level {
    private int id;
    private Environment environment;
    private TileMap tileMap;
    private Player player;
    private List<Zombie> zombies;
    private Pathfinder pathfinder;
    private float zombieSpawnTimer;
    private float zombieSpawnInterval;
    private int zombiesSpawned;
    private int zombiesRequired;
    private boolean levelComplete;
    private int wave;
    private List<Projectile> projectiles = new ArrayList<>();

    public Level(int id, Environment environment, Player player) {
        this.id = id;
        this.environment = environment;
        this.player = player;
        this.zombies = new ArrayList<>();
        this.pathfinder = new Pathfinder();
        this.zombieSpawnTimer = 0;
        this.zombieSpawnInterval = 2.0f;
        this.zombiesSpawned = 0;
        this.zombiesRequired = 10;
        this.levelComplete = false;
        this.wave = 1;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public void update(float dt) {
        if (levelComplete)
            return;

        environment.update(dt);
        player.setEnvironment(environment);
        player.update(dt);

        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);
            if (zombie.isAlive()) {
                zombie.updateAI(player, environment, dt);
            }
            zombie.update(dt);
            if (!zombie.isAlive() && zombie.isDeathAnimationFinished()) {
                zombies.remove(i);
                i--;
            }
        }

        environment.checkCollisions(player, zombies);
        updateProjectiles(dt);
        checkProjectileCollisions();

        zombieSpawnTimer -= dt;
        if (zombieSpawnTimer <= 0 && zombiesSpawned < zombiesRequired) {
            spawnZombie();
            zombieSpawnTimer = zombieSpawnInterval;
        }

        if (zombiesSpawned >= zombiesRequired && zombies.isEmpty()) {
            completeLevel();
        }

        if (player.isDead()) {
            if (player.getLives() <= 0) {
                gameOver();
            }
        }
    }

    public void render(Object g) {
        environment.render(g);
        for (Projectile p : projectiles)
            p.render(g);
        player.render(g);
        for (Zombie zombie : zombies)
            zombie.render(g);
    }

    public void spawnZombie() {
        float spawnX, spawnY;
        boolean safeDistance = false;
        int attempts = 0;

        do {
            spawnX = (float) (Math.random() * environment.getWidth());
            spawnY = (float) (Math.random() * environment.getHeight());
            float dist = (float) Math.sqrt(Math.pow(spawnX - player.getX(), 2) + Math.pow(spawnY - player.getY(), 2));
            if (environment.isWalkable(spawnX, spawnY) && dist > 200) {
                safeDistance = true;
            }
            attempts++;
        } while (!safeDistance && attempts < 100);

        Zombie zombie;
        if (wave == 5) {
            zombie = new Boss(spawnX, spawnY, 1000, 90, 35);
        } else {
            int hp = 20 + (wave - 1) * 5;
            float speed = 50 + (wave - 1) * 10;
            int damage = 10 + (wave - 1) * 2;
            zombie = new Zombie(spawnX, spawnY, 32, 32, hp, speed, damage);
        }

        zombies.add(zombie);
        zombiesSpawned++;
    }

    public void completeLevel() {
        levelComplete = true;
        wave++;
        zombiesSpawned = 0;
        zombieSpawnTimer = 0;
        if (wave == 5) {
            zombiesRequired = 1;
        } else {
            zombiesRequired = 10 + (wave - 1) * 5;
        }
    }

    public void gameOver() {
        levelComplete = true;
    }

    public void startNextWave() {
        levelComplete = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void removeZombie(Zombie zombie) {
        zombies.remove(zombie);
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    private void updateProjectiles(float dt) {
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.update(dt);
            if (!environment.isWalkable(p.getX(), p.getY())) {
                p.setActive(false);
            }
            if (!p.isActive()) {
                projectiles.remove(i);
                i--;
            }
        }
    }

    private void checkProjectileCollisions() {
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if (!p.isActive())
                continue;
            for (Zombie z : zombies) {
                if (z.isAlive() && z.getBounds() instanceof Rectangle && p.getBounds() instanceof Rectangle) {
                    Rectangle pRect = (Rectangle) p.getBounds();
                    Rectangle zRect = (Rectangle) z.getBounds();
                    if (pRect.intersects(zRect)) {
                        z.takeDamage(p.getDamage());
                        p.setActive(false);
                        break;
                    }
                }
            }
        }
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public int getWave() {
        return wave;
    }

    public int getZombiesSpawned() {
        return zombiesSpawned;
    }

    public int getZombiesRequired() {
        return zombiesRequired;
    }

    public int getZombiesRemaining() {
        return zombies.size();
    }
}
