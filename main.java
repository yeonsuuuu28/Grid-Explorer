package test_motor;

import java.util.*;

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

public class test_motor {
    private static EV3IRSensor distance_sensor;
    private static EV3ColorSensor color_sensor;
    static RegulatedMotor leftMotor = Motor.A;
    static RegulatedMotor rightMotor = Motor.B;
    public static float maxsp = leftMotor.getMaxSpeed();
    static int speed = (int) maxsp;
    static int speed2 = 500;
    static int delay = (int) (885000/maxsp);
    static int turnDelay = (int) (429000/500);
    static int turnDelayL = (int) (429000/500);
    static EV3 ev3 = (EV3) BrickFinder.getLocal();
    static Keys keys = ev3.getKeys();
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

    static int curX = 0;
    static int curY = 0;
    static char curDir = 'E';
    static ArrayList<Pair> unVisitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> visitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> redSet = new ArrayList<Pair>();// 아직 안만듦
    static ArrayList<Pair> blockSet = new ArrayList<Pair>();

    public static void goForward(RegulatedMotor x, RegulatedMotor y) {
    	if(blockSet.size()==2 && redSet.size()==2){
    		returnHome();
    	}
    	leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        int removeIndex = unVisitedSet.indexOf(new Pair(curX, curY));
        if (removeIndex != -1){
        	unVisitedSet.remove(removeIndex);
        	visitedSet.add(new Pair(curX, curY));
        }
        checkColor();
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

        y.backward();
        x.backward();
        Delay.msDelay(delay);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
    }

    public static void checkColor() {
        EV3 ev3 = (EV3) BrickFinder.getLocal();
        TextLCD lcd = ev3.getTextLCD();
        lcd.clear();
        int id = color_sensor.getColorID();
        if (id == Color.RED) {
            // lcd.drawString("red", 1, 4);
            redSet.add(new Pair(curX, curY));
            System.out.printf("Red Color %d, %d", curX, curY);
        } else {
            // lcd.drawString("hello world", 1, 4);
        }
    }

