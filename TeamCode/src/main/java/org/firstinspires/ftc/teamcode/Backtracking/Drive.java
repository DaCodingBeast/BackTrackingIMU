package org.firstinspires.ftc.teamcode.Backtracking;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.messages.PoseMessage;

public class Drive extends MecanumDrive {
    public Drive(HardwareMap hardwareMap, Pose2d pose) {
        super(hardwareMap, pose);
        localizer = new Localizer(hardwareMap,lazyImu.get(), PARAMS.inPerTick);
    }
    Pose2d poseNew;
    double[] lastErrors = new double[3];
    static double [] TotalChange = new double[2];
    double [] prevPose = new double[2];
    double[] newPose= new double []{0.0,0.0};
    static double [] Difference = new double[2];

    //This is the exact percentage of the effectiveness of this Localizer
    double [] percentage_Correction = new double[]{0.0,0.0};
    @Override
    public PoseVelocity2d updatePoseEstimate() {
        Twist2dDual<Time> twist = localizer.update();

        poseNew = pose.plus(twist.value());

        //weird indexes because RR states that field Y is lateral sometimes
        pose = new Pose2d(poseNew.position.x + ErrorCalculator.errors[1] - lastErrors[1], poseNew.position.y + ErrorCalculator.errors[0] - lastErrors[0], poseNew.heading.toDouble() + ErrorCalculator.totalDrift - lastErrors[2]);

        lastErrors = new double[]{ErrorCalculator.errors[0], ErrorCalculator.errors[1], ErrorCalculator.totalDrift};

        poseHistory.add(pose);

        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        estimatedPoseWriter.write(new PoseMessage(pose));

        //DW return value never used
        return twist.velocity().value();
    }

    public double [] TotalMovement(){
        double [] positions = new double[]{pose.position.y,pose.position.x};
        for (int i =0; i<2; i++){
            prevPose[i] = newPose[i];
            newPose[i] = positions[i];
            Difference[i] = Math.abs(newPose[i] - prevPose[i]);
            TotalChange[i] += Difference[i];
        }

        if(TotalChange[0] != 0 && TotalChange[1]!= 0){
            for (int i =0; i<2; i++){
                percentage_Correction[i] = 100*Math.abs(ErrorCalculator.errors[i])/TotalChange[i];
            }
        }

        return percentage_Correction;
    }

}
