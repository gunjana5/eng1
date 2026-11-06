package goosepatrol;
public class PlayerListener {
    PlayerController controller;
    boolean hadMove;
    public PlayerListener(PlayerController controller) {
        // sits between keys and the player
        this.controller = controller;
        hadMove = false;
    }
    public void onInputAction(String action) {
        // same as onInput  keys come in here first
        onInput(action);
    }
    public void onInput(String action) {
        // turn the string into a move or a consume
        if (action.equals("left")) {
            hadMove = true;
            controller.moveLeft();
        }
        if (action.equals("right")) {
            hadMove = true;
            controller.moveRight();
        }
        if (action.equals("up")) {
            hadMove = true;
            controller.moveUp();
        }
        if (action.equals("down")) {
            hadMove = true;
            controller.moveDown();
        }
        if (action.equals("use")) {
            controller.useItem();
        }
        if (action.equals("idle-x") && controller instanceof Player) {
            ((Player) controller).stopX();
        }
        if (action.equals("idle-y") && controller instanceof Player) {
            ((Player) controller).stopY();
        }
    }
}
