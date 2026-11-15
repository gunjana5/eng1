package goosepatrol;
// so the geese know where i am
public class PlayerTracker {
    Player player;
    PlayerListener listener;
    float idleTime;
    boolean afk;
    float followX;
    float followY;
    public PlayerTracker(Player player) {
        // one tracker for the geese
        this.player = player;
        listener = null;
        idleTime = 0;
        afk = false;
        followX = player.x + player.w / 2;
        followY = player.y + player.h / 2;
    }
    public float getdX() {
        // chase x  centre of me
        return followX;
    }
    public float getDy() {
        // chase y  centre of me
        return followY;
    }
    public void followPlayer() {
        // lock onto my centre so the geese can chase
        if (player == null) {
            return;
        }
        followX = player.x + player.w / 2;
        followY = player.y + player.h / 2;
    }
    public void update(float dt) {
        // stand still too long and they stop chasing
        boolean moving = false;
        if (listener != null && listener.hadMove) {
            moving = true;
            listener.hadMove = false;
        } else if (player != null && player.movedThisFrame) {
            moving = true;
        }
        if (moving) {
            idleTime = 0;
            afk = false;
        } else {
            idleTime += dt;
            // 8s then the popup
            if (idleTime > 8) {
                afk = true;
            }
        }
    }
}
