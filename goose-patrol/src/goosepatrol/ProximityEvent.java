package goosepatrol;
public class ProximityEvent {
    String id;
    float x;
    float y;
    float proximityRadius;
    boolean triggered;
    public ProximityEvent(String id, float x, float y, float proximityRadius) {
        // hidden until you walk in the circle
        this.id = id;
        this.x = x;
        this.y = y;
        this.proximityRadius = proximityRadius;
        this.triggered = false;
    }
    public boolean inRange(Player player) {
        // hidden till you walk close enough
        float px = player.x + player.w / 2;
        float py = player.y + player.h / 2;
        float dx = px - x;
        float dy = py - y;
        return dx * dx + dy * dy <= proximityRadius * proximityRadius;
    }
}
