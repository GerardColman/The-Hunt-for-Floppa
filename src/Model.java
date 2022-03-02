import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Random;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

import javax.swing.*;

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
 */
public class Model {

    private GameObject Player;

    public GameObject getFloppaBoss() {
        return FloppaBoss;
    }

    public void setFloppaBoss(GameObject floppaBoss) {
        FloppaBoss = floppaBoss;
    }

    private GameObject FloppaBoss;
    private Controller controller = Controller.getInstance();
    private CopyOnWriteArrayList<GameObject> EnemiesList = new CopyOnWriteArrayList<GameObject>();
    private CopyOnWriteArrayList<GameObject> BulletList = new CopyOnWriteArrayList<GameObject>();
    private CopyOnWriteArrayList<GameObject> WallList = new CopyOnWriteArrayList<GameObject>();
    private CopyOnWriteArrayList<GameObject> SpawnPointList = new CopyOnWriteArrayList<GameObject>();
    private CopyOnWriteArrayList<Point3f> SpawnPointListPoints = new CopyOnWriteArrayList<>();
    private JLabel healthDisplay;

    int enemy_speed = 3;
    int Score = 1;
    public int finalScore;

    private int frame_count;
    private int spawnRate = 1000;
    public boolean gameOverWon = false;
    public boolean gameOverLoss = false;
    private boolean playerIsImmune = false;
    private int immunityCountdown = 10;

    public boolean drawFinshHim = false;
    public boolean drawFloppa = false;
    public boolean drawPlayerHealth = true;
    public boolean drawPlayer = true;
    public boolean drawFloppaIsSadText = false;
    public boolean drawFloppaIsHappyText = false;

    public CopyOnWriteArrayList<GameObject> getSpawnPointList() {
        return SpawnPointList;
    }

    public void setSpawnPointList(CopyOnWriteArrayList<GameObject> spawnPointList) {
        SpawnPointList = spawnPointList;
    }

    public Model() {


        //setup game world
        //Player


        //Enemies  starting with four

//        EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float) Math.random() * 50 + 500), 0, 0)));
//        EnemiesList.add(new GameObject("res/floppa.png", 50, 50, new Point3f(((float) Math.random() * 100 + 500), 0, 0)));
//        EnemiesList.add(new GameObject("res/floppa.png", 50, 50, new Point3f(((float) Math.random() * 100 + 400), 0, 0)));


        loadLevel(1);

    }

    private void loadLevel(int level_number){
        switch (level_number){
            case 1:
                loadLevelOne();
                break;
        }
    }

    private void loadLevelOne(){
        Player = new GameObject("res/Lightning.png", 32, 64, new Point3f(1000, 500, 0));
        FloppaBoss = new GameObject("res/Lightning.png", 32, 64, new Point3f(500, 300, 0));

        healthDisplay = new JLabel();
        healthDisplay.setBounds(200, 200, 400, 50);
        healthDisplay.setVisible(true);

        // Rendering top and bottom border walls
        for(int i = 0;i<10;i++){
            WallList.add(new GameObject("res/Wall.png", 128, 32, new Point3f(i*128, 0, 0)));
            WallList.add(new GameObject("res/Wall.png", 128, 32, new Point3f(i*128, 650, 0)));
        }

        // Rendering left and right border walls
        for(int i = 0;i<6;i++){
            WallList.add(new GameObject("res/Wall.png", 32, 128, new Point3f(0, i*128, 0)));
            WallList.add(new GameObject("res/Wall.png", 32, 128, new Point3f(1232, i*128, 0)));
        }


        // Top Side
        SpawnPointListPoints.add(new Point3f(350, 100, 0));
        SpawnPointListPoints.add(new Point3f(600, 100, 0));
        SpawnPointListPoints.add(new Point3f(850, 100, 0));

        // Right Side
        SpawnPointListPoints.add(new Point3f(1100, 200, 0));
        SpawnPointListPoints.add(new Point3f(1100, 400, 0));

        // Bottom Side
        SpawnPointListPoints.add(new Point3f(350, 600, 0));
        SpawnPointListPoints.add(new Point3f(600, 600, 0));
        SpawnPointListPoints.add(new Point3f(850, 600, 0));

        // Left Side
        SpawnPointListPoints.add(new Point3f(100, 200, 0));
        SpawnPointListPoints.add(new Point3f(100, 400, 0));

        initEnemies();

    }

