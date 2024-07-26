# Using an IMU with RR's infamous three-deadwheel Localizer

### Why?
Getting the rotation of a robot using deadwheels isn't always accurate.
This inaccurate rotation affects the robot's estimated x and y positions on field axes.


### Why don't we use the IMU sensor (gyro) to get the robot's rotation?
After all the sensor is much more accurate.
The sensor takes quite a large time to read, meaning loop times will be negatively affected.

### How do we get the best of both?

Through reading the IMU infrequently **(from 100ms+)** you keep Loop Times sub **10ms** <br>Note: using nothing other than the IMU and odometry encoders<be>

Is it effective? Testing on my team's robot has proven an increase in accuracy between 1-5%. 

Why such a large range?
If your robot's autonomous path involves a lot of one-directional rotation (largely clockwise/counterclockwise), it will have a larger impact.
Furthermore, if your odometry is largely affected by external forces: wheel slipping, robot-to-robot contact, etc., this localizer will be your auto's **lifesaver**.

Don't believe me? This project was created so that you can **test** it yourself!

## If you have not already downloaded RR:
Clone this quickstart, and **tune the THREE DEAD WHEEL LOCALIZER** using the <a href="https://rr.brott.dev/docs/v1-0/tuning/">RR 1.0 tuning docs</a>.

Run the opMode named Backtracking_TUNING in the FTC Dashboard.
**(Make sure to read the telemetry instructions in the init phase of the Opmode)**
And your set! Feel free to use the Kotlin and or Java versions.

## If you already have RR installed:
You will only need to clone the packages labeled Backtracking. You can choose either the Java or Kotlin version.
Once you transfer it into your own repository,
You will need to make a few changes to RR's files (Will take less than 3 minutes, Android Studio will likely recommend you do these things anyway):

In ThreeDeadWheelLocalizer: 
1. Remove the **final** keyword from the class named ThreeDeadWheelLocalizer. 
2. Add the public keyword to these variables at the top: *public* int lastPar0Pos, lastPar1Pos, lastPerpPos; *public* boolean initialized;

In MecanumDrive: 
1. Remove the **final** keyword from the class named MecanumDrive.
2. Add the public keyword to these variables: *public* final DownsampledWriter estimatedPoseWriter,*public* final LinkedList<Pose2d> poseHistory.
4. Remove final from this variable *public* Localizer localizer;

All set! 
Run the opMode named Backtracking_TUNING in the FTC Dashboard.
(Make sure to read the telemetry instructions in the init phase of the Opmode)

# Questions? DM me in discord at arya0244
Note: Any suggestions or feedback are extremely welcomed, as a new developer I am always looking to improve.
