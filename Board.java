import greenfoot.*;
import java.util.*;

public class Board extends World {
    public static final int SIZE = 600;
    public static final int HUD   = 80;
    public static final int TILES = 10;
    public static final int TILE  = SIZE / TILES;
    
    private static final int HUD_Y = HUD / 2;
    private static final int HUD_TURN_X = SIZE / 4;
    private static final int HUD_ROLL_X = 3 * SIZE / 4;
    private static final int HUD_TEXT_OFFSET = 120;

    private Player red, green;
    private Dice dice;
    private int currentTurn = 0;
    private boolean waitingForMove = false;
    private Map<Integer,Integer> jumps = new HashMap<>();

    public Board() {
        super(SIZE, SIZE + HUD, 1);
        setPaintOrder(Num.class, Player.class, Dice.class);
        
        GreenfootImage bg = new GreenfootImage(SIZE, SIZE + HUD);
        bg.setColor(new Color(240,240,240));
        bg.fillRect(0, 0, SIZE, HUD);
        bg.drawImage(new GreenfootImage("board.png"), 0, HUD);
        setBackground(bg);
        
        addNumberActors();
        drawSnakesAndLadders();

        red   = new Player("red.png");
        green = new Player("green.png");
        addObject(red,   tileCenterX(1) - 15, tileCenterY(1) + 10);
        addObject(green, tileCenterX(1) + 15, tileCenterY(1) + 10);

        dice = new Dice();
        addObject(dice, SIZE / 2, HUD/2);

        initJumps();
    }

    public void act() {
        if (!waitingForMove && dice.hasRolled()) {
            int roll = dice.consumeRoll();
            Player p = (currentTurn==0) ? red : green;
            
            setTurnText("Turn: " + (currentTurn==0 ? "RED" : "GREEN"));
            
            waitingForMove = true;
            movePlayer(p, roll);
        }
    }

    private void movePlayer(Player p, int roll) {
        setRollText("Rolled: " + roll);
        int target = p.square + roll;

        if (target > 100) {
            endTurn();
            return;
        }

        p.moveToSquare(target);

        int after = jumps.getOrDefault(p.square, p.square);
        
        if (after != p.square) {
            p.moveToSquare(after);
        }

        if (p.square == 100) {
            showText((p == red ? "RED" : "GREEN") + " WINS!", SIZE / 2, HUD + SIZE / 2);
            Greenfoot.stop();
        } else {
            endTurn();
        }
    }

    private void endTurn() {
        currentTurn = 1 - currentTurn;
        waitingForMove = false;
        setTurnText("Turn: " + (currentTurn==0 ? "RED" : "GREEN"));
    }
    
    public static int tileCenterX(int square) {
        int idx = square - 1;
        int row = idx / 10;
        int col = idx % 10;
        
        if (row % 2 == 1) {
            col = 9 - col;
        }
        
        return col * TILE + TILE/2;
    }

    public static int tileCenterY(int square) {
        int idx = square - 1;
        int row = idx / 10;
        return HUD + (9 - row) * TILE + TILE/2;
    }

    private void initJumps() {
        jumps.put(5, 27);
        jumps.put(30, 52);
        jumps.put(54, 92);
        jumps.put(56, 76);
        jumps.put(61, 81);
    
        jumps.put(33, 12);
        jumps.put(58, 19);
        jumps.put(99, 84);
        jumps.put(36, 24);
    }

    private void drawSnakesAndLadders() {
        GreenfootImage bg = getBackground();
    
        drawLadder(bg, 5, 27);
        drawLadder(bg, 30, 52);
        drawLadder(bg, 54, 92);
        drawLadder(bg, 56, 76);
        drawLadder(bg, 61, 81);
    
        drawSnake(bg, 33, 12);
        drawSnake(bg, 58, 19);
        drawSnake(bg, 99, 84);
        drawSnake(bg, 36, 24);
    }
    
    private void drawLadder(GreenfootImage g, int fromSq, int toSq) {
        int x1 = tileCenterX(fromSq), y1 = tileCenterY(fromSq);
        int x2 = tileCenterX(toSq),   y2 = tileCenterY(toSq);
    
        drawThickLine(g, x1-6, y1-6, x2-6, y2-6, new Color(40,160,60), 6);
        drawThickLine(g, x1+6, y1+6, x2+6, y2+6, new Color(40,160,60), 6);
    
        int steps = 1 + (int)(distance(x1,y1,x2,y2) / (TILE*0.75));
        
        for (int i=1; i<steps; i++) {
            double t = i/(double)steps;
            int cx = (int)Math.round(lerp(x1,x2,t));
            int cy = (int)Math.round(lerp(y1,y2,t));
            drawThickLine(g, cx-10, cy-10, cx+10, cy+10, new Color(60,180,80), 5);
        }
    }
    
    private void drawSnake(GreenfootImage g, int fromSq, int toSq) {
        int x1 = tileCenterX(fromSq), y1 = tileCenterY(fromSq);
        int x2 = tileCenterX(toSq),   y2 = tileCenterY(toSq);
    
        drawThickLine(g, x1, y1, x2, y2, new Color(200,60,60), 10);
    
        fillCircle(g, x1, y1, 12, new Color(200,60,60));
        fillCircle(g, x2, y2, 7, new Color(200,60,60));
    }
    
    private void drawThickLine(GreenfootImage g, int x1, int y1, int x2, int y2, Color c, int thickness) {
        int steps = Math.max(2, (int)(distance(x1,y1,x2,y2) / 3));
        
        for (int i=0; i<=steps; i++) {
            double t = i/(double)steps;
            int x = (int)Math.round(lerp(x1,x2,t));
            int y = (int)Math.round(lerp(y1,y2,t));
            fillCircle(g, x, y, thickness/2, c);
        }
    }
    
    private void fillCircle(GreenfootImage g, int cx, int cy, int r, Color c) {
        g.setColor(c);
        g.fillOval(cx - r, cy - r, r*2, r*2);
    }
    
    private double distance(int x1,int y1,int x2,int y2) {
        int dx=x2-x1, dy=y2-y1; return Math.sqrt(dx*dx+dy*dy);
    }
    
    private double lerp(double a,double b,double t) {
        return a + (b-a)*t;
    }
    
    private void setTurnText(String s) {
        showText(s, dice.getX() - HUD_TEXT_OFFSET, HUD_Y);
    }
    
    private void setRollText(String s) {
        showText(s, dice.getX() + HUD_TEXT_OFFSET, HUD_Y);
    }

    private static class Num extends Actor {
        public Num(String text) {
            setImage(new GreenfootImage(text, 14, Color.BLACK, new Color(255,255,255,0)));
        }
    }
    
    private void addNumberActors() {
        String label;
        
        for (int i = 1; i <= 100; i++) {
            if (i == 1) {
                label = "       START";
            } else if (i == 100) {
                label = "       FINISH";
            } else {
                label = Integer.toString(i - 1);
            }
            
            Num n = new Num(label);
            int x = tileCenterX(i) - TILE/2 + 12;
            int y = tileCenterY(i) - TILE/2 + 12;
            addObject(n, x, y);
        }
    }
}
