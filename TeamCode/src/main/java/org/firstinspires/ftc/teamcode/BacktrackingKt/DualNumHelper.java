package org.firstinspires.ftc.teamcode.BacktrackingKt;

import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Time;

public class DualNumHelper {
    public static DualNum<Time> createDualNum(double[] list) {
        return new DualNum<>(list);
    }
}
