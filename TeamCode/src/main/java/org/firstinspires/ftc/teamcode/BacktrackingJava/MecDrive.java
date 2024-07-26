package org.firstinspires.ftc.teamcode.BacktrackingJava;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.messages.PoseMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class MecDrive extends MecanumDrive {
    public Drive drive = null;
    Pose2d poseWithoutBacktracking = new Pose2d(0.0, 0.0, 0.0);
    double[] OverallError = new double[3];

    public MecDrive(HardwareMap hardwareMap, Pose2d pose) {
        super(hardwareMap, pose);
    }

    public void createLocalizer(HardwareMap hardwareMap) {
        localizer = new Localizer(hardwareMap, lazyImu.get(), PARAMS.inPerTick, drive);
    }

    @Override
    public PoseVelocity2d updatePoseEstimate() {
        Twist2dDual<Time> twist = localizer.update();
        pose = pose.plus(twist.value());

        poseWithoutBacktracking = poseWithoutBacktracking.plus(twist.value());

        OverallError = new double[]{
                Math.abs(poseWithoutBacktracking.position.x - pose.position.x),
                Math.abs(poseWithoutBacktracking.position.y - pose.position.y),
                Math.abs(poseWithoutBacktracking.heading.minus(pose.heading))
        };

        poseHistory.add(pose);

        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        estimatedPoseWriter.write(new PoseMessage(pose));

        //DW return value never used
        return twist.velocity().value();
    }

    double[] percentage_Correction = new double[2], prevPose = new double[2];
    private ArrayList<Double> totalChange = new ArrayList<>(Arrays.asList(0.0, 0.0));


    // This returns the effectiveness of the Localizer in percentage
    public double[] totalMovement() {
        double[] currentPose = new double[]{pose.position.x, pose.position.y};
        double[] difference = new double[2];
        for (int i = 0; i < 2; i++) {
            difference[i] = Math.abs(currentPose[i] - prevPose[i]);
            totalChange.set(i, totalChange.get(i) + difference[i]);
            if (!totalChange.contains(0.0)) {
                percentage_Correction[i] = 100 * Math.abs(OverallError[i]) / totalChange.get(i);
            }
        }
        prevPose = currentPose;
        return percentage_Correction;
    }

}
