package devils;
import java.awt.*;
import robocode.*;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Predador - a robot by (Rafael Trindade)
 */
public class Predador extends TeamRobot
{
	boolean movingForward;
	/**
	 * run: Predador's default behavior
	 */
	@Override
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setRadarColor(new Color(250, 0, 20));
		setScanColor(new Color(250, 0, 20));
		setBodyColor(Color.black);
		setBulletColor(Color.red);
		setGunColor(Color.black);
		// Robot main loop
		while(true) {
			if ( getRadarTurnRemaining() == 0.0 )
				setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
         * @param e
	 */
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())) {
			return;
		}

		// Absolute angle towards target
		double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		
    // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
		double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
		
    // Distance we want to scan from middle of enemy to either side
    // The 36.0 is how many units from the center of the enemy robot it scans.
		double extraTurn = Math.min( Math.atan( 36.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
		
    // Adjust the radar turn so it goes that much further in the direction it is going to turn
    // Basically if we were going to turn it left, turn it even more left, if right, turn more right.
    // This allows us to overshoot our enemy so that we get a good sweep that will not slip.
		if (radarTurn < 0)
			radarTurn -= extraTurn;
		else
			radarTurn += extraTurn;
		
    //Turn the radar
		setTurnRadarRightRadians(radarTurn);
		
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0) {
				fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
			}
		} // otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnGunRight(bearingFromGun);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0) {
			scan();
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
         * @param e
	 */
	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		reverseDirection();
	}
	
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
         * @param e
	 */
	
	@Override
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		reverseDirection();
	}

	@Override
	public
	void onWin(WinEvent e) {
        super.onWin(e); //To change body of generated methods, choose Tools | Templates.
    }

}
