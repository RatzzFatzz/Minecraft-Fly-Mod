package at.pcgf.flymod;

import net.java.games.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

@Mod("flymod")
public class FlyModImpl {
    private static Field keybindings = null;

    public FlyModImpl() {
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) throws NoSuchFieldException {
        if(keybindings == null) {
            keybindings = KeyBinding.class.getDeclaredField("ALL");
            keybindings.setAccessible(true);
        }
    }

    @SubscribeEvent
    public void move(final TickEvent.PlayerTickEvent event) throws IllegalAccessException {
        isSpacePressed();
        Vector3d deltaVec = event.player.getDeltaMovement();
//        Vector3d vec = MovementManipulator.multiplyMovement(deltaVec);
        Vector3d vec = verticalMovement(deltaVec);
//        event.player.setShiftKeyDown(false);
//        event.player.setSprinting(false);
//        event.player.swinging = false;
        event.player.abilities.flying = true;
//        event.player.sendMessage(new TranslationTextComponent(vec.toString()), null);
        event.player.setDeltaMovement(vec);
    }

    public static Vector3d verticalMovement(Vector3d vector) throws IllegalAccessException {
        double y = vector.y();
        double flyUpDownBlocks = 2;
        if (isSpacePressed()) {
            y += flyUpDownBlocks;
        }
        return new Vector3d(vector.x, y, vector.z);
    }

    static boolean isSpacePressed() throws IllegalAccessException {
        if (keybindings == null) {
            return false;
        }
        Map<String, KeyBinding> bindings = (Map<String, KeyBinding>) keybindings.get(null);
        return bindings.get("key.jump").isDown();
    }
}
