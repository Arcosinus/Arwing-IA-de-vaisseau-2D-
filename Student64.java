package fr.rphstudio.codingdojo.students;

import fr.rphstudio.codingdojo.game.Pod;
import fr.rphstudio.codingdojo.game.PodPlugIn;

/**
 *
 * @author Romuald GRIGNON
 */

public class Student64 extends PodPlugIn
{
    boolean shouldRecharge;

    public Student64(Pod p) {
        super(p);
    }

    float getRelativeAngleDifference64(float ShipAng, float CibAng)
    {
        if ((CibAng - ShipAng) > 180.f) {
            return -(180.f + (-180.f + ((CibAng - ShipAng) - 180.f)));
        }

        else if ((CibAng - ShipAng) < -180.f) {
            return (180.f - ((CibAng - ShipAng) + 180.f));
        }

        else if ((CibAng - ShipAng) > 0.f) {
            return 180.f - abs(abs(((CibAng) - ShipAng) - 180.f));
        }

        else {
            return 180.f - abs(abs(((CibAng) - ShipAng) - 180.f));
        }
    }

    float getAbsoluteAngleFromPositions64(float ShipX, float ShipY, float CibX, float CibY)
    {
        return atan2((CibY - ShipY), (CibX - ShipX));
    }

    void turnToAngle64(float AngCib)
    {
        turn(getRelativeAngleDifference64(getShipAngle(), AngCib));
    }

    void turnTowardPosition64(float X, float Y)
    {
        turnToAngle64(getAbsoluteAngleFromPositions64(getShipPositionX(), getShipPositionY(), X, Y));
    }

    float getDistanceBetweenPositions64(float ShipX, float ShipY, float CibX, float CibY)
    {
        float DistX, DistY;

        DistX = (ShipX > CibX) ? ShipX - CibX : CibX - ShipX;
        DistY = (ShipY > CibY) ? ShipY - CibY : CibY - ShipY;

        return (sqrt(DistX * DistX + DistY * DistY));
    }

    float getFirstChargingCheckPointX64()
    {
        return getCheckPointX(getFirstChargingCheckPointIndex64());
    }

    float getFirstChargingCheckPointY64()
    {
        return getCheckPointY(getFirstChargingCheckPointIndex64());
    }

    float getNextCheckPointX64(int index)
    {
        return getCheckPointX(getNextCheckPointIndex() + index);
    }

    float getNextCheckPointY64(int index)
    {
        return getCheckPointY(getNextCheckPointIndex() + index);
    }

    void turnTowardFirstChargingCheckPoint64()
    {
        turnTowardPosition64(getFirstChargingCheckPointX64(), getFirstChargingCheckPointY64());
    }

    float turnTowardNextCheckPoint64(int index)
    {
        turnTowardPosition64(getNextCheckPointX64(index), getNextCheckPointY64(index));

        return (getDistanceBetweenPositions64(getShipPositionX(), getShipPositionY(), getNextCheckPointX64(index), getNextCheckPointY64(index)));
    }

    void moveAndRecharge64(float minBatLvl, float maxBatLvl)
    {
        if ((getShipBoostLevel() == 100.f) && (updateChargingMode64(minBatLvl, maxBatLvl) && (getShipBatteryLevel() <= 30.f) && (getRelativeAngleDifference64(getShipAngle(), getAbsoluteAngleFromPositions64(getShipPositionX(), getShipPositionY(), getCheckPointX(getFirstChargingCheckPointIndex64()), getCheckPointY(getFirstChargingCheckPointIndex64()))) < 0.1f) && ((getDistanceBetweenPositions64(getShipPositionX(), getShipPositionY(), getCheckPointX(getFirstChargingCheckPointIndex64()), getCheckPointY(getFirstChargingCheckPointIndex64()))) >= 10.f))) {
            useBoost();
        }

        else if ((updateChargingMode64(minBatLvl, maxBatLvl) && (getShipBatteryLevel() <= 87.f))) {
            turnTowardFirstChargingCheckPoint64();
        }

        else if (shouldRecharge && getShipBatteryLevel() >= 87.f) {
            turnTowardNextCheckPoint64(0);
        }

        else if ((getShipBatteryLevel() >= 100.f) && (getShipBoostLevel() == 100.f) && (!shouldRecharge)) {
            useBoost();
        }

        else if ((getShipBatteryLevel() >= 30.f) && (getShipBoostLevel() == 100.f) && ((getDistanceBetweenPositions64(getShipPositionX(), getShipPositionY(), getCheckPointX(getNextCheckPointIndex()), getCheckPointY(getNextCheckPointIndex()))) >= 10.f) && (getRelativeAngleDifference64(getShipAngle(), getAbsoluteAngleFromPositions64(getShipPositionX(), getShipPositionY(), getCheckPointX(getNextCheckPointIndex()), getCheckPointY(getNextCheckPointIndex()))) < 0.1f)) {
            useBoost();
        }

        else {
            turnTowardNextCheckPoint64(0);
        }

        if (shouldRecharge)
        {
            if (getDistanceBetweenPositions64(getShipPositionX(), getShipPositionY(), getFirstChargingCheckPointX64(), getFirstChargingCheckPointY64()) <= 0.45f) {
                accelerateOrBrake(-0.5f);
            }

            else {
                accelerateOrBrake(0.75f);
            }
        }

        else
        {
            if ((getDistanceBetweenPositions64(getShipPositionX(), getShipPositionY(), getNextCheckPointX64(0), getNextCheckPointY64(0)) <= 4.f) && (getShipSpeed() >= 4.f)) {
                accelerateOrBrake(-0.55f);
            }

            else if ((getShipSpeed() <= 4.f)) {
                accelerateOrBrake(1.f);
            }

            else {
                accelerateOrBrake(0.75f);
            }
        }
    }

    public boolean updateChargingMode(float minBatteryLevel, float maxBatteryLevel) {
        return super.updateChargingMode(minBatteryLevel, maxBatteryLevel);
    }

    boolean updateChargingMode64(float minBatLvl, float maxBatLvl)
    {
        float battery = getShipBatteryLevel();

        if (battery <= minBatLvl) {
            shouldRecharge = true;
        }

        else if (battery >= maxBatLvl) {
            shouldRecharge = false;
        }

        return shouldRecharge;
    }

    int getFirstChargingCheckPointIndex64()
    {
        float min = Float.MAX_VALUE;
        int nbIndex = 0;

        for (int i = 0; i < getNbRaceCheckPoints(); i++)
        {
            if (isCheckPointCharging(i)) {
                if (getDistanceBetweenPositions64(getShipPositionX() ,getShipPositionY() ,getCheckPointX(i), getCheckPointY(i)) < min) {
                    min = getDistanceBetweenPositions64(getShipPositionX() ,getShipPositionY() ,getCheckPointX(i), getCheckPointY(i));

                    nbIndex = i;
                }
            }
        }

        return nbIndex;
    }

    @Override
    public void process(int delta)
    {
        setPlayerName("Arwing");

        if ((getShipShape() == 36) && (getPlayerColorRed() <= 135)) {
            selectShip(1);
        }

        else if (getPlayerColorRed() <= 135) {
            selectShip(getShipShape() + 1);
        }

        setPlayerColor(135, 128, 221, getPlayerColorTransparency());
        moveAndRecharge64(15.f, 100.f);
    }
}
