package org.firstinspires.ftc.teamcode.BacktrackingJava;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "SampleAuto", group = "Linear OpMode")
public class SampleAuto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        //start Pose
        Drive drive = new Drive(hardwareMap, new Pose2d(0.0, 0.0, 0.0));

        waitForStart();
        while (opModeIsActive()) {
            drive.updatePoseEstimate();

            // Run your trajectories


            telemetry.update();
        }
    }
}
