package virtuoel.pehkui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import virtuoel.pehkui.api.PehkuiConfig;
import virtuoel.pehkui.api.ScaleOperations;
import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.command.PehkuiEntitySelectorOptions;
import virtuoel.pehkui.util.CommandUtils;
import virtuoel.pehkui.util.ConfigSyncUtils;
import virtuoel.pehkui.util.GravityChangerCompatibility;
import virtuoel.pehkui.util.IdentityCompatibility;
import virtuoel.pehkui.util.ImmersivePortalsCompatibility;
import virtuoel.pehkui.util.ModLoaderUtils;
import virtuoel.pehkui.util.MulticonnectCompatibility;
import virtuoel.pehkui.util.ReachEntityAttributesCompatibility;

@ApiStatus.Internal
public class Pehkui implements ModInitializer
{
	public static final String MOD_ID = "pehkui";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	
	public Pehkui()
	{
		ScaleTypes.INVALID.getClass();
		ScaleOperations.NOOP.getClass();
		PehkuiConfig.BUILDER.config.get();
	}
	
	@Override
	public void onInitialize()
	{
		CommandUtils.registerArgumentTypes();
		
		PehkuiEntitySelectorOptions.register();
		
		CommandUtils.registerCommands();
		
		if (ModLoaderUtils.isModLoaded("fabric-networking-api-v1"))
		{
			ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			{
				if (!server.isHost(handler.player.getGameProfile()))
				{
					ConfigSyncUtils.syncConfigs(handler);
				}
				else
				{
					ConfigSyncUtils.resetSyncedConfigs();
				}
			});
		}
		
		GravityChangerCompatibility.INSTANCE.getClass();
		IdentityCompatibility.INSTANCE.getClass();
		ImmersivePortalsCompatibility.INSTANCE.getClass();
		MulticonnectCompatibility.INSTANCE.getClass();
		ReachEntityAttributesCompatibility.INSTANCE.getClass();
	}
	
	public static Identifier id(String path)
	{
		return new Identifier(MOD_ID, path);
	}
	
	public static Identifier id(String path, String... paths)
	{
		return id(paths.length == 0 ? path : path + "/" + String.join("/", paths));
	}
	
	public static final Identifier SCALE_PACKET = id("scale");
	public static final Identifier CONFIG_SYNC_PACKET = id("config_sync");
	public static final Identifier DEBUG_PACKET = id("debug");
}
