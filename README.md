<h1>Using an IMU with RR's infamous three-deadwheel Localizer</h1>

<h3>Why?</h3>
Getting the rotation of a robot using deadwheels isn't always accurate.
This inaccurate rotation affects the robot's estimated x and y positions on field axes.


<h3>Why don't we use the IMU sensor (gyro) to get the robot's rotation?</h3>
After all the sensor is much more accurate.
The sensor takes quite a large time to read, meaning loop times will be negatively affected.

<h3>How do we get the best of both?</h3>

Through reading the IMU infrequently <b>(from 100ms+)</b> you keep Loop Times at a low of below <b>8ms</b> (using nothing other than the IMU and odometry wheels)
Is it effective? Testing has proven it to have an average of 1% + difference on my Team's Robot. 
A <b>1% difference</b> can be the reason you win and lose a game, as autos in FTC often require the robot to be positioned <i>perfectly</i> when depositing game objects.

Don't believe these statistics? This project was created so that you can test it yourself!

<h2>If you have not already downloaded RR: </h2>
Clone this quickstart, and run the opMode named Backtracking_TUNING in the FTC Dashboard.
(Make sure to read the telemetry instructions in the init phase of the Opmode)

<h2> If you already have RR installed: </h2>
You will only need the package named Backtracking.
Once you add that to your repository,
You will need to make a few changes to RR's files (Will take less than 3 minutes, Android Studio will likely recommend you do these things anyway):

In ThreeDeadWheelLocalizer: remove the <b>final</b> keyword from the class named ThreeDeadWheelLocalizer. Add the public keyword to these variables at the top: <i>public</i> int lastPar0Pos, lastPar1Pos, lastPerpPos; <i>public</i> boolean initialized;
In MecanumDrive: remove the <b>final</b> keyword from the class named MecanumDrive. Add the public keyword to these variables: 
1. <i>public</i> final DownsampledWriter estimatedPoseWriter,<i>public</i> final LinkedList<Pose2d> poseHistory.
2. Remove final from this variable <i>public</i> Localizer localizer;

All set! 
Run the opMode named Backtracking_TUNING in the FTC Dashboard.
(Make sure to read the telemetry instructions in the init phase of the Opmode)
