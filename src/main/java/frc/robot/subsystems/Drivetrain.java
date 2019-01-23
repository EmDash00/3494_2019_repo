package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;
import frc.robot.commands.drive.Drive;
import frc.robot.sensors.PDP;

public class Drivetrain extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    /**
     * Master CANSparkMax, left side.
     */
    private CANSparkMax driveLeftMaster;
    /**
     * Follower CANSparkMax, left side.
     */
    private CANSparkMax driveLeftFollowOne;
    /**
     * Additional follower CANSparkMax, left side.
     */
    private CANSparkMax driveLeftFollowTwo;

    /**
     * Master CANSparkMax, right side.
     */
    private CANSparkMax driveRightMaster;
    /**
     * Follower CANSparkMax, right side.
     */
    private CANSparkMax driveRightFollowOne;
    /**
     * Additional follower CANSparkMax, right side.
     */
    private CANSparkMax driveRightFollowTwo;


    private static Drivetrain INSTANCE = new Drivetrain();

    private Drivetrain() {
        this.driveLeftMaster = new CANSparkMax(RobotMap.DRIVETRAIN.leftMasterChannel, CANSparkMaxLowLevel.MotorType.kBrushless);

        this.driveLeftFollowOne = new CANSparkMax(RobotMap.DRIVETRAIN.leftFollower1Channel, CANSparkMaxLowLevel.MotorType.kBrushless);
        this.driveLeftFollowOne.follow(driveLeftMaster);

        this.driveLeftFollowTwo = new CANSparkMax(RobotMap.DRIVETRAIN.leftFollower2Channel, CANSparkMaxLowLevel.MotorType.kBrushless);
        this.driveLeftFollowTwo.follow(driveLeftMaster);

        this.driveRightMaster = new CANSparkMax(RobotMap.DRIVETRAIN.rightMasterChannel, CANSparkMaxLowLevel.MotorType.kBrushless);

        this.driveRightFollowOne = new CANSparkMax(RobotMap.DRIVETRAIN.rightFollower1Channel, CANSparkMaxLowLevel.MotorType.kBrushless);
        this.driveRightFollowOne.follow(driveRightMaster);

        this.driveRightFollowTwo = new CANSparkMax(RobotMap.DRIVETRAIN.rightFollower2Channel, CANSparkMaxLowLevel.MotorType.kBrushless);
        this.driveRightFollowTwo.follow(driveRightMaster);
    }

    /**
     * Tank drive.
     *
     * @param leftSpeed  Speed of left side.
     * @param rightSpeed Speed of right side.
     */
    public void tankDrive(double leftSpeed, double rightSpeed) {

        this.driveLeftMaster.set(leftSpeed);
        this.driveRightMaster.set(rightSpeed);

        if (!checkMotorsGoingSameDirection(leftSpeed, rightSpeed)) {
            this.stop();
        }
    }

    /**
     * checkDriveMotorsGoCorrectDirection sanity check
     * this checks to make sure that if the robot is told to go forward, its actually traveling forward instead of backward
     * <p>
     * leftCounter uses Math.signum to get the signs of the three left motors.
     * if the motor is traveling forward, leftCounter increments by 1. If backward, it decrements by 1.
     * leftCounter  will have values of 3 or -3 if all motors travel in the same direction.
     * the if statement makes sure all motors are going the same direction,
     * and that they are traveling the same direction as the variable leftSpeed is telling them to go
     * Ditto for right side.
     *
     * @param leftSpeed  Speed of left side.
     * @param rightSpeed Speed of right side.
     */
    //find better system than printing errors
    /*
    private boolean checkDriveMotorsGoCorrectDirection(double leftSpeed, double rightSpeed) {
        int leftCounter = 0;
        leftCounter += Math.signum(this.getLeftMasterDirection());
        leftCounter += Math.signum(this.getLeftFollowOneDirection());
        leftCounter += Math.signum(this.getLeftFollowTwoDirection());

        if (Math.abs(leftCounter) != 3 || Math.signum(this.getLeftMasterDirection()) != Math.signum(leftSpeed)) {
            System.out.println("One of the left motors is jammed! RIP");
            return false;
        }

        int rightCounter = 0;
        rightCounter += Math.signum(this.getRightMasterCurrent());
        rightCounter += Math.signum(this.getRightFollowOneCurrent());
        rightCounter += Math.signum(this.getRightFollowTwoCurrent());

        if (Math.abs(rightCounter) != 3 || Math.signum(this.getRightMasterCurrent()) != Math.signum(rightSpeed)) {
            System.out.println("One of the left motors is jammed! RIP");
            return false;
        }
        return true;
    }
    */

    private enum Direction{
        kBackward, //corresponds to 0
        kNeutral, //corresponds to 1
        kForward; //corresponds to 2
    }

    private boolean checkMotorsGoingSameDirection(double leftSpeed, double rightSpeed){
        if(getLeftMasterDirection()!=getLeftFollowOneDirection() ||
           getLeftMasterDirection()!=getLeftFollowTwoDirection())
        {
            this.stop();
            return false;
        }

        if((getLeftMasterDirection().ordinal()-1)!=Math.signum(leftSpeed)){
            return false;
        }

        if(getRightMasterDirection()!=getRightFollowOneDirection() ||
           getRightMasterDirection()!=getRightFollowTwoDirection())
        {
            return false;
        }

        if((getRightMasterDirection().ordinal()-1)!=Math.signum(rightSpeed)){
            return false;
        }
    }

    public Direction getLeftMasterDirection() {
        double motorDirection = Math.signum(this.driveLeftMaster.getEncoder().getVelocity());

        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    //add method to check if encoders are null
    public Direction getLeftFollowOneDirection() {
        double motorDirection = Math.signum(this.driveLeftFollowOne.getEncoder().getVelocity());
        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    public Direction getLeftFollowTwoDirection() {
        double motorDirection = Math.signum(this.driveLeftFollowTwo.getEncoder().getVelocity());
        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    public Direction getRightMasterDirection() {
        double motorDirection = Math.signum(this.driveRightMaster.getEncoder().getVelocity());
        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    public Direction getRightFollowOneDirection() {
        double motorDirection = Math.signum(this.driveRightFollowOne.getEncoder().getVelocity());
        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    public Direction getRightFollowTwoDirection() {
        double motorDirection = Math.signum(this.driveRightFollowTwo.getEncoder().getVelocity());
        if (motorDirection == 1) {
            return Direction.kForward;
        }

        if (motorDirection == -1) {
            return Direction.kBackward;
        }

        return Direction.kNeutral;
    }

    public void stop() {
        this.tankDrive(0, 0);
    }

    @Override
    public void periodic() {
        if (SmartDashboard.getBoolean("Display Drivetrain data?", false)) {
            System.out.println("The left side: " + PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftMasterChannel) + ", "
                    + PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftFollower1Channel) + ", " +
                    PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftFollower2Channel));
            System.out.println("The left side: " + PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftMasterChannel) + ", "
                    + PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftFollower1Channel) + ", " +
                    PDP.getInstance().getCurrent(RobotMap.DRIVETRAIN.leftFollower2Channel));
        }
    }

    public static Drivetrain getInstance() {
        return INSTANCE;
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
        setDefaultCommand(new Drive());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