    private void initEnemies(){
        Random random = new Random();
        int rand;
        ArrayList<Integer> previous_numbers = new ArrayList<>();

        for(int i = 0;i<20;i++){
//            while (true) {
//                rand = random.nextInt(SpawnPointListPoints.size());
//                if (!previous_numbers.contains(rand)) {
//                    previous_numbers.add(rand);
//                    break;
//                }
//            }

            rand = random.nextInt(SpawnPointListPoints.size());

            GameObject temp_enemy = new GameObject("res/UFO.png", 32, 32, SpawnPointListPoints.get(rand));

            if (temp_enemy.getCentre().getY() == 100) {
                temp_enemy.player_direction = "DOWN";
            } else if (temp_enemy.getCentre().getY() == 600) {
                temp_enemy.player_direction = "UP";
            } else if (temp_enemy.getCentre().getX() == 100) {
                temp_enemy.player_direction = "RIGHT";
            } else if (temp_enemy.getCentre().getX() == 1100) {
                temp_enemy.player_direction = "LEFT";
            }

            EnemiesList.add(temp_enemy);
        }

    }

    // This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly.
    public void gamelogic() {

        // Player Logic first
        playerLogic();

        // Enemy Logic next
        enemyLogic();

        // Bullets move next
        bulletLogic();

        // interactions between objects
        gameLogic();

    }

    private void gameLogic() {

        collision();
        scoreIncrease();
        gameOverCheck();
        displayUI();
        immunity();
        spawnFloppa();
    }

    private void spawnFloppa(){
        if(Score == 999){

            FloppaBoss.setHealth(1);
            EnemiesList.clear();
            enemy_speed = 0;
            drawFinshHim = true;
            drawFloppa = true;
        }
    }

    private void immunity(){
        if(immunityCountdown > 0){
            immunityCountdown--;
        }else{
            playerIsImmune = false;
        }
    }

    private void displayUI(){
        healthDisplay.setText("Health: " + Player.getHealth());
    }

    private void gameOverCheck(){

        //Player dies
        if(Player.getHealth() <= 0){
            EnemiesList.clear();
            WallList.clear();
            drawPlayer = false;
            drawPlayerHealth = false;
            drawFloppa = false;
            drawFinshHim = false;
            gameOverLoss = true;
            drawFloppaIsSadText = true;
            finalScore = Score;
        }

        //Floppa dies
        if(FloppaBoss.getHealth() <= 0){
            EnemiesList.clear();
            WallList.clear();
            drawPlayer = false;
            drawFloppa = false;
            drawPlayerHealth = false;
            drawFinshHim = false;
            gameOverWon = true;
            drawFloppaIsHappyText = true;
            Score = 1000;
        }
    }

    private void scoreIncrease(){
        //System.out.println("Spawn rate: " + spawnRate);
        if(frame_count == 5){
            if(!(gameOverLoss || gameOverWon)){
                Score++;
            }

            if(spawnRate < 1000){
                spawnRate++;
            }
            frame_count = 0;
        }else{
            frame_count++;
        }

    }

    private void collision(){
        playerEnemyCollision();
        playerWallCollision();
        //enemyWallCollision();
    }

    private void enemyWallCollision(){

        for(GameObject enemy : EnemiesList){
            for(GameObject wall : WallList){
                if(Math.abs(wall.getCentre().getX() - enemy.getCentre().getX()) < wall.getWidth()
                    && Math.abs(wall.getCentre().getX() - enemy.getCentre().getY()) < wall.getHeight()) {
                    System.out.println("ENEMY REMOVED");
                    EnemiesList.remove(enemy);
                }
            }
        }
    }

    private void playerEnemyCollision(){
        for(GameObject temp : EnemiesList) {
            if (Math.abs(temp.getCentre().getX() - Player.getCentre().getX()) < temp.getWidth()
                    && Math.abs(temp.getCentre().getY() - Player.getCentre().getY()) < temp.getHeight()) {
                if(!playerIsImmune){
                    Player.health--;
                    playerIsImmune = true;
                    immunityCountdown = 10;
                }

            }
        }
    }

