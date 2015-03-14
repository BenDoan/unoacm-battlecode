package example;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
    public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else {
            myself = new BaseBot(rc);
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class BaseBot {
        protected RobotController rc;
        protected MapLocation myHQ, theirHQ;
        protected Team myTeam, theirTeam;
        protected Random rand;
        protected static Direction[] directions = {
            Direction.EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH
        };

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
            this.rand = new Random(rc.getID());
        }

        public Direction[] getDirectionsToward(MapLocation dest) {
            Direction toDest = rc.getLocation().directionTo(dest);
            Direction[] dirs = {toDest,
                    toDest.rotateLeft(), toDest.rotateRight(),
                    toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

            return dirs;
        }

        public Direction getMoveDir(MapLocation dest) {
            Direction[] dirs = getDirectionsToward(dest);
            for (Direction d : dirs) {
                if (rc.canMove(d)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getSpawnDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
            for (Direction d : dirs) {
                if (rc.canSpawn(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getBuildDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
            for (Direction d : dirs) {
                if (rc.canBuild(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public RobotInfo[] getAllies() {
            RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
            return allies;
        }

        public RobotInfo[] getEnemiesInAttackingRange() {
            RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
            return enemies;
        }

        public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
            if (enemies.length == 0) {
                return;
            }

            double minEnergon = Double.MAX_VALUE;
            MapLocation toAttack = null;
            for (RobotInfo info : enemies) {
                if (info.health < minEnergon) {
                    toAttack = info.location;
                    minEnergon = info.health;
                }
            }

            rc.attackLocation(toAttack);
        }

        public void beginningOfTurn() {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
            }
        }

        public void endOfTurn() {
        }

        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }

    public static class HQ extends BaseBot {
        public HQ(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction spawnDir = getSpawnDirection(RobotType.BEAVER);
            if (spawnDir != null && rc.isCoreReady()) {
               rc.spawn(spawnDir, RobotType.BEAVER);
            }
            rc.yield();
        }
    }

    public static class Beaver extends BaseBot {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            if(rc.senseOre(rc.getLocation()) >= 2.0) {
                if (rc.canMine() && rc.getCoreDelay() == 0) {
                    rc.mine();
                }
            }else {
                if(rc.getCoreDelay() == 0) {
                    if (rand.nextInt(100) == 0) {
                        Direction buildDir = getBuildDirection(RobotType.BARRACKS);
                        if (buildDir != null) {
                            rc.build(buildDir, RobotType.BARRACKS);
                        }
                    } else {
                        Direction dir = directions[rand.nextInt(4)];
                        if (rc.canMove(dir)) {
                            rc.move(dir);
                        }
                    }
                }
            }
            rc.yield();
        }
    }

    public static class Barracks extends BaseBot {
        public Barracks(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction spawnDir = getSpawnDirection(RobotType.SOLDIER);
            if(spawnDir != null && rc.isCoreReady()){
                rc.spawn(spawnDir,RobotType.SOLDIER);
            }
            rc.yield();
        }
    }

    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            if(rc.isCoreReady()) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
                }
            }
            if(rc.isCoreReady()){
                if(rc.getRoundLimit() * .5 < Clock.getRoundNum()) {
                    Direction moveDir = getMoveDir(theirHQ);
                    if (moveDir != null) {
                        rc.move(moveDir);
                    }
                } else {
                    Direction dir = directions[rand.nextInt(4)];
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                }
            }
            rc.yield();
        }
    }

    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            if(rc.isCoreReady()) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
                }
            }
            rc.yield();
        }
    }
}