
package devils;

import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.*;
import java.awt.*;
import robocode.util.Utils;

public
class Killer extends TeamRobot {

    double moveAmount; // How much to move
    /**
     * run: Move around the walls
     */
    @Override
    public
    void run() {
        // Set colors
    	setRadarColor(new Color(250, 0, 20));
    	setScanColor(new Color(250, 0, 20));
    	setBodyColor(Color.black);
    	setBulletColor(Color.red);
    	setGunColor(Color.black);

        // Initialize moveAmount to the maximum possible for this battlefield.
    	moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
    	turnLeft(getHeading() % 90);
    	turnGunRight(90);
    	ahead(moveAmount);
    	while(true) {
    		if ( getRadarTurnRemaining() == 0.0 )
    			setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
    		execute();
    	}
    }

    /**
     * onHitRobot: Move away a bit.
     *
     * @param e
     */
    @Override
    public
    void onHitRobot(HitRobotEvent e) {
        // If he's in front of us, set back up a bit.
    	if (e.getBearing() > -90 && e.getBearing() < 90) {
    		back(100);
        } // else he's in back of us, so set ahead a bit.
        else {
        	ahead(100);
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public
    void onHitByBullet(HitByBulletEvent e) {
    	ahead(moveAmount);
    	execute();
    }

    /**
     *
     * @param e
     */
    @Override
    public
    void onHitWall(HitWallEvent e) {
    	setAhead(moveAmount);
    	turnRight(90);
    	super.setAdjustGunForRobotTurn(true);
    	super.setAdjustRadarForGunTurn(true);
    	execute();
    }

    /**
     *
     * @param e
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
    	if (isTeammate(e.getName())) return;
    	double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
    	double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
    	double extraTurn = Math.min( Math.atan( 45.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
    	if (radarTurn < 0)
    		radarTurn -= extraTurn;
    	else
    		radarTurn += extraTurn;
    	setTurnRadarRightRadians(radarTurn);
    	double absoluteBearing = getHeading() + e.getBearing();
    	double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
    	if (Math.abs(bearingFromGun) <= 3) {
    		turnGunRight(bearingFromGun);
    		if (getGunHeat() == 0) {
    			fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
    		}
    	}
    	else
    		turnGunRight(bearingFromGun);
    	if (bearingFromGun == 0)
    		scan();
    }

}