    private void playerWallCollision(){

        // North face of wall
        for(GameObject wall : WallList){
            if((wall.getCentre().getY() + wall.getHeight() + 12 <= (Player.getCentre().getY() + Player.getHeight()))
                    && !(Player.getCentre().getY() > wall.getCentre().getY() + wall.getHeight())
                    && (wall.getCentre().getX() <=  Player.getCentre().getX())
                    && (wall.getCentre().getX() + wall.getWidth()) >= (Player.getCentre().getX() + Player.getWidth())){

                Player.getCentre().ApplyVector(new Vector3f(0, Player.getSpeed(), 0));
            }

            // South face of wall
            if((wall.getCentre().getY() + (wall.getHeight() + 12) >= (Player.getCentre().getY()))
                    && !(Player.getCentre().getY() < wall.getCentre().getY())
                    && (wall.getCentre().getX() <=  Player.getCentre().getX())
                    && (wall.getCentre().getX() + wall.getWidth()) >= (Player.getCentre().getX() + Player.getWidth())){

                Player.getCentre().ApplyVector(new Vector3f(0, (Player.getSpeed() * -1), 0));
            }

            // West face of wall
            if((wall.getCentre().getX() <= Player.getCentre().getX() + Player.getWidth() - 15)
                    && !(wall.getCentre().getX() + wall.getWidth() < Player.getCentre().getX())
                    && (wall.getCentre().getY() <= Player.getCentre().getY())
                    && (wall.getCentre().getY() + wall.getHeight() >= Player.getCentre().getY())){
                Player.getCentre().ApplyVector(new Vector3f((Player.getSpeed() * -1), 0, 0));
            }

            // East face of wall
            if((wall.getCentre().getX() + wall.getWidth() >= Player.getCentre().getX() - 10)
                    && !(wall.getCentre().getX() > Player.getCentre().getX())
                    && (wall.getCentre().getY() <= Player.getCentre().getY())
                    && (wall.getCentre().getY() + wall.getHeight() >= Player.getCentre().getY())){
                Player.getCentre().ApplyVector(new Vector3f(Player.getSpeed(), 0, 0));

            }
        }
    }

    private void enemyLogic() {
        for(GameObject enemy : EnemiesList){
            moveEnemy(enemy);
            boolean wasEnemyRemoved = despawnEnemy(enemy);

            if(wasEnemyRemoved){
                spawnEnemy();
                //System.out.println("SHOULD SPAWN ENEMY");
            }

        }
    }


    private boolean despawnEnemy(GameObject enemy){
        boolean wasEnemyRemoved = false;

        if(enemy.getCentre().getX() <= 0.0f || enemy.getCentre().getX() >= 1280.0f){
            EnemiesList.remove(enemy);
            wasEnemyRemoved = true;
            //System.out.println("ENEMY REMOVED");
        }

        if(enemy.getCentre().getY() <= 0.0f || enemy.getCentre().getY() >= 700.0f){
            EnemiesList.remove(enemy);
            wasEnemyRemoved = true;
            //System.out.println("ENEMY REMOVED");
        }

        return wasEnemyRemoved;
    }

    private void moveEnemy(GameObject enemy){
        switch (enemy.player_direction){
            case "UP":
                enemy.getCentre().ApplyVector(new Vector3f(0, enemy_speed, 0));
                break;
            case "DOWN":
                enemy.getCentre().ApplyVector(new Vector3f(0, -enemy_speed, 0));
                break;
            case "RIGHT":
                enemy.getCentre().ApplyVector(new Vector3f(enemy_speed, 0, 0));
                break;
            case "LEFT":
                enemy.getCentre().ApplyVector(new Vector3f(-enemy_speed, 0, 0));
                break;
            default:
                break;
        }
    }

    private void spawnEnemy(){
        Random random = new Random();
//        ArrayList previous_numbers = new ArrayList();
//
//        while(true) {
//            rand = random.nextInt(SpawnPointListPoints.size());
//            if(!previous_numbers.contains(rand)){
//                previous_numbers.add(rand);
//                break;
//            }
//        }

        GameObject temp_enemy = new GameObject("res/UFO.png", 32, 32, new Point3f(getRandomNumber(1, 1280), getRandomNumber(1, 720), 0));

        int rand = random.nextInt(4);
        //System.out.println(temp_enemy.getCentre().getX() + " : " + temp_enemy.getCentre().getY());
        switch (rand){
            case 0:
                temp_enemy.player_direction = "UP";
                break;
            case 1:
                temp_enemy.player_direction = "DOWN";
                break;
            case 2:
                temp_enemy.player_direction = "RIGHT";
                break;
            case 3:
                temp_enemy.player_direction = "LEFT";
                break;
        }

        float chanceToSpawn = random.nextInt(1000);
        if(chanceToSpawn <= spawnRate){
            EnemiesList.add(temp_enemy);
        }
    }

