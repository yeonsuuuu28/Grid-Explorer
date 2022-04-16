package test_motor;

import java.util.*;
import java.util.Date;


import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.Sound;

public class test_motor {
   private static EV3IRSensor distance_sensor;
   private static EV3IRSensor distance_sensor2;
    static EV3ColorSensor color_sensor_left;
    static EV3ColorSensor color_sensor_right;
    static RegulatedMotor leftMotor = Motor.D;
    static RegulatedMotor rightMotor = Motor.A;
    public static float maxsp = leftMotor.getMaxSpeed();
    static int speed = 600;
    static int speed2 = 600;
    static int delay = (int) (915200/600);
    static int turnDelay = (int) (440000/600);
    static int turnDelayL = (int) (475000/600);
    static EV3 ev3 = (EV3) BrickFinder.getLocal();
    static Keys keys = ev3.getKeys();
    static boolean isAtZero;
    static boolean is250= false;
    
    static class Pair {
        int x, y;

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object object) {
            Pair pair = (Pair) object;
            // name은 상관없이, id만 같으면 true를 리턴합니다.
            if (pair.x == this.x && pair.y == this.y) {
                return true;
            }
            return false;
        }
    }
    static long start_time = 0;
    static int curX = 0;
    static int curY = 0;
    static int forwardCnt = 0;
    static int turnCnt = 0;
    static char curDir;
    static boolean leftSensed = false;
    static long leftTime;
    static long rightTime;
    static boolean rightSensed = false;
    static ArrayList<Pair> unVisitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> visitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> redSet = new ArrayList<Pair>();
    static ArrayList<Pair> blockSet = new ArrayList<Pair>();
    static ArrayList<Pair> boxes = new ArrayList<Pair>();
    static ArrayList<Pair> redCells = new ArrayList<Pair>();
    static ArrayList<Pair> initialPairs = new ArrayList<Pair>();
    static int leftColor;
    static int rightColor;
    
    public static boolean correctDir() {
       try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int leftColor = color_sensor_left.getColorID();
        int rightColor = color_sensor_right.getColorID();
        if (leftColor == Color.BLACK && rightColor == Color.BLACK){
           return true;
        }
        if (leftColor != Color.BLACK && rightColor == Color.BLACK){
           leftMotor.stop(true);
           Delay.msDelay(1);
           long start = System.currentTimeMillis();
           while (leftColor != Color.BLACK){
              rightMotor.setSpeed(120);
              rightMotor.backward();
              Delay.msDelay(1);
              leftColor = color_sensor_left.getColorID();
              long end = System.currentTimeMillis();
              if (end - start > 1500){
                 rightMotor.setSpeed(120);
                 rightMotor.forward();
                 Delay.msDelay(end-start+500);
                 leftMotor.setSpeed(speed);
                 rightMotor.setSpeed(speed);
                 rightMotor.forward();
                 leftMotor.forward();
                 Delay.msDelay(200);
                 correctDir();
                 break;
              }
              }
           return true;
        }
        if (leftColor == Color.BLACK && rightColor != Color.BLACK){
           rightMotor.stop(true);
           Delay.msDelay(1);
           long start = System.currentTimeMillis();
           while (rightColor != Color.BLACK){
              leftMotor.setSpeed(120);
              leftMotor.backward();
              Delay.msDelay(1);
              rightColor = color_sensor_right.getColorID();
              long end = System.currentTimeMillis();
              if (end - start > 1500){
                 leftMotor.setSpeed(120);
                 leftMotor.forward();
                 Delay.msDelay(end-start+500);
                 rightMotor.setSpeed(speed);
                 leftMotor.setSpeed(speed);
                 rightMotor.forward();
                 leftMotor.forward();
                 Delay.msDelay(200);
                 correctDir();
                 break;
              }
           }
           leftMotor.setSpeed(120);
          leftMotor.backward();
          Delay.msDelay(250);
           return true;
        }
        else{
           long start = System.currentTimeMillis();
           while(leftColor != Color.BLACK && rightColor != Color.BLACK){
              rightMotor.setSpeed(120);
              leftMotor.setSpeed(120);
              leftMotor.backward();
              rightMotor.backward();
              Delay.msDelay(1);
              leftColor = color_sensor_left.getColorID();
              rightColor = color_sensor_right.getColorID();
              long end = System.currentTimeMillis();
              if (end - start > 7000){
                 leftMotor.setSpeed(120);
                 rightMotor.setSpeed(120);
                 leftMotor.forward();
                 rightMotor.forward();
                 Delay.msDelay(end-start+300);
                 correctDir();
                 break;
              }
           }
           correctDir();
           return true;
        }
    }
    
    public static void goForward(RegulatedMotor x, RegulatedMotor y) {
       forwardCnt+=1;
       
       
        int removeIndex = unVisitedSet.indexOf(new Pair(curX, curY));
        if (removeIndex != -1) {
            unVisitedSet.remove(removeIndex);
            visitedSet.add(new Pair(curX, curY));
        }
        
        // 방향에 따른 좌표 변화
        switch (curDir) {
            case 'E':
                curX++;
                break;
            case 'W':
                curX--;
                break;
            case 'S':
                curY--;
                break;
            case 'N':
                curY++;
                break;
        }
        
        correctDir();
        
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        y.backward();
        x.backward();
        Delay.msDelay(1250);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(200);
        //이게 있어야 distance 가능
        checkColor();
        try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } 

    public static void checkColor() {
       try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();
        lcd.clear();
        int id = color_sensor_left.getColorID();
        int id2 = color_sensor_right.getColorID();
        if (id == Color.RED || id2 == Color.RED) {
            Pair nowPos = new Pair(curX, curY);
            if (!redCells.contains(nowPos)) {
               Sound.playNote(Sound. PIANO, 1047, 1000);
                redCells.add(nowPos);
                if(isAtZero){                   
                   System.out.printf("(%d,%d,R)\n", curX, curY);
                }
                else{
                   System.out.printf("(%d,%d,R)\n", 5-curX, 3-curY);
                }
            }
            
        }
    }
    public static void returnHome() {
       System.out.printf("returnHome!");
       if(keys.getButtons()==Keys.ID_ESCAPE){
         return;
      }
       initializePairs();
        ArrayList<Pair> newVisited = new ArrayList<Pair>();
        visitedSet= newVisited;

        int restrictCnt = 0;
        while(curX!=0 || curY!=0){
           if(keys.getButtons() == Keys.ID_ESCAPE){
              return;
           }
            if(restrictCnt >= 30){
                System.out.printf("return BREAK!!!!!!!!!!!!!!!!!!!\n");
                System.exit(1);
            }
            if(!strictCheck()){
                looseCheck();
            }
            restrictCnt+=1;
        }
        return;
    }

    public static void turnRight(RegulatedMotor x, RegulatedMotor y) {
       if(keys.getButtons() == Keys.ID_ESCAPE){
          System.exit(1);
       }
        switch (curDir) {
            case 'E':
                curDir = 'S';
                break;
            case 'W':
                curDir = 'N';
                break;
            case 'S':
                curDir = 'W';
                break;
            case 'N':
                curDir = 'E';
                break;
        }

        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        y.backward();
        x.backward();
        Delay.msDelay(200);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
        
        x.forward();
        y.backward();
        Delay.msDelay(turnDelay);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);

        x.forward();
        y.forward();
        Delay.msDelay(500);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
        
        try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void turnLeft (RegulatedMotor x, RegulatedMotor y) {
       if(keys.getButtons() == Keys.ID_ESCAPE){
          System.exit(1);
       }
        switch (curDir) {
            case 'E':
                curDir = 'N';
                break;
            case 'W':
                curDir = 'S';
                break;
            case 'S':
                curDir = 'E';
                break;
            case 'N':
                curDir = 'W';
                break;
        }
        
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        y.backward();
        x.backward();
        Delay.msDelay(200);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(50);
        
        y.forward();
        x.backward();
        Delay.msDelay(turnDelayL);
        y.stop(true);
        x.stop(true);
        Delay.msDelay(450);
        
        x.forward();
        y.forward();
        Delay.msDelay(500);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
        
        try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    public static void initializePairs() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Pair newCoor = new Pair(i, j);
                unVisitedSet.add(newCoor);
            }
        }
        return;
    }

    public static boolean distanceCheck() {
        // 앞에 박스가 없으면 true, 있으면 false
       try {
            Thread.sleep(100); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SampleProvider distanceMode = distance_sensor.getMode("Distance");
        SampleProvider distanceMode2 = distance_sensor2.getMode("Distance");
        float value[] = new float[distanceMode.sampleSize()];
        float value2[] = new float[distanceMode2.sampleSize()];
        distanceMode.fetchSample(value, 0);
        distanceMode2.fetchSample(value2, 0);
        float centimeter = value[0];
        float centimeter2 = value2[0];
        if(centimeter < 53.0 || centimeter2 < 53.0){
           Sound.playNote(Sound.PIANO, 523, 1000);
            return false;
        }
        else{
            return true;
        }
    }

    public static Pair getNextPos() {
        int nextPosX = curX;
        int nextPosY = curY;
        switch (curDir) {
            case 'E':
                nextPosX = curX + 1;
                break;
            case 'W':
                nextPosX = curX - 1;
                break;
            case 'S':
                nextPosY = curY - 1;
                break;
            case 'N':
                nextPosY = curY + 1;
                break;
        }
        Pair nextPos = new Pair(nextPosX, nextPosY);
        return nextPos;
    }
    public static Pair getLeftPos() {
        int leftPosX = curX;
        int leftPosY = curY;
        switch (curDir) {
            case 'E':
                leftPosY = curY + 1;
                break;
            case 'W':
                leftPosY = curY - 1;
                break;
            case 'S':
                leftPosX = curX + 1;
                break;
            case 'N':
                leftPosX = curX - 1;
                break;
        }
        Pair leftPos = new Pair(leftPosX, leftPosY);
        return leftPos;
    }
    public static Pair getRightPos() {
        int rightPosX = curX;
        int rightPosY = curY;
        switch (curDir) {
            case 'E':
                rightPosY = curY - 1;
                break;
            case 'W':
                rightPosY = curY + 1;
                break;
            case 'S':
                rightPosX = curX - 1;
                break;
            case 'N':
                rightPosX = curX + 1;
                break;
        }
        Pair rightPos = new Pair(rightPosX, rightPosY);
        return rightPos;
    }


    public static boolean isAvailableStrict(Pair nextPos) {
        if (nextPos.x > 5 || nextPos.x < 0) {
            return false;
        }
        if (nextPos.y > 3 || nextPos.y < 0) {
            return false;
        }
        if (visitedSet.contains(new Pair(nextPos.x, nextPos.y)) || blockSet.contains(new Pair(nextPos.x, nextPos.y))) {
            return false;
        }

        return true;
    }

    public static boolean isAvailableLoose(Pair nextPos) {
        if (nextPos.x > 5 || nextPos.x < 0) {
            return false;
        }
        if (nextPos.y > 3 || nextPos.y < 0) {
            return false;
        }
        if (blockSet.contains(new Pair(nextPos.x, nextPos.y))) {
            return false;
        }

        return true;
    }

    public static boolean strictCheck() {
        // strictCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        Pair leftPos = getLeftPos();
        Pair rightPos = getRightPos();
        if (isAvailableStrict(nextPos)) {
            if (!distanceCheck()) {
                // box pos 추가
                if(!blockSet.contains(nextPos)){
                   if(isAtZero){                   
                       System.out.printf("(%d,%d,B)\n", nextPos.x, nextPos.y);
                    }
                    else{
                       System.out.printf("(%d,%d,B)\n", 5-nextPos.x, 3-nextPos.y);
                    }
                    blockSet.add(nextPos);
                }
            }
            else{
                goForward(leftMotor, rightMotor);
                return true;
            }
        }
        // 왼쪽
        if (isAvailableStrict(leftPos)) {
            turnLeft(leftMotor, rightMotor);
            if (!distanceCheck()) {
                // box pos 추가
                if(!(blockSet.contains(leftPos))){
                   if(isAtZero){                   
                       System.out.printf("(%d,%d,B)\n", leftPos.x, leftPos.y);
                    }
                    else{
                       System.out.printf("(%d,%d,B)\n", 5-leftPos.x, 3-leftPos.y);
                    }
                    blockSet.add(leftPos);
                }
                turnRight(leftMotor, rightMotor);
            }
            else{
                goForward(leftMotor, rightMotor);
                return true;
            }
        }
        // 오른쪽
        if (isAvailableStrict(rightPos)) {
            // System.out.println("strict right");
            turnRight(leftMotor, rightMotor);
            if (!distanceCheck()) {
                // box pos 추가
                if(!(blockSet.contains(rightPos))){
                   if(isAtZero){                   
                       System.out.printf("(%d,%d,B)\n", rightPos.x, rightPos.y);
                    }
                    else{
                       System.out.printf("(%d,%d,B)\n", 5-rightPos.x, 3-rightPos.y);
                    }
                    blockSet.add(rightPos);
                }
                turnLeft(leftMotor, rightMotor);
            }
            else{
                goForward(leftMotor, rightMotor);
                return true;
            }
        }
        return false;
    }
    
    public static boolean looseCheck() {
        // looseCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        Pair leftPos = getLeftPos();
        Pair rightPos = getRightPos();
        if (isAvailableLoose(nextPos)) {
            if (!distanceCheck()) {
                // box pos 추가
                if(!blockSet.contains(nextPos)){
                   if(isAtZero){                   
                       System.out.printf("(%d,%d,B)\n", nextPos.x, nextPos.y);
                    }
                    else{
                       System.out.printf("(%d,%d,B)\n", 5-nextPos.x, 3-nextPos.y);
                    }
                    blockSet.add(nextPos);
                }
            }
            else{
                goForward(leftMotor, rightMotor);
                return true;
            }
        }

        if(curDir=='N' || curDir=='W'){
            if(isAvailableLoose(leftPos)){
                turnLeft(leftMotor, rightMotor);
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(leftPos))){
                       if(isAtZero){                   
                           System.out.printf("(%d,%d,B)\n", leftPos.x, leftPos.y);
                        }
                        else{
                           System.out.printf("(%d,%d,B)\n", 5-leftPos.x, 3-leftPos.y);
                        };
                        blockSet.add(leftPos);
                    }
                    turnRight(leftMotor, rightMotor);
                }
                else{
                    goForward(leftMotor, rightMotor);
                    return true;
                }
            }
            if(isAvailableLoose(rightPos)){
                turnRight(leftMotor, rightMotor);
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(rightPos))){
                       if(isAtZero){                   
                           System.out.printf("(%d,%d,B)\n", rightPos.x, rightPos.y);
                        }
                        else{
                           System.out.printf("(%d,%d,B)\n", 5-rightPos.x, 3-rightPos.y);
                        }
                        blockSet.add(rightPos);
                    }
                    turnLeft(leftMotor, rightMotor);
                }
                else{
                    goForward(leftMotor, rightMotor);
                    return true;
                }
            }
            turnRight(leftMotor, rightMotor);
            turnRight(leftMotor, rightMotor);
            goForward(leftMotor, rightMotor);
        }
        else{
            if(isAvailableLoose(rightPos)){
                turnRight(leftMotor, rightMotor);
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(rightPos))){
                       if(isAtZero){                   
                           System.out.printf("(%d,%d,B)\n", rightPos.x, rightPos.y);
                        }
                        else{
                           System.out.printf("(%d,%d,B)\n", 5-rightPos.x, 3-rightPos.y);
                        }
                        blockSet.add(rightPos);
                    }
                    turnLeft(leftMotor, rightMotor);
                }
                else{
                    goForward(leftMotor, rightMotor);
                    return true;
                }
            }
            if(isAvailableLoose(leftPos)){
                turnLeft(leftMotor, rightMotor);
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(leftPos))){
                       if(isAtZero){                   
                           System.out.printf("(%d,%d,B)\n", leftPos.x, leftPos.y);
                        }
                        else{
                           System.out.printf("(%d,%d,B)\n", 5-leftPos.x, 3-leftPos.y);
                        };
                        blockSet.add(leftPos);
                    }
                    turnRight(leftMotor, rightMotor);
                }
                else{
                    goForward(leftMotor, rightMotor);
                    return true;
                }
            }
            turnRight(leftMotor, rightMotor);
            turnRight(leftMotor, rightMotor);
            goForward(leftMotor, rightMotor);
        }
        return true;
    }

    
    /*
     * 시작할때 beep
     *  시작할 떄 좌표 0,0이면 left, 3,5면 right누름 (낮은피아노, 높은피아노)
     *  doubleBeep하면 방향 설정 시작
     *  Up일 때 오른쪽, Down일 때 위로 가게 끔.
     *  돌기 시작할 때 red만나면 높은 피아노, box만나면 낮은 피아노
     *  returnHome시작할 때 플룻소리
     *  다 끝나면 doubleBeep소리
     * */
    public static void main(String[] args) {
       
       distance_sensor = new EV3IRSensor(SensorPort.S1);
       distance_sensor2 = new EV3IRSensor(SensorPort.S4);
       color_sensor_right = new EV3ColorSensor(SensorPort.S2);
       color_sensor_left = new EV3ColorSensor(SensorPort.S3);
//        //inform initial positon 0,0 or 5,3
       Sound.beep();
       do{
          if(keys.getButtons()==Keys.ID_LEFT){
             isAtZero = true;
             Sound.playNote(Sound.PIANO, 523, 1000);
             break;
          }
          else if(keys.getButtons()==Keys.ID_RIGHT){
             Sound.playNote(Sound. PIANO, 1047, 1000);
             isAtZero = false;
             break;
          }
          else{
             continue;
          }
       }while(true);
       Sound.twoBeeps();
       
       do{
          if(keys.getButtons()==Keys.ID_UP){
             curDir='E';
             Sound.playNote(Sound.PIANO, 523, 1000);
             break;
          }
          else if(keys.getButtons()==Keys.ID_DOWN){
             curDir='N';
             Sound.playNote(Sound. PIANO, 1047, 1000);
             break;
          }
          else{
             continue;
          }
       }while(true);
       if(isAtZero){
          System.out.println("Start at (0,0)");           
       }
       else{
          System.out.println("Start at (5,3)");           
       }
       try {
          Thread.sleep(1000); // 1초 대기
       } catch (InterruptedException e) {
          e.printStackTrace();
       }
       Sound.twoBeeps();
        initializePairs();
        start_time = System.currentTimeMillis();
         while (!(redCells.size()>=2 && blockSet.size()>=2)) {
            long end_time = System.currentTimeMillis();
            if ((end_time - start_time >= 250000) && !is250 ){
                Sound.playNote(Sound.FLUTE, 1047, 1000);
                if (redCells.size() < 2 ){
                   if (redCells.size() == 1){
                   Pair temp = unVisitedSet.get((int)(Math.random()*unVisitedSet.size()));
                   System.out.printf("(%d,%d,R)\n", temp.x, temp.y);
                   }
                   else{
                       Pair temp = unVisitedSet.get((int)(Math.random()*unVisitedSet.size()));
                       System.out.printf("(%d,%d,R)\n", temp.x, temp.y);
                       Pair temp2 = unVisitedSet.get((int)(Math.random()*unVisitedSet.size()));
                       System.out.printf("(%d,%d,R)\n", temp2.x, temp2.y);
                   }
                }
                is250 = true;
             }
            if (is250 && curX == 0 && curY == 0){
               Delay.msDelay(1000000);
            }
            if(keys.getButtons()==Keys.ID_ESCAPE){
                break;
             }
             try {
                 Thread.sleep(50); // 1초 대기
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             if (!strictCheck()) {
                 looseCheck();
             }
         }
         Sound.playNote(Sound.FLUTE, 1047, 1000);
         returnHome();
          Sound.twoBeeps();
          do{
            if(keys.getButtons()==Keys.ID_ENTER){
               break;
            }
         }while(true);
    }
}