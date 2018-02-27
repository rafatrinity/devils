package devils;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.*;
import robocode.util.Utils;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * WannaCry - a robot by (your name here)
 */
public class WannaCry extends TeamRobot
{
	boolean movingForward;
	public void run() {
		setRadarColor(new Color(250, 0, 20));
		setScanColor(new Color(250, 0, 20));
		setBodyColor(Color.black);
		setBulletColor(Color.red);
		setGunColor(Color.black);
		double moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		setAhead(moveAmount);
		movingForward = true;
		while(true) {
			if ( getRadarTurnRemaining() == 0.0 )
				setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName()))
			return;
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
		double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
		double extraTurn = Math.min( Math.atan( 45.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
		if (radarTurn < 0)
			radarTurn -= extraTurn;
		else
			radarTurn += extraTurn;
		setTurnRadarRightRadians(radarTurn);
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			if (getGunHeat() == 0) fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
		}
		else
			turnGunRight(bearingFromGun);
		if (bearingFromGun == 0)
			scan();
	}

	public void onHitByBullet(HitByBulletEvent e){
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
		turnRight(40);
		super.setAdjustGunForRobotTurn(true);
		super.setAdjustRadarForGunTurn(true);
		execute();
	}

	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}	
}
