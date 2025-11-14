import greenfoot.*;

public class Player extends Actor {
    public int square = 1;

    public Player(String imageName) {
        setImage(imageName);
        GreenfootImage img = getImage();
        img.scale(20, 35);
        setImage(img);
    }

    public void moveToSquare(int s) {
        square = s;
        setLocation(Board.tileCenterX(square), Board.tileCenterY(square));
    }
}
