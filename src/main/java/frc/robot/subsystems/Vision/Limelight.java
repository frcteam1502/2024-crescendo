// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.subsystems.Vision.LimelightHelpers.LimelightTarget_Fiducial;

/** Add your docs here. */
final class LimelightConfig{
    public static boolean IS_LIMELIGHT_MODE = true;
    public static int APRILTAG_PIPELINE;
    public static String POSE_LIMELIGHT = "limelight-pose";
    public static double POSE_LIME_X = -0.33195;
    public static double POSE_LIME_Y = 0.0;
    public static double POSE_LIME_Z = 0.395;
    public static double POSE_LIME_PITCH = 25;
    public static double POSE_LIME_ROLL = 0.0;
    public static double POSE_LIME_YAW = -180;
    
    public static double FIELD_LENGTH_METERS = 16.54;
    public static double FIELD_WIDTH_METERS = 8.02;
}

public class Limelight {
  
    private Pose2d botPose;
    private Pose2d tempPose;
    private double limeLatency;
    private boolean apriltagLimelightConnected = false;

    private LimelightHelpers.LimelightResults jsonResults;
    private Pose2d targetRobotRelativePose;

    public Limelight(){
        botPose = new Pose2d(0, 0, new Rotation2d(Units.degreesToRadians(0)));
        tempPose = new Pose2d(0, 0, new Rotation2d(Units.degreesToRadians(0)));
        targetRobotRelativePose = new Pose2d();
        limeLatency = 0.0;

        if (LimelightConfig.IS_LIMELIGHT_MODE) {
            LimelightHelpers.setLEDMode_ForceOff(LimelightConfig.POSE_LIMELIGHT);
            setLimelightPipeline(LimelightConfig.POSE_LIMELIGHT, LimelightConfig.APRILTAG_PIPELINE);
            LimelightHelpers.setCameraPose_RobotSpace(
                LimelightConfig.POSE_LIMELIGHT,
                LimelightConfig.POSE_LIME_X,
                LimelightConfig.POSE_LIME_Y,
                LimelightConfig.POSE_LIME_Z,
                LimelightConfig.POSE_LIME_ROLL,
                LimelightConfig.POSE_LIME_PITCH,
                LimelightConfig.POSE_LIME_YAW);
            }
    }

    public void update(){
        /*Ensures empty json not fed to pipeline*/
        apriltagLimelightConnected =
            !NetworkTableInstance.getDefault()
                .getTable(LimelightConfig.POSE_LIMELIGHT)
                .getEntry("json")
                .getString("")
                .equals("");
        
        if (LimelightConfig.IS_LIMELIGHT_MODE && apriltagLimelightConnected) {
            jsonResults = LimelightHelpers.getLatestResults(LimelightConfig.POSE_LIMELIGHT);
            tempPose = LimelightHelpers.getBotPose2d_wpiBlue(LimelightConfig.POSE_LIMELIGHT);
            if (visionAccurate(tempPose)) {
                // json dump more accurate?
                // Update Vision robotpose - need to read more about coordinate systems centered
                // Blue alliance means origin is bottom right of the field 
                limeLatency =
                    LimelightHelpers.getLatency_Pipeline(LimelightConfig.POSE_LIMELIGHT)
                        + LimelightHelpers.getLatency_Capture(LimelightConfig.POSE_LIMELIGHT);
                botPose = tempPose;
            }
        }
    }

    /**
   * @param limelight name of limelight to control in {@link VisionConfig}
   * @param pipelineIndex use pipeline indexes in {@link VisionConfig}
   */
    public void setLimelightPipeline(String limelight, int pipelineIndex) {
        LimelightHelpers.setPipelineIndex(limelight, pipelineIndex);
    }

      /**
   * @return whether or not vision sees a tag
   */
    public boolean isValidPose() {
        /* Disregard Vision if there are no targets in view */
        if (LimelightConfig.IS_LIMELIGHT_MODE) {
            return LimelightHelpers.getTV(LimelightConfig.POSE_LIMELIGHT);}
    return false;
    }

    public Pose2d getVisionBotPose(){
        return botPose;
    }

    public boolean isInMap(Pose2d currentPose) {
        return ((currentPose.getX() > 0.0 && currentPose.getX() <= LimelightConfig.FIELD_LENGTH_METERS)
            && (currentPose.getY() > 0.0 && currentPose.getY() <= LimelightConfig.FIELD_WIDTH_METERS));
    }

    /**
   * @return whether the camera sees multiple tags or not
   */
    public boolean multipleTargetsInView() {
        if (jsonResults == null) {
            return false;
        }
        
        LimelightTarget_Fiducial[] tags = jsonResults.targetingResults.targets_Fiducials;
        if (tags.length > 1) {
            return true;
        }
        
        return false;
    }

    /**
   * @return if vision should be trusted more than estimated pose
   */
    public boolean visionAccurate(Pose2d currentPose) {
        return isValidPose() && (isInMap(currentPose)||multipleTargetsInView());
    }

    /**
   * @return the total latency of the limelight camera
   */
    public double getTotalLatency() {
        return limeLatency;
    }

    /**
   * Gets the camera capture time in seconds. Only used for limelight
   *
   * @param latencyMillis the latency of the camera in milliseconds
   * @return the camera capture time in seconds
   */
    public double getTimestampSeconds(double latencyMillis) {
        return Timer.getFPGATimestamp() - (latencyMillis / 1000d);
    }
}