    public static void returnHome() {
    	while (curX == 0 & curY == 0){
            while (curDir != 'S'){
                turnLeft(leftMotor, rightMotor);
            }

            while (curY > 0 & !distanceCheck()){
                    goForward(leftMotor, rightMotor);
                } //일단 밑으로 갈수있는데까지 내려감

            turnLeft(leftMotor, rightMotor); //이제 다시 중 W보는중
            if (curY !=0){ //0이 아니면 밑에 block 이 있단거

                if (distanceCheck() | curX == 0){ //왼쪽으로 못감 이유 2가지
                    turnLeft(leftMotor, rightMotor); 
                    turnLeft(leftMotor, rightMotor);
            
                    if (distanceCheck()) { //만약 right 에 block 이 있다? -> 왼쪽은 boundary 오른쪽 block 밑 block
                    	turnLeft(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	turnRight(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	turnRight(leftMotor, rightMotor);
                    }
                    else if (curX == 5){ //왼쪽으로 못가는데 오른쪽은 또 boundary 라 못감. 즉 back 해야함
                    	turnLeft(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	turnLeft(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	goForward(leftMotor, rightMotor);
                    	turnLeft(leftMotor, rightMotor);
                    }
                    else{ // 오른쪽 뚫려있음 가고 다시 S보게 만듬
                    	goForward(leftMotor, rightMotor);
                    	if (curX != 4 & !distanceCheck())  {
                    		goForward(leftMotor, rightMotor); //4가 아니면 두번갈 수 있음 block 나란히 두개 방지
                    		turnRight(leftMotor, rightMotor);
                    	}
                    }
                }
        
                else { //그냥 왼쪽이 뚫려있음 => 왼쪽으로감 + 다시 S보는중
                	goForward(leftMotor, rightMotor);
                	turnLeft(leftMotor, rightMotor);
                }
           }
           else { //이건 curY가 0일때만 실행 0이 아니면 위에 while문 다시 실행해서 여기 도착
                  //여기 실행된다는 것은 맨 밑줄에 있다는 것. 지금 W보고있음.
                while(curX>0 & !distanceCheck()){
                    goForward(leftMotor, rightMotor);
                } //왼쪽으로 갈때까지 감

                if (curX == 0) return;
                    //complete return

                 else{ //block 에 막힌거
                    turnRight(leftMotor, rightMotor); 
                    if (distanceCheck()){ //왼쪽 위 막혀있음 돌아서 나옴
                        turnRight(leftMotor, rightMotor);
                        goForward(leftMotor, rightMotor);
                        turnLeft(leftMotor, rightMotor);
                        goForward(leftMotor, rightMotor);
                        goForward(leftMotor, rightMotor);
                        turnLeft(leftMotor, rightMotor);
                        goForward(leftMotor, rightMotor);
                        goForward(leftMotor, rightMotor);
                        turnLeft(leftMotor, rightMotor);
                        continue;
                    }
                    goForward(leftMotor, rightMotor);
                    turnLeft(leftMotor, rightMotor);
                        if (distanceCheck()){ //2개 쌓여있음
                            turnRight(leftMotor, rightMotor);
                            goForward(leftMotor, rightMotor);
                            turnLeft(leftMotor, rightMotor);
                            goForward(leftMotor, rightMotor);
                            goForward(leftMotor, rightMotor);
                        }
                        else{
                            goForward(leftMotor, rightMotor); 
                            if (distanceCheck()){ //한번 갔는데 block 이 또있음
                                turnRight(leftMotor, rightMotor);
                                goForward(leftMotor, rightMotor);
                                turnLeft(leftMotor, rightMotor);
                                goForward(leftMotor, rightMotor);
                                goForward(leftMotor, rightMotor);
                            }
                            else{
                            	goForward(leftMotor, rightMotor);
                            }
                        }
                    }
            }
            }   
//        leftMotor.stop(true);
//        rightMotor.stop(true);
//        Delay.msDelay(50000000);
    }

    public static void turnRight(RegulatedMotor x, RegulatedMotor y) {
    	leftMotor.setSpeed(speed2);
        rightMotor.setSpeed(speed2);
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
        // x.rotate(+1170);
        // y.rotate(-1170);

        y.backward();
        x.backward();
        Delay.msDelay(250);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(100);
        
        x.forward();
        y.backward();
        Delay.msDelay(turnDelay);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
        
        x.forward();
        y.forward();
        Delay.msDelay(400);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);

    }

    //it turns a little bit more, so need to change if needed.
    public static void turnLeft(RegulatedMotor x, RegulatedMotor y) {
    	leftMotor.setSpeed(speed2);
        rightMotor.setSpeed(speed2);
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
        
        y.backward();
        x.backward();
        Delay.msDelay(250);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(100);
                
        y.forward();
        x.backward();
        Delay.msDelay(turnDelayL);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
//        
        x.forward();
        y.forward();
        Delay.msDelay(400);
        x.stop(true);
        y.stop(true);
        Delay.msDelay(500);
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
        SampleProvider distanceMode = distance_sensor.getMode("Distance");
        float value[] = new float[distanceMode.sampleSize()];

        distanceMode.fetchSample(value, 0);
        float centimeter = value[0];
        if(centimeter < 53.0){
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

    public static boolean isAvailableStrict(Pair nextPos) {
        if (nextPos.x > 5 || nextPos.x < 0) {
            return false;
        }
        if (nextPos.y > 3 || nextPos.y < 0) {
            return false;
        }
        if (visitedSet.contains(new Pair(nextPos.x, nextPos.y))) {// 여기에 하자가 있음
            return false;
        }
        if (!distanceCheck()) {
            // box pos 추가
            blockSet.add(nextPos);
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
        if (!distanceCheck()) {
            // box pos 추가
            blockSet.add(nextPos);
            return false;
        }

        return true;
    }

    public static boolean strictCheck() {
        // strictCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        if (isAvailableStrict(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 왼쪽
        turnLeft(leftMotor, rightMotor);
        nextPos = getNextPos();
        if (isAvailableStrict(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 오른쪽
        turnRight(leftMotor, rightMotor);
        turnRight(leftMotor, rightMotor);
        nextPos = getNextPos();
        if (isAvailableStrict(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 뒤는 이미 간 곳이니까, strictCheck 통과 불가능
        turnLeft(leftMotor, rightMotor);
        return false;
    }

    public static boolean looseCheck() {
        // looseCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 왼쪽
        turnLeft(leftMotor, rightMotor);
        nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 오른쪽
        turnRight(leftMotor, rightMotor);
        turnRight(leftMotor, rightMotor);
        nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goForward(leftMotor, rightMotor);
            return true;
        }
        // 뒤, 뒤까지 못 갈 수는 없다.
        turnRight(leftMotor, rightMotor);
        goForward(leftMotor, rightMotor);

        return true;
    }

    public static void main(String[] args) {
    	
    	distance_sensor = new EV3IRSensor(SensorPort.S1);
        color_sensor = new EV3ColorSensor(SensorPort.S4);



        initializePairs();

        // System.out.printf("%d, %d, %c", curX, curY, curDir);
        // 알고리즘 시작

//         System.out.println(unVisitedSet.contains(new Pair(0, 0)));
        do {
         while (!unVisitedSet.isEmpty()) {
        
         try {
         Thread.sleep(200); // 1초 대기
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
        
         if (!strictCheck()) {
        	 looseCheck();
         }
         }
        }while(keys.getButtons()!=Keys.ID_ESCAPE);
//         모든 칸을 다 가봄
//        returnHome();
                      
//        goForward(leftMotor, rightMotor);
//        turnLeft(leftMotor, rightMotor);
//        turnRight(leftMotor, rightMotor);
        
    }
}