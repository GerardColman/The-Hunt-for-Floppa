import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.GameObject;


/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 
 * Credits: Kelly Charles (2020)
 */
public class Viewer extends JPanel {
    private long CurrentAnimationTime = 0;

    Model gameworld = new Model();


    int attack_remaining = 0;

    Image enemyTexture;
    BufferedImage playerTextureFront;
    BufferedImage playerTextureBack;
    BufferedImage playerTextureLeft;
    BufferedImage playerTextureRight;
    BufferedImage playerAttackFrontTexture;
    BufferedImage playerAttackBackTexture;
    BufferedImage playerAttackRightTexture;
    BufferedImage playerAttackLeftTexture;
    BufferedImage wallTexture;
    BufferedImage floppaTexture;
    BufferedImage endScreenWinTexture;
    BufferedImage endScreenLoseTexture;

    public BufferedImage getWallTexture() {
        return wallTexture;
    }

    public void setWallTexture(BufferedImage wallTexture) {
        this.wallTexture = wallTexture;
    }

    public BufferedImage getPlayerAttackRightTexture() {
        return playerAttackRightTexture;
    }

    public void setPlayerAttackRightTexture(BufferedImage playerAttackRightTexture) {
        this.playerAttackRightTexture = playerAttackRightTexture;
    }

    public BufferedImage getPlayerAttackLeftTexture() {
        return playerAttackLeftTexture;
    }

    public void setPlayerAttackLeftTexture(BufferedImage playerAttackLeftTexture) {
        this.playerAttackLeftTexture = playerAttackLeftTexture;
    }

    public BufferedImage getPlayerAttackBackTexture() {
        return playerAttackBackTexture;
    }

    public void setPlayerAttackBackTexture(BufferedImage playerAttackBackTexture) {
        this.playerAttackBackTexture = playerAttackBackTexture;
    }


    public BufferedImage getPlayerAttackFrontTexture() {
        return playerAttackFrontTexture;
    }

    public void setPlayerAttackFrontTexture(BufferedImage playerAttackFrontTexture) {
        this.playerAttackFrontTexture = playerAttackFrontTexture;
    }

    Image backgroundTexture;

    double current_rotation = 0;

    public Viewer(Model World) {
        this.gameworld = World;
        loadTextures();
        // TODO Auto-generated constructor stub
    }

    public Viewer(LayoutManager layout) {
        super(layout);
        loadTextures();
        // TODO Auto-generated constructor stub
    }

    public Viewer(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        loadTextures();
        // TODO Auto-generated constructor stub
    }

