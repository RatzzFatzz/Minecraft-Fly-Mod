import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

public class MouseControlTest {
    public static Vec3d mouseControlMovementOld(Vec3d vec3d, boolean forwards, boolean backwards, boolean right,
                                                boolean left, float prevYaw, float prevPitch) {
        if (true) {
            boolean backwardsMovement = backwards;
            boolean forwardsMovement = forwards;
            boolean leftMovement = left;
            boolean rightMovement = right;
            float pitch = prevPitch;
            float yaw = prevYaw;
            boolean invert = false;
            if (forwardsMovement) {
                if (rightMovement) {
                    yaw += 45.0f;
                } else if (leftMovement) {
                    yaw += 315.0f;
                }
            } else if (backwardsMovement) {
                if (rightMovement) {
                    yaw += 315.0f;
                } else if (leftMovement) {
                    yaw += 45.0f;
                }
                invert = true;
            } else if (rightMovement) {
                pitch = 0.0f;
                yaw += 90.0f;
            } else if (leftMovement) {
                pitch = 0.0f;
                yaw += 270.0f;
            }
            if (yaw > 180.0f) {
                yaw -= 360.0f;
            }
            Vec3d e = Vec3d.fromPolar(pitch, yaw).normalize();
            System.out.println(e);
            double length = Math.sqrt((vec3d.getX() * vec3d.getX()) + (vec3d.getZ() * vec3d.getZ()));
            if (invert) {
                length = -length;
            }
            e = new Vec3d(e.getX() * length, e.getY() * length, e.getZ() * length);
            System.out.println(e);
            return e;
        }
        return vec3d;
    }

    private static Stream<Arguments> provideVectors() {
        return Stream.of(
                // move forward, look straight up and forward
                Arguments.of(new Vec3d(-1, 0, 0), new Vec3d(0, 1, 0), true, false, false, false, 90, -90),
                // move forward, look up and right
                Arguments.of(new Vec3d(-1, 0, 0), new Vec3d(0, 1, 0), true, false, false, false, 180, -90),
                // move forward, look forward
                Arguments.of(new Vec3d(-1, 0, 0), new Vec3d(-1, 0, 0), true, false, false, false, 90, 0),
                // move forward, look down and forward
                Arguments.of(new Vec3d(-1, 0, 0), new Vec3d(-0.707, -0.707, 0), true, false, false, false, 90, 45),
                // move forward, look down
                Arguments.of(new Vec3d(-1, 0, 0), new Vec3d(0, -1, 0), true, false, false, false, 90, 90),
                // move forward and right, look forward
                Arguments.of(new Vec3d(-1, 0, -1), new Vec3d(-1, 0, -1), true, false, true, false, 90, 0),
                // move backwards and left, look forward and left
                Arguments.of(new Vec3d(1, 0, 1), new Vec3d(1.414, 0, 0), false, true, false, true, 45, 0)
        );
    }

    public Vec3d mouseControlMovement(Vec3d vec3d, boolean forwards, boolean backwards, boolean right,
                                      boolean left, float prevYaw, float prevPitch) {
        if (true) {
            float pitch = prevPitch;
            float yaw = prevYaw;
            Vector4f directionsVector = new Vector4f(
                    (backwards ? 1 : 0) - (forwards ? 1 : 0),
                    0,
                    (left ? 1 : 0) - (right ? 1 : 0),
                    1);
            directionsVector.normalize();
            float length = (float) Math.sqrt((vec3d.getX() * vec3d.getX()) + (vec3d.getZ() * vec3d.getZ()));
            Vector4f movementVector = new Vector4f(
                    directionsVector.getX() * length,
                    directionsVector.getY() * length,
                    directionsVector.getZ() * length,
                    directionsVector.getW() * length
            );
            System.out.println(movementVector);
            // x y z // roll yaw pitch
            movementVector.rotate(new Quaternion(0, yaw, pitch, true));
            System.out.println(movementVector);
            Vec3d vec = new Vec3d(movementVector.getX() / movementVector.getW(), movementVector.getY() / movementVector.getW(), movementVector.getZ() / movementVector.getW());
            System.out.println(vec);
            return vec;
        }
        return vec3d;
    }

    @ParameterizedTest
    @MethodSource("provideVectors")
    void mouseControlMovementTest(Vec3d input, Vec3d expected, boolean forwards, boolean backwards, boolean right,
                                  boolean left, float prevYaw, float prevPitch) {
        mouseControlMovementOld(input, forwards, backwards, right, left, prevYaw, prevPitch);
        Vec3d result = mouseControlMovement(input, forwards, backwards, right, left, prevYaw, prevPitch);
        assertThat(result.getX()).as("X").isCloseTo(expected.getX(), byLessThan(0.001));
        assertThat(result.getY()).as("Y").isCloseTo(expected.getY(), byLessThan(0.001));
        assertThat(result.getZ()).as("Z").isCloseTo(expected.getZ(), byLessThan(0.001));
    }
}
