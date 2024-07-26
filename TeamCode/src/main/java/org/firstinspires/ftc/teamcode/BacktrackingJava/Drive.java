package org.firstinspires.ftc.teamcode.BacktrackingJava;

import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2d;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;

public class Drive extends MecDrive {

    public Drive(HardwareMap hwMap, Pose2d startPos) {
        super(hwMap, startPos);
        this.drive = this;
        createLocalizer(hwMap);
    }

    private double lastDrift = 0.0;
    public static double angleError = 0.0;

    public void correctCurrentPose(ArrayList<ArrayList<Double>> changesInPos, ArrayList<Pose2d> posHistory, ArrayList<Twist2d> twistChanges, double totalAngleDrift) {
        angleError += (totalAngleDrift - lastDrift);

        //loop through each estimated pose
        for (int i = 0; i < changesInPos.size(); i++) {
            double timeDrift = 1.0 / changesInPos.size() * totalAngleDrift;
            correctThePose(
                    new double[]{
                            changesInPos.get(i).get(0),
                            changesInPos.get(i).get(1),
                            changesInPos.get(i).get(2) + timeDrift},

                    posHistory.get(i),
                    twistChanges.get(i));
        }

        changesInPos.clear();
        posHistory.clear();
        twistChanges.clear();

        lastDrift = angleError;
    }


    private double[] lastErrors = new double[3];
    private double[] errors = new double[2];

    public void correctThePose(double[] change, Pose2d prevPose, Twist2d estTwist) {
        Twist2dDual<Time> imuTwist = new Twist2dDual<>(
                new Vector2dDual<>(
                        new DualNum<Time>(new double[]{
                                change[0],
                                0.0,
                        }).times(PARAMS.inPerTick),
                        new DualNum<Time>(new double[]{
                                change[1],
                                0.0,
                        }).times(PARAMS.inPerTick)
                ),
                new DualNum<>(new double[]{
                        change[2],
                        0.0,
                })
        );

        //because RR does not support subtracting twists or poses

        //Find pose using odo twist
        Pose2d previousPose = prevPose;
        previousPose = previousPose.plus(estTwist);
        double[] prevPosePoses = new double[]{previousPose.position.x, previousPose.position.y};

        //Find pose using imu twist
        Pose2d newPose = prevPose;
        newPose = newPose.plus(imuTwist.value());
        double[] newPosePoses = new double[]{newPose.position.x, newPose.position.y};

        for (int i = 0; i < 2; i++) {
            errors[i] += newPosePoses[i] - prevPosePoses[i];
        }

        pose = new Pose2d(
                pose.position.x + errors[0] - lastErrors[0],
                pose.position.y + errors[1] - lastErrors[1],
                (pose.heading.plus(angleError - lastErrors[2])).toDouble()
        );

        lastErrors = new double[]{
                errors[0],
                errors[1],
                angleError
        };
    }


}
