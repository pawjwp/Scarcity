package net.pawjwp.scarcity.mixin.compat;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Hides Obscure API's title-screen button
@Mixin(value = TitleScreen.class, priority = 1100)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void scarcity$hideObscureApiMenuButton(CallbackInfo ci) {
        if (!ScarcityConfig.hideObscureAPIMenuButton) return;

        Class<?> pButton;
        try {
            pButton = Class.forName("com.obscuria.obscureapi.client.screen.PButton");
        } catch (ClassNotFoundException e) {
            return;
        }

        for (GuiEventListener child : this.children()) {
            if (pButton.isInstance(child) && child instanceof AbstractWidget aw) {
                aw.visible = false;
            }
        }
    }
}