    public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        loadTextures();
        // TODO Auto-generated constructor stub
    }

    private void loadTextures(){

        // Loading enemy assets
        File enemyFile = new File("res/enemy_skull.png");

        // Loading level assets
        File backgroundFile = new File("res/background_crater.png");
        File wallFile = new File("res/Wall.png");

        //Loading player assets
        File playerFileFront = new File("res/character_front.png");
        File playerFileBack = new File("res/character_back.png");
        File playerFileLeft = new File("res/character_left.png");
        File playerFileRight = new File("res/character_right.png");

        //Loading attack assets
        File playerAttackFront = new File("res/char_attack_front.png");
        File playerAttackBack = new File("res/char_attack_back.png");
        File playerAttackRight = new File("res/char_attack_right.png");
        File playerAttackLeft = new File("res/char_attack_left.png");

        //Loading Floppa assets
        File floppaFile = new File("res/floppa.png");

        //Loading End Screen Backgrounds
        File endScreenWin = new File("res/happyfloppa.jpg");
        File endScreenLose = new File("res/sadfloppa.png");

        try {
            enemyTexture = ImageIO.read(enemyFile);
            playerTextureFront = ImageIO.read(playerFileFront);
            playerTextureBack = ImageIO.read(playerFileBack);
            playerTextureLeft = ImageIO.read(playerFileLeft);
            playerTextureRight = ImageIO.read(playerFileRight);
            backgroundTexture = ImageIO.read(backgroundFile);
            playerAttackFrontTexture = ImageIO.read(playerAttackFront);
            playerAttackBackTexture = ImageIO.read(playerAttackBack);
            playerAttackRightTexture = ImageIO.read(playerAttackRight);
            playerAttackLeftTexture = ImageIO.read(playerAttackLeft);
            wallTexture = ImageIO.read(wallFile);
            floppaTexture = ImageIO.read(floppaFile);
            endScreenWinTexture = ImageIO.read(endScreenWin);
            endScreenLoseTexture = ImageIO.read(endScreenLose);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateview() {

        this.repaint();
        // TODO Auto-generated method stub

    }


    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        CurrentAnimationTime++; // runs animation time step


        //Draw player Game Object
        int x = (int) gameworld.getPlayer().getCentre().getX();
        int y = (int) gameworld.getPlayer().getCentre().getY();
        int width = (int) gameworld.getPlayer().getWidth();
        int height = (int) gameworld.getPlayer().getHeight();
        String texture = gameworld.getPlayer().getTexture();

        //Draw background
        if(gameworld.gameOverWon){
            drawBackgroundWon(g);
        }else if(gameworld.gameOverLoss){
            drawBackgroundLose(g);
        }else{
            drawBackground(g);
        }

        if(gameworld.getPlayer().isIs_attacking()){
            attack_remaining = 4;
        }

        //Draw player

        if(gameworld.drawPlayer){
            if(attack_remaining != 0){
                //Call draw attack instead
                drawPlayerAttack(x, y, width, height, texture, g, gameworld.getPlayer().getPlayer_rotation_angle());
            }else{
                drawPlayer(x, y, width, height, texture, g, gameworld.getPlayer().getPlayer_rotation_angle());
            }
        }


        if(attack_remaining > 0){
            attack_remaining--;
        }

        //Drawing Player Health
        if(gameworld.drawPlayerHealth){
            drawPlayerHealth(g);
        }

        if(gameworld.drawFinshHim){
            drawFinishHim(g);
        }

        if(gameworld.drawFloppa){
            drawFloppa((int)gameworld.getFloppaBoss().getCentre().getX(), (int)gameworld.getFloppaBoss().getCentre().getY(), (int)gameworld.getFloppaBoss().getWidth(), (int)gameworld.getFloppaBoss().getHeight(), gameworld.getFloppaBoss().getTexture(), g);
        }

        if(gameworld.drawFloppaIsHappyText){
            drawFloppaIsHappy(g);
        }

        if(gameworld.drawFloppaIsSadText){
            drawFloppaIsSad(g);
        }


        //Draw Bullets
        // change back
//        gameworld.getBullets().forEach((temp) ->
//        {
//            drawBullet((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g);
//        });

        //Draw Enemies
        gameworld.getEnemies().forEach((temp) ->
        {
            drawEnemies((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g);

        });

        //Draw walls
        gameworld.getWalls().forEach((temp) ->
        {
            drawWalls((int) temp.getCentre().getX(), (int) temp.getCentre().getY(), (int) temp.getWidth(), (int) temp.getHeight(), temp.getTexture(), g);

        });
    }

    private void drawEnemies(int x, int y, int width, int height, String texture, Graphics g) {
        //The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
        //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
        int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4) * 32); //slows down animation so every 10 frames we get another frame so every 100ms
        currentPositionInAnimation = 0;
        g.drawImage(enemyTexture, x, y, x + width, y + height, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);

    }

    private void drawWalls(int x, int y, int width, int height, String texture, Graphics g) {
        //The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
        //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
        int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4) * 32); //slows down animation so every 10 frames we get another frame so every 100ms
        currentPositionInAnimation = 0;
        g.drawImage(wallTexture, x, y, x + width, y + height, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);

    }

    private void drawBackground(Graphics g) {
        g.drawImage(backgroundTexture, 0, 0, 1280, 720, 0, 0, 1000, 1000, null);
    }

    private void drawBackgroundWon(Graphics g) {
        g.drawImage(endScreenWinTexture, 0, 0, 1280, 720, 0, 0, 1000, 1000, null);
    }

    private void drawBackgroundLose(Graphics g) {
        g.drawImage(endScreenLoseTexture, 0, 0, 1280, 720, 0, 0, 1000, 1000, null);
    }

