import greenfoot.*;

public class Dice extends Actor {
    private int lastRoll = 0;

    public Dice() {
        drawFace(0);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            lastRoll = Greenfoot.getRandomNumber(6) + 1;
            drawFace(lastRoll);
        }
    }

    public boolean hasRolled() {
        return lastRoll > 0;
    }

    public int consumeRoll() {
        int r = lastRoll;
        lastRoll = 0;
        drawFace(0);
        return r;
    }

    private void drawFace(int n) {
        GreenfootImage img = new GreenfootImage(60, 60);
        
        img.setColor(new Color(250,250,250));
        img.fill();
        
        img.setColor(Color.BLACK);
        img.drawRect(0,0,59,59);
        
        img.setFont(img.getFont().deriveFont(22f));
        String text = (n==0) ? "Roll" : Integer.toString(n);
        img.drawString(text, 12, 36);
        
        setImage(img);
    }
}