    private float getRandomNumber(int min, int max){
        return (float) ((Math.random() * (max - min)) + min);
    }

    private void bulletLogic() {
        // TODO Auto-generated method stub
        // move bullets

        for (GameObject temp : BulletList) {
            //check to move them

            temp.getCentre().ApplyVector(new Vector3f(0, 1, 0));
            //see if they hit anything

            //see if they get to the top of the screen ( remember 0 is the top
            if (temp.getCentre().getY() == 0) {
                BulletList.remove(temp);
            }
        }

    }

    private void playerLogic() {

        // Resets rotation angle from previous frame
        // player_rotation_angle = 0;

        // smoother animation is possible if we make a target position  // done but may try to change things for students

        //check for movement and if you fired a bullet

        if (Controller.getInstance().isKeyAPressed()) {
            Player.getCentre().ApplyVector(new Vector3f((Player.getSpeed() * -1), 0, 0));
            Player.setPlayer_direction("LEFT");
        }

        if (Controller.getInstance().isKeyDPressed()) {
            Player.getCentre().ApplyVector(new Vector3f(Player.getSpeed(), 0, 0));
            Player.setPlayer_direction("RIGHT");
        }

        if (Controller.getInstance().isKeyWPressed()) {
            Player.getCentre().ApplyVector(new Vector3f(0, Player.getSpeed(), 0));
            Player.setPlayer_direction("BACK");
        }

        if (Controller.getInstance().isKeySPressed()) {
            Player.getCentre().ApplyVector(new Vector3f(0, (Player.getSpeed() * -1), 0));
            Player.setPlayer_direction("FRONT");
        }

        if (Controller.getInstance().isKeySpacePressed()) {
            Player.setIs_attacking(true);
            attack();
        }

        if (!Controller.getInstance().isKeySpacePressed()) {
            Player.setIs_attacking(false);
        }

        if (Controller.getInstance().isKeyUpPressed()){

        }

        if (Controller.getInstance().isKeyLeftPressed()){
            Player.player_rotation_angle += -0.1;
        }

        if (Controller.getInstance().isKeyDownPressed()){

        }

        if (Controller.getInstance().isKeyRightPressed()){
            Player.player_rotation_angle += 0.1;
        }

        if(Controller.getInstance().isKeyShiftPressed()){
            Player.setSpeed(9);
        }

        if(!Controller.getInstance().isKeyShiftPressed()){
            Player.setSpeed(5);
        }
    }

    private void attack() {
        /*
        1. Create sword vertex
        2. Check if it collides
        3. Destroy enemy
         */
        Vector3f vector;
        switch (Player.getPlayer_direction()){
            case "FRONT":
                vector = new Vector3f(0,-192,0);
            case "BACK":
                vector = new Vector3f(0,192,0);
            case "LEFT":
                vector = new Vector3f(-128,0,0);
            case "RIGHT":
                vector = new Vector3f(128,0,0);
            default:
                vector = new Vector3f(0,0,0);
        }
        GameObject swordHitBox = new GameObject(Player.getCentre().PlusVector(vector), 128, 128);
        for(GameObject enemy : EnemiesList){
            if(Math.abs(enemy.getCentre().getX() - swordHitBox.getCentre().getX()) < enemy.getWidth() && Math.abs(enemy.getCentre().getY() - swordHitBox.getCentre().getY()) < enemy.getHeight()){
                EnemiesList.remove(enemy);
                Score += 10;
            }
        }

        if(Math.abs(FloppaBoss.getCentre().getX() - swordHitBox.getCentre().getX()) < FloppaBoss.getWidth() && Math.abs(FloppaBoss.getCentre().getY() - swordHitBox.getCentre().getY()) < FloppaBoss.getHeight()){
            FloppaBoss.health--;
        }
    }

    public GameObject getPlayer() {
        return Player;
    }

    public CopyOnWriteArrayList<GameObject> getEnemies() {
        return EnemiesList;
    }