//    private void drawBullet(int x, int y, int width, int height, String texture, Graphics g) {
//        File TextureToLoad = new File(texture);  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
//        try {
//            Image myImage = ImageIO.read(TextureToLoad);
//            //64 by 128
//            g.drawImage(myImage, x, y, x + width, y + height, 0, 0, 63, 127, null);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void drawPlayer(int x, int y, int width, int height, String texture, Graphics g, double rotation) {
        //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
        try{
            Graphics2D gfx = (Graphics2D) g;
            int currentPositionInAnimation = ((int) ((CurrentAnimationTime % 40) / 10)) * 16; //slows down animation so every 10 frames we get another frame so every 100ms
            BufferedImage playerTexture;
            //System.out.println("Player_direction = " + gameworld.getPlayer().getPlayer_direction());

            switch (gameworld.getPlayer().getPlayer_direction()) {
                case "FRONT":
                    playerTexture = playerTextureFront;
                    break;
                case "BACK":
                    playerTexture = playerTextureBack;
                    break;
                case "LEFT":
                    playerTexture = playerTextureLeft;
                    break;
                case "RIGHT":
                    playerTexture = playerTextureRight;
                    break;
                default:
                    playerTexture = playerTextureFront;
                    break;
            }

            BufferedImage image = playerTexture.getSubimage(currentPositionInAnimation, 0, 16, 32);

            AffineTransform transform = gfx.getTransform();
            AffineTransform tx = AffineTransform.getRotateInstance(rotation, x, y);
            gfx.transform(tx);
            gfx.drawImage(image, x - width / 2, y - height / 2, width, height, null);

            gfx.setTransform(transform);
            // g.drawImage(playerTexture, x, y, x + width, y + height, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);
        } catch(Exception e){
            e.printStackTrace();
        }


        //g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
        //Lighnting Png from https://opengameart.org/content/animated-spaceships  its 32x32 thats why I know to increament by 32 each time
        // Bullets from https://opengameart.org/forumtopic/tatermands-art
        // background image from https://www.needpix.com/photo/download/677346/space-stars-nebula-background-galaxy-universe-free-pictures-free-photos-free-images

    }

    private void drawPlayerAttack(int x, int y, int width, int height, String texture, Graphics g, double rotation) {
        //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
        try{
            Graphics2D gfx = (Graphics2D) g;
            int currentPositionInAnimation = ((int) ((CurrentAnimationTime % 40) / 10)) * 16; //slows down animation so every 10 frames we get another frame so every 100ms
            BufferedImage playerTexture;

            switch (gameworld.getPlayer().getPlayer_direction()) {
                case "FRONT":
                    playerTexture = playerAttackFrontTexture;
                    break;
                case "BACK":
                    playerTexture = playerAttackBackTexture;
                    break;
                case "LEFT":
                    playerTexture = playerAttackLeftTexture;
                    break;
                case "RIGHT":
                    playerTexture = playerAttackRightTexture;
                    break;
                default:
                    playerTexture = playerAttackFrontTexture;
                    break;
            }

            BufferedImage image = playerTexture.getSubimage(currentPositionInAnimation, 0, 16, 32);

            AffineTransform transform = gfx.getTransform();
            AffineTransform tx = AffineTransform.getRotateInstance(rotation, x, y);
            gfx.transform(tx);
            gfx.drawImage(image, x - width / 2, y - height / 2, width, height, null);

            gfx.setTransform(transform);
            // g.drawImage(playerTexture, x, y, x + width, y + height, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);
        } catch(Exception e){
            e.printStackTrace();
        }


        //g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer));
        //Lighnting Png from https://opengameart.org/content/animated-spaceships  its 32x32 thats why I know to increament by 32 each time
        // Bullets from https://opengameart.org/forumtopic/tatermands-art
        // background image from https://www.needpix.com/photo/download/677346/space-stars-nebula-background-galaxy-universe-free-pictures-free-photos-free-images

    }

    private void drawPlayerHealth(Graphics g){
        g.setFont(new Font("Verdana", Font.PLAIN, 30));
        g.setColor(new Color(255,255,255));
        g.drawString("Health: " + gameworld.getPlayer().getHealth(), 50, 70);
    }

    private void drawFinishHim(Graphics g){

        g.setFont(new Font("Verdana", Font.BOLD, 16));
        g.setColor(new Color(255,0,0));
        g.drawString("FINISH HIM", 500, 250);
    }

    private void drawFloppaIsHappy(Graphics g){

        g.setFont(new Font("Verdana", Font.BOLD, 30));
        g.setColor(new Color(0,0,0));
        g.drawString("You won the game! Floppa is happy!", 500, 250);
    }

    private void drawFloppaIsSad(Graphics g){

        g.setFont(new Font("Verdana", Font.BOLD, 30));
        g.setColor(new Color(0,0,0));
        g.drawString("You lost the game :( Floppa is sad.  You scored: " + gameworld.finalScore, 200, 50);
    }

    private void drawFloppa(int x, int y, int width, int height, String texture, Graphics g) {
        //The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
        //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
        int currentPositionInAnimation = ((int) (CurrentAnimationTime % 4) * 32); //slows down animation so every 10 frames we get another frame so every 100ms
        currentPositionInAnimation = 0;
        g.drawImage(floppaTexture, x, y, x + width, y + height, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);

    }


}


