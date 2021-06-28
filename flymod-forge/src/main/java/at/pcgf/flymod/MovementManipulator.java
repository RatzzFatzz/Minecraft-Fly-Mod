package at.pcgf.flymod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class MovementManipulator {
    private static boolean isSpacePressed;

    public static Vector3d multiplyMovement(Vector3d vector) {
        return new Vector3d(vector.x * 2,
                vector.y * 2,
                vector.z * 2);
    }

    public static void faceMovement(PlayerEntity player) {

    }

    public static Vector3d verticalMovement(Vector3d vector) throws NoSuchFieldException {
        double y = vector.y();
        double flyUpDownBlocks = 2;
        if (isSpacePressed) {
            y += flyUpDownBlocks;
        }

        return new Vector3d(vector.x, y, vector.z);
    }

    public boolean isSpacePressed() {
        return isSpacePressed;
    }

    public static void setSpacePressed(boolean spacePressed) {
        isSpacePressed = spacePressed;
    }
}
