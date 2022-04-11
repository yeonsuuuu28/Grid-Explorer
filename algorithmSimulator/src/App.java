import java.util.*;

// go_forward()
// check_color()
// turn_right()
// turn_left()

public class App {

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
    static int forwardCnt = 0;
    static int turnCnt = 0;
    static char curDir = 'E';
    static ArrayList<Pair> unVisitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> visitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> redSet = new ArrayList<Pair>();// 아직 안만듦
    static ArrayList<Pair> blockSet = new ArrayList<Pair>();
    static ArrayList<Pair> boxes = new ArrayList<Pair>();
    static ArrayList<Pair> redCells = new ArrayList<Pair>();
    static ArrayList<Pair> initialPairs = new ArrayList<Pair>();
    public static ArrayList<Pair> pickRandoms() {
        System.out.println("in!");
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        int red1x = (int) (Math.random()*6);
        int red1y = (int) (Math.random()*4);
        int red2x = (int) (Math.random()*6);
        int red2y = (int) (Math.random()*4);
        int box1x = (int) (Math.random()*6);
        int box1y = (int) (Math.random()*4);
        int box2x = (int) (Math.random()*6);
        int box2y = (int) (Math.random()*4); 
        System.out.printf("%d %d %d %d\n", red1x, red1y, red2x, red2y);
        while (red1x == red2x && red2y == red1y){
            red1y = (int) (Math.random()*6);
            red2y = (int) (Math.random()*4);
        }
        System.out.println("1");
        while ((red1x == box1x && red1y == box1y) || (red2x == box1x && red2y == box1y) || (box1x == 0 && box1y == 0)){
            box1x = (int) (Math.random()*6);
            box1y = (int) (Math.random()*4); 
        }
        System.out.println("2");

        while ((red1x == box2x && red1y == box2y) || (red2x == box2x && red2y == box2y) || (box1x == box2x && box1y == box2y) || (box2x == 0 && box2y == 0)){
            box2x = (int) (Math.random()*6);
            box2y = (int) (Math.random()*4); 
        }
        System.out.println("3");
        
        System.out.printf("(%d,%d)\n", box1x, box1y);

        Pair box1 = new Pair(box1x,box1y);
        Pair box2 = new Pair(box2x,box2y);
        Pair red1 = new Pair(red1x, red1y);
        Pair red2 = new Pair(red2x, red2y);
        pairs.add(box1);
        pairs.add(box2);
        pairs.add(red1);
        pairs.add(red2);
        
        return pairs;
    }
    public static void goFoward() {
        forwardCnt+=1;
        int removeIndex = unVisitedSet.indexOf(new Pair(curX, curY));
        if (removeIndex != -1) {
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
        // unVisitedSet.remove(new Pair(curX, curY));
        // visitedSet.add(new Pair(curX, curY));
        // 여기에 기존 forward 추가.
    }

    public static void turnRight() {
        turnCnt+=1;
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
        // 기존 turnRight()
    }

    public static void turnLeft() {
        turnCnt+=1;
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
        // 기존 turnLeft()
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

    public static void returnHome() {
        initializePairs();
        ArrayList<Pair> newVisited = new ArrayList<Pair>();
        visitedSet= newVisited;

        int restrictCnt = 0;
        System.out.println("*********Return Home Start!!!*********");
        while(curX!=0 || curY!=0){
            if(restrictCnt >= 30){
                System.out.printf("BREAK!!!!!!!!!!!!!!!!!!!\n");
                System.out.printf("Boxes are at (%d, %d), (%d, %d)\n",
        initialPairs.get(0).x, initialPairs.get(0).y, initialPairs.get(1).x, initialPairs.get(1).y
        );
        System.out.printf("Reds are at (%d, %d), (%d, %d)\n",
        initialPairs.get(2).x, initialPairs.get(2).y, initialPairs.get(3).x, initialPairs.get(3).y
        );
        System.exit(1);
            }
            System.out.printf("currentPos is %d, %d, currentDir is %c\n", curX, curY, curDir);
            // looseCheck();
            if(!strictCheck()){
                looseCheck();
            }
            restrictCnt+=1;
        }
        System.out.printf("%d , %d\n", curX, curY);
        System.out.printf("total (move,turn) is (%d, %d)\n", forwardCnt, turnCnt);
        double totalTime = forwardCnt*(2) + turnCnt*(3.5);
        System.out.printf("total time is %f", totalTime);
        System.exit(1);
    }

    public static boolean distanceCheck() {
        // 앞에 박스가 없으면 true, 있으면 false
        Pair nextPos = getNextPos();
        if (boxes.contains(nextPos))
            return false;
        return true;
    }

    public static void checkColor() {
        Pair nowPos = new Pair(curX, curY);
        if (redCells.contains(nowPos)) {
            redSet.add(nowPos);
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
        // if (!distanceCheck()) {
        //     // box pos 추가
        //     blockSet.add(nextPos);
        //     return false;
        // }

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
        // if (!distanceCheck()) {
        //     // box pos 추가
        //     if(!(blockSet.contains(nextPos))){
        //         blockSet.add(nextPos);
        //     }
        //     return false;
        // }

        return true;
    }

    public static boolean strictCheck() {
        // strictCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        Pair leftPos = getLeftPos();
        Pair rightPos = getRightPos();
        // System.out.printf("leftPos is (%d, %d)", leftPos.x, leftPos.y);
        if (isAvailableStrict(nextPos)) {
            // System.out.println("strict forward");
            if (!distanceCheck()) {
                // box pos 추가
                if(!blockSet.contains(nextPos)){
                    blockSet.add(nextPos);
                }
            }
            else{
                goFoward();
                return true;
            }
        }
        // 왼쪽
        if (isAvailableStrict(leftPos)) {
            // System.out.println("strict left");
            turnLeft();
            if (!distanceCheck()) {
                // box pos 추가
                if(!(blockSet.contains(leftPos))){
                    blockSet.add(leftPos);
                }
                turnRight();
            }
            else{
                goFoward();
                return true;
            }
        }
        // 오른쪽
        if (isAvailableStrict(rightPos)) {
            // System.out.println("strict right");
            turnRight();
            if (!distanceCheck()) {
                // box pos 추가
                if(!(blockSet.contains(rightPos))){
                    blockSet.add(rightPos);
                }
                turnLeft();
            }
            else{
                goFoward();
                return true;
            }
        }
        // 뒤는 이미 간 곳이니까, strictCheck 통과 불가능
        return false;
    }

    public static boolean looseCheck() {
        // looseCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        System.out.println("looseCheck!");
        Pair nextPos = getNextPos();
        Pair leftPos = getLeftPos();
        Pair rightPos = getRightPos();
        if (isAvailableLoose(nextPos)) {
            if (!distanceCheck()) {
                // box pos 추가
                if(!blockSet.contains(nextPos)){
                    blockSet.add(nextPos);
                }
            }
            else{
                goFoward();
                return true;
            }
        }
        if(curDir=='N' || curDir=='W'){
            if(isAvailableLoose(leftPos)){
                turnLeft();
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(leftPos))){
                        blockSet.add(leftPos);
                    }
                    turnRight();
                }
                else{
                    goFoward();
                    return true;
                }
            }
            if(isAvailableLoose(rightPos)){
                turnRight();
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(rightPos))){
                        blockSet.add(rightPos);
                    }
                    turnLeft();
                }
                else{
                    goFoward();
                    return true;
                }
            }
            turnRight();
            turnRight();
            goFoward();
        }
        else{
            if(isAvailableLoose(rightPos)){
                turnRight();
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(rightPos))){
                        blockSet.add(rightPos);
                    }
                    turnLeft();
                }
                else{
                    goFoward();
                    return true;
                }
            }
            if(isAvailableLoose(leftPos)){
                turnLeft();
                if (!distanceCheck()) {
                    // box pos 추가
                    if(!(blockSet.contains(leftPos))){
                        blockSet.add(leftPos);
                    }
                    turnRight();
                }
                else{
                    goFoward();
                    return true;
                }
            }
            turnRight();
            turnRight();
            goFoward();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        initializePairs();
        initialPairs = pickRandoms();
        System.out.printf("Boxes are at (%d, %d), (%d, %d)",
        initialPairs.get(0).x, initialPairs.get(0).y, initialPairs.get(1).x, initialPairs.get(1).y
        );
        System.out.printf("Reds are at (%d, %d), (%d, %d)",
        initialPairs.get(2).x, initialPairs.get(2).y, initialPairs.get(3).x, initialPairs.get(3).y
        );
        boxes.add(initialPairs.get(0));
        boxes.add(initialPairs.get(1));
        redCells.add(initialPairs.get(2));
        redCells.add(initialPairs.get(3));
        // boxes.add(new Pair(0,3));
        // boxes.add(new Pair(5,0));
        // redCells.add(new Pair(1,0));
        // redCells.add(new Pair(4,1));
        System.out.printf("%d, %d, %c", curX, curY, curDir);
        // 알고리즘 시작

        // System.out.println(unVisitedSet.contains(new Pair(0, 0)));

        while (!unVisitedSet.isEmpty() && !(redSet.size()>=2 && blockSet.size()>=2)) {
            System.out.printf("currentPos is %d, %d, currentDir is %c\n", curX, curY, curDir);
            // for (int i = 0; i < unVisitedSet.size(); i++) {
            //     System.out.printf("(%d,%d)", unVisitedSet.get(i).x, unVisitedSet.get(i).y);
            // }
            // System.out.println("");
            // for (int i = 0; i < visitedSet.size(); i++) {
            //     System.out.printf("(%d,%d)", visitedSet.get(i).x, visitedSet.get(i).y);
            // }
            // System.out.println("");
            System.out.printf("boxes, redCells are %d, %d", blockSet.size(), redSet.size());
            System.out.println("");

            try {
                Thread.sleep(50); // 1초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!strictCheck()) {
                System.out.println("looseCheck!");
                looseCheck();
            }
        }
        // 모든 칸을 다 가봄
        
        returnHome();
        System.out.printf("%d", unVisitedSet.size());
        System.out.printf("%d , %d", curX, curY);

    }
}
