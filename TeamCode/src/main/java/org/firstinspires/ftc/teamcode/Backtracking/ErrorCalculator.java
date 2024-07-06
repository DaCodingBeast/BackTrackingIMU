package org.firstinspires.ftc.teamcode.Backtracking;

import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2dDual;

import java.util.ArrayList;
import java.util.List;

public class ErrorCalculator {
    double inPerTick;

    public ErrorCalculator(double inPerTick) {
        this.inPerTick = inPerTick;
        errors[0] =0;
        errors[1] =0;
        totalDrift =0;
    }
    public static double [] errors = new double[]{0.0,0.0};
    Pose2d pose = new Pose2d(0,0,0);
    public static double totalDrift;

    public void Recalculate(ArrayList<List<Double>> previousPoses, double totalDriftSinceRead) {
        ErrorCalculator.totalDrift = totalDriftSinceRead;

        //if you just wanted to check difference between estimation and correct data after iterations are complete
        //double latestDW = previousPoses.get(previousPoses.size()-1).get(2);

        //Find the correct heading of each moment
        double numOfIterations = previousPoses.size();


        //loop through each estimated pose
        for (int i = 0; i < numOfIterations; i++) {

            //set prev pose variables
            double estAxialChange = previousPoses.get(i).get(0), estLateralChange = previousPoses.get(i).get(1);
            double estHeadingChange = previousPoses.get(i).get(2);

            double [] estimatedPoses = new double[]{estAxialChange, estLateralChange, estHeadingChange};

            //Discover Global Localizer Errors
            double [] estimation = FindGlobalError(estimatedPoses);


            double driftRelative_TO_Time = (i / numOfIterations) * totalDriftSinceRead;
            double correctHeading = estHeadingChange + driftRelative_TO_Time;

            double [] correctedPoses = new double[]{estAxialChange, estLateralChange, correctHeading};

            double [] betterEstimation = FindGlobalError(correctedPoses);

            errors[0] += (betterEstimation[0] - estimation[0]);
            errors[1] += (betterEstimation[1] - estimation[1]);
        }

        previousPoses.clear();
    }

    public double[] FindGlobalError(double[] change) {
        pose = new Pose2d(0, 0, 0);
        double[] localizerValues = new double[2];

        Twist2dDual<Time> twist = new Twist2dDual<>(
                new Vector2dDual<>(
                        new DualNum<Time>(new double[]{
                                change[0],
                                0.0,
                        }).times(inPerTick),
                        new DualNum<Time>(new double[]{
                                change[1],
                                0.0,
                        }).times(inPerTick)
                ),
                new DualNum<>(new double[]{
                        change[2],
                        0.0,
                })
        );

        pose = pose.plus(twist.value());

        localizerValues[0] = pose.position.y;
        localizerValues[1] = pose.position.x;

        return localizerValues;
    }
}
