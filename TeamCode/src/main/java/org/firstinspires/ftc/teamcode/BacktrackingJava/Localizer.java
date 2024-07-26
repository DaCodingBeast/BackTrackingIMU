package org.firstinspires.ftc.teamcode.BacktrackingJava;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2d;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.acmerobotics.roadrunner.ftc.FlightRecorder;
import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.messages.ThreeDeadWheelInputsMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class Localizer extends ThreeDeadWheelLocalizer {
    public final IMU imu;
    public ElapsedTime timer = new ElapsedTime();
    private final Drive drive;
    private Rotation2d deadWheelHeading = new Rotation2d(0.0, 0.0);
    private Rotation2d heading = new Rotation2d(0.0, 0.0);
    private ArrayList<ArrayList<Double>> listOfChanges = new ArrayList<>();
    private ArrayList<Pose2d> listOfPoses = new ArrayList<>();
    private ArrayList<Twist2d> listofEstimatedPosChanges = new ArrayList<>();


    //Initialize and reset
    public Localizer(HardwareMap hardwareMap, IMU imu, double inPerTick, @NonNull Drive drive) {
        super(hardwareMap, inPerTick);
        this.drive = drive;
        this.imu = imu;
        timer.reset();
        this.imu.resetYaw();

        //reset odowheels (Have to do this because RR's custom encoder class does not let you Reset)
        DcMotor parx = hardwareMap.get(DcMotor.class, "par0");
        DcMotor pary = hardwareMap.get(DcMotor.class, "par1");
        parx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pary.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        parx.close();
        pary.close();
    }


    @Override
    public Twist2dDual<Time> update() {
        boolean readImu = timer.milliseconds() >= Backtracking_TUNING.timeBetween_Reads;

        PositionVelocityPair par0PosVel = par0.getPositionAndVelocity();
        PositionVelocityPair par1PosVel = par1.getPositionAndVelocity();
        PositionVelocityPair perpPosVel = perp.getPositionAndVelocity();

        //get heading
        if (readImu) {
            YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
            heading = Rotation2d.exp(angles.getYaw(AngleUnit.RADIANS));
        }

        FlightRecorder.write("THREE_DEAD_WHEEL_INPUTS", new ThreeDeadWheelInputsMessage(par0PosVel, par1PosVel, perpPosVel));


        if (!initialized) {
            initialized = true;

            lastPar0Pos = par0PosVel.position;
            lastPar1Pos = par1PosVel.position;
            lastPerpPos = perpPosVel.position;
            deadWheelHeading = Rotation2d.exp((par0PosVel.position - par1PosVel.position) / (PARAMS.par0YTicks - PARAMS.par1YTicks));

            return new Twist2dDual<>(
                    Vector2dDual.constant(new Vector2d(0.0, 0.0), 2),
                    DualNum.constant(0.0, 2)
            );
        }

        int par0PosDelta = par0PosVel.position - lastPar0Pos;
        int par1PosDelta = par1PosVel.position - lastPar1Pos;
        int perpPosDelta = perpPosVel.position - lastPerpPos;

        double deadWheelHeadingDelta = (par0PosDelta - par1PosDelta) / (PARAMS.par0YTicks - PARAMS.par1YTicks);
        double axial = (PARAMS.par0YTicks * par1PosDelta - PARAMS.par1YTicks * par0PosDelta) / (PARAMS.par0YTicks - PARAMS.par1YTicks);
        double lateral = (PARAMS.perpXTicks / (PARAMS.par0YTicks - PARAMS.par1YTicks) * (par1PosDelta - par0PosDelta) + perpPosDelta);

        ArrayList<Double> poseChange = new ArrayList<>(Arrays.asList(axial, lateral, deadWheelHeadingDelta));
        listOfChanges.add(poseChange);
        listOfPoses.add(drive.pose);

        Twist2dDual<Time> twist = new Twist2dDual<>(
                new Vector2dDual<>(
                        new DualNum<Time>(new double[]{
                                axial,
                                (PARAMS.par0YTicks * par1PosVel.velocity - PARAMS.par1YTicks * par0PosVel.velocity) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                        }).times(inPerTick),
                        new DualNum<Time>(new double[]{
                                lateral,
                                (PARAMS.perpXTicks / (PARAMS.par0YTicks - PARAMS.par1YTicks) * (par1PosVel.velocity - par0PosVel.velocity) + perpPosVel.velocity),
                        }).times(inPerTick)
                ),
                new DualNum<>(new double[]{
                        deadWheelHeadingDelta,
                        (par0PosVel.velocity - par1PosVel.velocity) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                })
        );

        listofEstimatedPosChanges.add(twist.value());

        if (readImu) {
            double headingDrift = heading.minus(deadWheelHeading);
            drive.correctCurrentPose(listOfChanges, listOfPoses, listofEstimatedPosChanges, headingDrift);
            timer.reset();
        }

        lastPar0Pos = par0PosVel.position;
        lastPar1Pos = par1PosVel.position;
        lastPerpPos = perpPosVel.position;
        deadWheelHeading = Rotation2d.exp((par0PosVel.position - par1PosVel.position) / (PARAMS.par0YTicks - PARAMS.par1YTicks));

        return twist;
    }
}
