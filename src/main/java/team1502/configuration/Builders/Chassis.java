package team1502.configuration.Builders;

import java.util.function.Function;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

public class Chassis extends Builder {
  private static final String NAME = "Chassis";

    public Chassis(Function<Chassis, Builder> fn) {
        super(NAME, NAME, fn);
    }

    public Chassis(Builder parent) {
        setPart(parent.getPart(NAME));
    }

    @Override
    public Builder createBuilder() {
        return new Chassis((Function<Chassis, Builder>)buildFunction);
    }

    public Chassis Square(double inches) {
        Value("chassisLayout", "square");
        Value("wheelBaseWidth", inches);
        Value("wheelBaseLength", inches);
        return this;
    }
    public Chassis Rectangular(double inchesWidth, double inchesLength) {
        Value("chassisLayout", "rectangular");
        Value("wheelBaseWidth", inchesWidth);
        Value("wheelBaseLength", inchesLength);
        return this;
    }

    public double WheelDiameter() { return Units.inchesToMeters(getDouble("wheelDiameter")); }
    public Chassis WheelDiameter(double inches) {
      Value("wheelDiameter", inches);
      return this;
  }

    /**
     * Assumes four modules
     * @param moduleNumber
     * @return
     */
    public Translation2d getModuleLocation(int moduleNumber) {
      double halfX = Units.inchesToMeters(getDouble("wheelBaseWidth"))/2;
      if (moduleNumber == 3 || moduleNumber == 4) {// the back row
        halfX = -halfX;
      }
      double halfY = Units.inchesToMeters(getDouble("wheelBaseLength"))/2;
      if (moduleNumber == 2 || moduleNumber == 4) {// the right side
        halfY = -halfY;
      }
      return new Translation2d(halfX, halfY);
    }
}

/*
  
  public static final double WHEEL_BASE_WIDTH = Units.inchesToMeters(23.25);
  public static final double WHEEL_BASE_LENGTH = Units.inchesToMeters(23.25);


  public static final Translation2d FRONT_LEFT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d FRONT_RIGHT_MODULE = new Translation2d(WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_LEFT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, WHEEL_BASE_WIDTH/2);
  public static final Translation2d BACK_RIGHT_MODULE = new Translation2d(-WHEEL_BASE_LENGTH/2, -WHEEL_BASE_WIDTH/2);

NOTE: THIS ORIENTATION IS ROTATED CCW 90deg

    |<-W/2->|

   1,1              1,-1                       ^
   [1]------|-------[2]   ---                  X
    :       :        :     ^                   |
    :       :        :    L/2                  |
    :       :        :     |                   |
    +-------+--------+    ---       < Y -------+
    :       :        :
    :       :        :
    :       :        :
   [3]------|-------[4]
  -1,1             -1,-1
    
  1  1, 1
  2  1,-1
  3 -1, 1
  4 -1,-1  


 */

