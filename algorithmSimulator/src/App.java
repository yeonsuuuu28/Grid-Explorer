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
    static char curDir = 'E';
    static ArrayList<Pair> unVisitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> visitedSet = new ArrayList<Pair>();
    static ArrayList<Pair> redSet = new ArrayList<Pair>();// 아직 안만듦
    static ArrayList<Pair> blockSet = new ArrayList<Pair>();
    static ArrayList<Pair> boxes = new ArrayList<Pair>();

    public static void goFoward() {
        int removeIndex = unVisitedSet.indexOf(new Pair(curX, curY));
        unVisitedSet.remove(removeIndex);
        visitedSet.add(new Pair(curX, curY));
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

    public static boolean customContains(Pair pos) {
        for (int i = 0; i < visitedSet.size(); i++) {

        }
        return true;
    }

    public static void checkColor() {
        // 기존 checkColor()
    }

    public static void returnHome() {
        // 끝났을 때 원점으로 돌아오는 함수
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
        Pair nextPos = getNextPos();
        if (boxes.contains(nextPos))
            return false;
        return true;
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
            goFoward();
            return true;
        }
        // 왼쪽
        turnLeft();
        nextPos = getNextPos();
        if (isAvailableStrict(nextPos)) {
            goFoward();
            return true;
        }
        // 오른쪽
        turnRight();
        turnRight();
        nextPos = getNextPos();
        if (isAvailableStrict(nextPos)) {
            goFoward();
            return true;
        }
        // 뒤는 이미 간 곳이니까, strictCheck 통과 불가능
        return false;
    }

    public static boolean looseCheck() {
        // looseCheck에 통과(갈 수 있는 칸이 존재)면 true
        // 정면
        Pair nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goFoward();
            return true;
        }
        // 왼쪽
        turnLeft();
        nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goFoward();
            return true;
        }
        // 오른쪽
        turnRight();
        turnRight();
        nextPos = getNextPos();
        if (isAvailableLoose(nextPos)) {
            goFoward();
            return true;
        }
        // 뒤, 뒤까지 못 갈 수는 없다.
        turnRight();
        goFoward();

        return true;
    }

    public static void main(String[] args) throws Exception {

        initializePairs();
        boxes.add(new Pair(3, 0));
        boxes.add(new Pair(1, 2));
        // System.out.printf("%d, %d, %c", curX, curY, curDir);
        // 알고리즘 시작

        // System.out.println(unVisitedSet.contains(new Pair(0, 0)));

        while (!unVisitedSet.isEmpty()) {
            System.out.printf("currentPos is %d, %d, currentDir is %c\n", curX, curY,
                    curDir);
            for (int i = 0; i < unVisitedSet.size(); i++) {
                System.out.printf("(%d,%d)", unVisitedSet.get(i).x, unVisitedSet.get(i).y);
            }
            System.out.printf("visitedCells # is %d\n", unVisitedSet.size());
            System.out.println("");

            try {
                Thread.sleep(200); // 1초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!strictCheck()) {
                looseCheck();
            }
        }
        // 모든 칸을 다 가봄
        returnHome();
        System.out.printf("%d", unVisitedSet.size());

    }
}
