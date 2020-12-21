package io.github.magicalbananapie.arcanespace.integration;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

public class ArcaneModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Return the screen here with the one you created from Cloth Config Builder
        return (screen) -> AutoConfig.getConfigScreen(ArcaneConfig.class, screen).get();
    }

    @Override
    public String getModId() {
        return ArcaneSpace.MOD_ID;
    }
}