package org.firstinspires.ftc.teamcode.BacktrackingJava;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name = "Backtracking_Tuning", group = "Linear OpMode")
public class Backtracking_TUNING extends LinearOpMode {
    //You can tune the time at which the program reads the IMU to find best accuracy
    public static int timeBetween_Reads = 300;
    MecDrive drive;
    ElapsedTime loopTime = new ElapsedTime();
    double total = 0.0, NumOfLoops = 0.0;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        drive = new Drive(hardwareMap, new Pose2d(0, 0, 0));

        loopTime.reset();

        while (opModeInInit()) {
            telemetry.addLine("The DIRECTIONS");
            telemetry.addLine("Tune variable in FTC Dashboard under Backtracking_Tuning");
            telemetry.addLine("Watch and tune for best localizer accuracy");
            telemetry.addLine("Turn and push your robot for about 30 seconds in every direction");
            telemetry.addLine("To make it more realistic simulate robot to robot contact");
            telemetry.update();
        }

        telemetry.clearAll();
        waitForStart();
        while (opModeIsActive()) {

            drive.updatePoseEstimate();

            NumOfLoops++;

            total += loopTime.milliseconds();
            if (total > 5000) {
                total = 0;
                NumOfLoops = 0;
            }

            telemetry.addData("LoopTime", (int) loopTime.milliseconds());
            if (NumOfLoops != 0) {
                telemetry.addData("Avg LoopT", (int) (total / NumOfLoops));
            }
            loopTime.reset();

            //Weird X is Y things are because Y in RR is lateral movement
            telemetry.addData("Overall Effectiveness X - In Percentage", drive.totalMovement()[1]);
            telemetry.addData("Overall Effectiveness Y - In Percentage", drive.totalMovement()[0]);

            telemetry.addData("Error recognized in degrees (R)", Math.toDegrees(drive.OverallError[2]));
            telemetry.addData("Error recognized in inches (X)", drive.OverallError[1]);
            telemetry.addData("Error recognized in inches (Y)", drive.OverallError[0]);

            telemetry.addData("Rotation - Pose", Math.toDegrees(drive.pose.heading.toDouble()));
            telemetry.addData("X - Pose", drive.pose.position.y*-1);
            telemetry.addData("Y - Pose", drive.pose.position.x);
            telemetry.update();
        }

    }


}

