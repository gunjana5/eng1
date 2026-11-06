package goosepatrol;
// keys call these  player is the one that actually moves
public interface PlayerController {
    // wasd left
    void moveLeft();
    // wasd right
    void moveRight();
    // wasd up
    void moveUp();
    // wasd down
    void moveDown();
    // walk onto an item
    void pickUp();
    // E  after 1/2 has picked the slot
    void useItem();
}
