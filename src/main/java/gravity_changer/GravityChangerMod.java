package gravity_changer;

import gravity_changer.api.RotationParameters;
import gravity_changer.command.GravityCommand;
import gravity_changer.config.GravityChangerConfig;
import gravity_changer.gravity_plate.PlatingBlock;
import gravity_changer.gravity_plate.PlatingBlockEntity;
import gravity_changer.item.GravityChangerItem;
import gravity_changer.item.GravityChangerItemAOE;
import gravity_changer.mob_effect.GravityDirectionMobEffect;
import gravity_changer.mob_effect.GravityInvertMobEffect;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String NAMESPACE = "gravity_changer";
    public static final Logger LOGGER = LogManager.getLogger(GravityChangerMod.class);
    
    public static CreativeModeTab GravityChangerGroup;
    
    public static ConfigHolder<GravityChangerConfig> configHolder;
    public static GravityChangerConfig config;
    
    @Override
    public void onInitialize() {
        GravityChangerItem.init();
        GravityChangerItemAOE.init();
        
        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(GravityChangerConfig.class);
        configHolder.registerSaveListener(new ConfigSerializeEvent.Save<GravityChangerConfig>() {
            @Override
            public InteractionResult onSave(ConfigHolder<GravityChangerConfig> configHolder, GravityChangerConfig gravityChangerConfig) {
                RotationParameters.updateDefault();
                return InteractionResult.PASS;
            }
        });
        config = configHolder.getConfig();
        
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );
        
        GravityChangerGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_DOWN));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_EAST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_WEST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_NORTH));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_SOUTH));
                
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_UP_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_DOWN_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_EAST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_WEST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_NORTH_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_SOUTH_AOE));
                
                entries.accept(new ItemStack(PlatingBlock.PLATING_BLOCK_ITEM));
                entries.accept(new ItemStack(PlatingBlock.DENSE_PLATING_BLOCK_ITEM));
            })
            .title(Component.translatable("itemGroup.gravity_changer.general"))
            .build();
        
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, id("general"),
            GravityChangerGroup
        );
        
        GravityDirectionMobEffect.init();
        GravityInvertMobEffect.init();
        GravityField.init();
        
        PlatingBlock.init();
        PlatingBlockEntity.init();
    }
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }
}