    public CopyOnWriteArrayList<GameObject> getBullets() {
        return BulletList;
    }

    public CopyOnWriteArrayList<GameObject> getWalls() { return WallList; }

    public int getScore() {
        return Score;
    }


}


/* MODEL OF your GAME world 
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWNNNXXXKKK000000000000KKKXXXNNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXXK0OOkkxddddooooooolllllllloooooooddddxkkOO0KXXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNXK0OkxddooolllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkdooollllllllooddddxxxkkkOOOOOOOOOOOOOOOkkxxdddooolllllllllllooddxO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllloddxkO0KKXNNNNWWWWWWMMMMMMMMMMMMMWWWWNNNXXK00Okkxdoollllllllloodxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOxdooolllllodxkO0KXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0OkxdolllllolloodxOKXWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdoolllllodxO0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXKOkdolllllllloodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllooxk0KNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdolllllllllodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xdolllllodk0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMMWN0kdolllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWMMMMMMMMMMWNXKOkkkk0WMMMMMMMMMMMMWNKkdolllloololodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdolllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0kxk0KNWWWWNX0OkdoolllooONMMMMMMMMMMMMMMMWXOxolllllllollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0xooollloodkOOkdoollllllllloxXWMMMMMMMMMMMMMMMWXkolllllllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0koolllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllox0XWWMMMMMMMMMWNKOdoloooollllllllllllok0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoolllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxllolllllllllllllllllloollllllolodxO0KXNNNXK0kdoooxO0K0Odolllollllllllox0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllloolllllllllllllllllllolllloddddoolloxOKNWMMMWNKOxdolollllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolllolllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllolllllloxkxolllllllllllllllllolllllllllllllxKWMWWWNNXXXKKOxoollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWXOdollllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllxKNKOxooollolllllllllllllllllllolod0XX0OkxdddoooodoollollllllllllodOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMN0xollllllllllllllllllllllld0NMMMMMMMMMMMMMMMMMMMMMMMWWNKKNMMMMMMMMMMMW0dlllllllllokXWMWNKkoloolllllllllllllllllllolokkxoolllllllllllllollllllllllllllox0NMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMWNKOxdookNMMMMMMMMMWXkollllllodx0NWMMWWXkolooollllllllllllllllllllooollllllllllllllolllllllllllloooolloxKWMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWXOdllllllllllllllooollllllllollld0WMMMMMMMMMMMMMMMMWXOxollllloOWMMMMMMMWNkollloodxk0KKXXK0OkdoollllllllllllllllllllllllllllllollllllllloollllllollllllllllllldOXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMN0xolllllllllllolllllllllllloodddddONMMMMMMMMMMMMMMMNOdolllllllokNMMMMMMWNkolllloddddddoooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllox0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWXkolllllllllllllllllllodxxkkO0KXNNXXXWMMMMMMMMMMMMMMNkolllllllllod0NMMMMMNOollllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMWKxollllllllllllllllllox0NWWWWWMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllookKNWWNOolollloolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMN0dlllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloolllollllolloxO0Odllllllllllllllllllllllllllllllllllllllllllllollllllllllllllllllllllllllllllllllllllllllllllld0NWMMMMMMMMMMMMM
MMMMMMMMMMMMMXkolllllllllllllllllolllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXOO0KKOdollllllllllooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONWMMMMMMMMMMMM
MMMMMMMMMMMWXkollllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWMMMMWNKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMM
MMMMMMMMMMWKxollllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMMM
MMMMMMMMMWKxollllllllllllodxkkkkkkkO0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKOkO0KK0OkdolllllloolllllllllllloooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxKWMMMMMMMMM
MMMMMMMMWKxllllllllllolodOXWWWWWWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolloooollllllllllllllllloollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMM
MMMMMMMWKxlllllllllollokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxololllllllooolloollllloolloooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMM
MMMMMMWXxllllllllooodkKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdloollllllllllololodxxddddk0KK0kxxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWMMMMMM
MMMMMMXkolllllodk0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdllollllllllllllodOXWWNXXNWMMMMWWWNX0xolollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokNMMMMMM
MMMMMNOollllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dooollllllllllllodOXNWWWWWWMMMMMMMMMWXOddxxddolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMM
MMMMW0dllllodKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKKK0kdlllllllllllloodxxxxkkOOKNWMMMMMMWNNNNNXKOkdooooollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMM
MMMWKxllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllollllllllllllllllodOKXWMMMMMMMMMMMMWNXKK0OOkkkxdooolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxKWMMM
MMMNkollllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXOdlllllolllllllllllloloolllooxKWMMMMMMMMMMMMMMMMMMMMWWWNXKOxoollllllllllllllllllllllllllllllllllllllllllllllllllllllllllolokNMMM
MMW0ollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOOkxdollllllllllllllllllllllllllllox0NWMMMMWWNNXXKKXNWMMMMMMMMMWNKOxolllolllllllllllllllllllllllllllllllllllllllllllllllllllllllo0WMM
MMXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkolllllllllllllllllllllllllllllllllllooxO000OkxdddoooodkKWMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWM
MWOollllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXkollllllllllllllllllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloOWM
MXxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXkollllllllllllllllllllllllllllllllllooollllllllllllllllllold0WMMMMMMWN0dolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXM
W0ollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkdolllllllllllllllllllllllllllllllllllllllllllllllllolllllllllokKXNWWNKkollllllllloxdollllllllolllllllllllllllllllllllllolllllllllllllolo0W
NkllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllodxkkdoolollllllllxKOolllllllllllllllllllllllollooollllllloolllllloolllllkN
KxllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0doolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllkX0dlllllllllllllllllllloollloOKKOkxdddoollllllllllllllxK
Oolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXXkollllooolllllllllllllllloONMMMWNNNXX0xolllllllllolloO
kolllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllxXWXkollollllllllllllllllllodKMMMMMMMMMMWKxollllolollolok
kllllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloolllllllllxXWWXkolllllllllllllllolllloONMMMMMMMMMMMW0dllllllllllllk
xollolld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllolloONMMN0xoolllllllolllllllloxXWMMMMMMMMMMMMXxollllllloollx
dollllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollld0WMMWWXOdollollollllllloxXWMMMMMMMMMMMMMNOollllllokkold
olllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNxlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONMMMMWXxollllolllllox0NWMMMMMMMMMMMMMMNOollllllxXOolo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloONMMMMMXxddxxxxkkO0XWMMMMMMMMMMMMMMMMMNOolllllxKW0olo
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllldONWMMMWNXNNWWWMMMMMMMMMMMMMMMMMMMMMMMW0dllollOWW0oll
llllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloxO0KXXXXKKKXNWMMMMMMMMMMMMMMMMMMMMMMMNOdolllkNWOolo
ollllllo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllooooddooloodkKWMMMMMMMMMMMMMMMMMMMMMMWXOolldKNOooo
dollllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllo0WMMMMMMMMMMMMMMMMMMMMMMMMXkold0Nkold
xollllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllollokNMMMMMMMMMMMMMMMMMMMMMMMMMWOookXXxolx
xolllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMN00XW0dlox
kollllllloxOKXXNNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOxollllllllllllllllllllllllllllllolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllolllllolo0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollk
OolllllllllloodddkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkkxddooooollllllllllooodxxdollolllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloO
KdllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXXXK0OOkkkkkkkkOKXXXNNX0xolllllllllllllllllllllllllllllllllllllllllllllllllllllllloollllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKdlldK
NkllllollloolllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWWWWMMMMMMMMMWNOdlllllllllllllllllllllllllllllllllllllllllllllllllllllllollllllllodOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOolokN
WOolllllllllllolllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllllod0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXxolo0W
WXxllllllllllllllllox0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollllllllllllllllllllllllllllllllllllllllllllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollxXM
MWOollllllllllllllooloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllllllllllllllllllllllllllllloolld0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlloOWM
MWXxllolllllllllllllllldOXWWNNK00KXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllllllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxollxXWM
MMWOollllllllloollllllolodxkxdollodk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOollllllllllllllllllllllllllllllllllllllllllllllllllodxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWN0dlllo0WMM
MMMXxllolllllllllllllllllllllllllllloox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dooollllllllllllllllllllllllllllllllllllllllllllodOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOkxxolllokNMMM
MMMW0dlllllllllllllllllllolllllllollollokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdoolllllllllllllllllllllllllllllllllllllllllllxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNOoollllllldKWMMM
MMMMNOollllllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXKOdolllllllllllllllllllllllllllllllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllloOWMMMM
MMMMMXkollllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllolllllokNMMMMM
MMMMMWXxlllllllllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0ollllllllllllllllllllllllllllllllllllllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWOollllllllxXWMMMMM
MMMMMMWKdlllllllllllllllllllllllllllllllokNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxolllllllllllllllllllllllllllllllllllllloONWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllxKWMMMMMM
MMMMMMMW0dlllllllllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllloollllllllllllllllllllllllllllllllloxkOKKXXKKXNMMMMMMMMMMMMMMMMMMMMMMMMNOolllllldKWMMMMMMM
MMMMMMMMW0dllllllllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllllllllllllllllllllllllllllllllllllllllloooood0WMMMMMMMMMMMMMMMMMMMMMMMNOollolldKWMMMMMMMM
MMMMMMMMMW0dlllllllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllllllllllllllllllllllllllolllllllllllllllllld0WMMMMMMMMMMMMMMMMMMMMMMWKxllllldKWMMMMMMMMM
MMMMMMMMMMW0dlllllllllllllllllllllllllloxXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllllllllllllllllllllllllllllllllllllllllllllllllxXMMMMMMMMMMMMMMMMMMMMMWXOdolllldKWMMMMMMMMMM
MMMMMMMMMMMWKxollllllllllllllllllllllllloOWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllllllllllllllllllllllllloolllllllolllollllkNMMMMMMMMMMMMMMMMMMMWXOdolllloxKWMMMMMMMMMMM
MMMMMMMMMMMMWKxollllllllllllllllllllllllod0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkoloollllllllllllllllllllllllllllloddollllllllllllld0WMMMMMMMMMMMMMMMWWNKOdolllllokXWMMMMMMMMMMMM
MMMMMMMMMMMMMWXkollllllllllllllllllllllllldKMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllollllllllllllllllllllllllllllld0XOollllllllllllkNMMMMMMMMMMMMWNK0OkxollllllloONWMMMMMMMMMMMMM
MMMMMMMMMMMMMMMNOdlllllllllllllllllllllllokXMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dlllllllllllllllllllllllllolllld0NWN0dlllllloodxkKWMMMMMMMMMMMMNOollllllllllld0NMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMWKxolollllllllllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNOolllllllllllllllllllllllllllldONMMMWKkdoooxOXNNWMMMMMMMMMMMMMNOollllllllllokXWMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMWXOdlllllllllllllllllloONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdllllllllllllllllllllllllllld0NMMMMMWWXXXXNWMMMMMMMMMMMMMMMMW0dlllllllllod0NMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMWKxollolllllllllllloONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMXxllllllllllllllllllllllllloxKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dlllllllllokXWMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMWNOdollllllloolllldKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkolllloollllooolllllllllodONWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNkllllllolox0NMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMWXkollllllolllllox0NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKdlllllollllllllllllllodkKWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMW0dllllllodOXWMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMWKxoolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN0dollllllllllooddxxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdollllldOXWMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMN0xolllllllllllokXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKxolllllodk0KXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdollolodkXWMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMNKxoolllllllllodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWXOdolldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xoollllodkXWMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNKkolllollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOx0WMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdolllllldOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWKOdollllllllllodx0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOdoollllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0xollollollollodxOXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdooollllodk0NWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKkdooolllllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOxdollllllloxOXWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWN0kdllllllllollllodkOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWXKOkdoolllllloodOKNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdolllllllllllllodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNX0OxdollloolllloxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWX0kdoolllllllllllllooxkO0XNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWNX0OkxoololllllllooxOKNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kdoolllllllllllloooodxkO0KXNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNXK0Okxdoolllllollllloxk0XWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNKOkdoollllllllloolllllloodxkkO00KXXNNWWWWWWMMMMMMMMMWWWWWWWNNXXK00Okxxdoolllllllllllloooxk0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNK0kxdoollllllllllllllllllllloodddxxxkkOOOOOOOOOOOkkkxxxdddoollllllllllllllllloodxO0XNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OxdooollllllllllllooolllllllllllllllllllllllllllllllllllllllllllooodkO0KNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNXK0OkxdooollllllllllllllllllllllllllllllllllllllllllloooddxkO0KXNWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWNNXK0OOkkxdddoooooollllllllllllllllooooooddxxkOO0KKXNWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMWWWNNXXXKK00OOOOOOOOOOOOOOOO00KKXXXNNWWWMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 */