/*
 * 
 * 
 *              VIEWER HMD into the world                                                             
                                                                                
                                      .                                         
                                         .                                      
                                             .  ..                              
                               .........~++++.. .  .                            
                 .   . ....,++??+++?+??+++?++?7ZZ7..   .                        
         .   . . .+?+???++++???D7I????Z8Z8N8MD7I?=+O$..                         
      .. ........ZOZZ$7ZZNZZDNODDOMMMMND8$$77I??I?+?+=O .     .                 
      .. ...7$OZZ?788DDNDDDDD8ZZ7$$$7I7III7??I?????+++=+~.                      
       ...8OZII?III7II77777I$I7II???7I??+?I?I?+?+IDNN8??++=...                  
     ....OOIIIII????II?I??II?I????I?????=?+Z88O77ZZO8888OO?++,......            
      ..OZI7III??II??I??I?7ODM8NN8O8OZO8DDDDDDDDD8DDDDDDDDNNNOZ= ......   ..    
     ..OZI?II7I?????+????+IIO8O8DDDDD8DNMMNNNNNDDNNDDDNDDNNNNNNDD$,.........    
      ,ZII77II?III??????DO8DDD8DNNNNNDDMDDDDDNNDDDNNNDNNNNDNNNNDDNDD+.......   .
      7Z??II7??II??I??IOMDDNMNNNNNDDDDDMDDDDNDDNNNNNDNNNNDNNDMNNNNNDDD,......   
 .  ..IZ??IIIII777?I?8NNNNNNNNNDDDDDDDDNDDDDDNNMMMDNDMMNNDNNDMNNNNNNDDDD.....   
      .$???I7IIIIIIINNNNNNNNNNNDDNDDDDDD8DDDDNM888888888DNNNNNNDNNNNNNDDO.....  
       $+??IIII?II?NNNNNMMMMMDN8DNNNDDDDZDDNN?D88I==INNDDDNNDNMNNMNNNNND8:..... 
   ....$+??III??I+NNNNNMMM88D88D88888DDDZDDMND88==+=NNNNMDDNNNNNNMMNNNNND8......
.......8=+????III8NNNNMMMDD8I=~+ONN8D8NDODNMN8DNDNNNNNNNM8DNNNNNNMNNNNDDD8..... 
. ......O=??IIIIIMNNNMMMDDD?+=?ONNNN888NMDDM88MNNNNNNNNNMDDNNNMNNNMMNDNND8......
........,+++???IINNNNNMMDDMDNMNDNMNNM8ONMDDM88NNNNNN+==ND8NNNDMNMNNNNNDDD8......
......,,,:++??I?ONNNNNMDDDMNNNNNNNNMM88NMDDNN88MNDN==~MD8DNNNNNMNMNNNDND8O......
....,,,,:::+??IIONNNNNNNDDMNNNNNO+?MN88DN8DDD888DNMMM888DNDNNNNMMMNNDDDD8,.... .
...,,,,::::~+?+?NNNNNNNMD8DNNN++++MNO8D88NNMODD8O88888DDDDDDNNMMMNNNDDD8........
..,,,,:::~~~=+??MNNNNNNNND88MNMMMD888NNNNNNNMODDDDDDDDND8DDDNNNNNNDDD8,.........
..,,,,:::~~~=++?NMNNNNNNND8888888O8DNNNNNNMMMNDDDDDDNMMNDDDOO+~~::,,,.......... 
..,,,:::~~~~==+?NNNDDNDNDDNDDDDDDDDNNND88OOZZ$8DDMNDZNZDZ7I?++~::,,,............
..,,,::::~~~~==7DDNNDDD8DDDDDDDD8DD888OOOZZ$$$7777OOZZZ$7I?++=~~:,,,.........   
..,,,,::::~~~~=+8NNNNNDDDMMMNNNNNDOOOOZZZ$$$77777777777II?++==~::,,,......  . ..
...,,,,::::~~~~=I8DNNN8DDNZOM$ZDOOZZZZ$$$7777IIIIIIIII???++==~~::,,........  .  
....,,,,:::::~~~~+=++?I$$ZZOZZZZZ$$$$$777IIII?????????+++==~~:::,,,...... ..    
.....,,,,:::::~~~~~==+?II777$$$$77777IIII????+++++++=====~~~:::,,,........      
......,,,,,:::::~~~~==++??IIIIIIIII?????++++=======~~~~~~:::,,,,,,.......       
.......,,,,,,,::::~~~~==+++???????+++++=====~~~~~~::::::::,,,,,..........       
.........,,,,,,,,::::~~~======+======~~~~~~:::::::::,,,,,,,,............        
  .........,.,,,,,,,,::::~~~~~~~~~~:::::::::,,,,,,,,,,,...............          
   ..........,..,,,,,,,,,,::::::::::,,,,,,,,,.,....................             
     .................,,,,,,,,,,,,,,,,.......................                   
       .................................................                        
           ....................................                                 
               ....................   .                                         
                                                                                
                                                                                
                                                                 GlassGiant.com
                                                                 */
