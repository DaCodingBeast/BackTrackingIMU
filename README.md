<h1>Using an IMU with RR's infamous three-deadwheel Localizer</h1>

<h3>Why?</h3>
Getting the rotation of a robot using deadwheels isn't always accurate.
This inaccurate rotation affects the robot's estimated x and y positions on field axes.


<h3>Why don't we use the IMU sensor (gyro) to get the robot's rotation?</h3>
After all the sensor is much more accurate.
The sensor takes quite a large time to read, meaning loop times will be negatively affected.

<h3>How do we get the best of both?</h3>

Through reading the IMU infrequently <b>(from 100ms+)</b> you keep Loop Times at a low <b>7ms</b> <br>Note: using nothing other than the IMU and odometry encoders<be>

Is it effective? Testing on my team's robot has proven an increase in accuracy of 1.5%<br>

Without looking at the big picture, it may seem to have little impact. But it can determine whether you win or lose a match, as autos in FTC often require the robot to be <i>perfectly</i> positioned when depositing game objects.

This can prevent errors of over 10 inches in the 30-second autonomous period.

Don't believe these statistics? This project was created so that you can <b>test</b> it yourself!

<h2>If you have not already downloaded RR: </h2>
Clone this quickstart, and tune using the <a href="https://rr.brott.dev/docs/v1-0/tuning/">RR 1.0 tuning docs</a>.

Then run the opMode named Backtracking_TUNING in the FTC Dashboard.

<b>(Make sure to read the telemetry instructions in the init phase of the Opmode)</b>

<h2> If you already have RR installed: </h2>
You will only need the package named Backtracking.
Once you add that to your repository,
You will need to make a few changes to RR's files (Will take less than 3 minutes, Android Studio will likely recommend you do these things anyway):

In ThreeDeadWheelLocalizer: 
1. Remove the <b>final</b> keyword from the class named ThreeDeadWheelLocalizer. 
2. Add the public keyword to these variables at the top: <i>public</i> int lastPar0Pos, lastPar1Pos, lastPerpPos; <i>public</i> boolean initialized;

In MecanumDrive: 
1. Remove the <b>final</b> keyword from the class named MecanumDrive.
2. Add the public keyword to these variables: <i>public</i> final DownsampledWriter estimatedPoseWriter,<i>public</i> final LinkedList<Pose2d> poseHistory.
4. Remove final from this variable <i>public</i> Localizer localizer;

All set! 
Run the opMode named Backtracking_TUNING in the FTC Dashboard.
(Make sure to read the telemetry instructions in the init phase of the Opmode)

<h1>Questions? DM me in discord at arya0244 </h1>
Note: Any suggestions or feedback are extremely welcomed, as a new developer I am always looking to improve.
