package at.pcgf.flymod;

import at.pcgf.flymod.gui.FlyModConfig;
import at.pcgf.flymod.gui.FlyModConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class FlyModImpl implements ClientModInitializer {
    public static FabricKeyBinding flyKey = FabricKeyBinding.Builder.create(
            new Identifier("flykey"),
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.flymod"
    ).build();


    public static byte flying = -1;
    public static final String MOD_ID = "flymod";

    @Override
    public void onInitializeClient() {
        FlyModConfig config = FlyModConfigManager.init();
        KeyBindingRegistry.INSTANCE.register(flyKey);
        ClientTickCallback.EVENT.register(e -> {
            if(flyKey.wasPressed()){
                System.out.println("b is pressed");
                flying = (byte) (flying > 0 ? 0 : 1);
            }
        });

    }
}