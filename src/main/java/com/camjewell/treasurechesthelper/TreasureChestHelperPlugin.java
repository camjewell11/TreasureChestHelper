package com.camjewell.treasurechesthelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Treasure Chest Helper",
	description = "Shows which POH treasure chest tier a Treasure Trails reward is stored in when hovering it inside your house",
	tags = {"poh", "house", "costume", "treasure", "chest", "clue", "stash", "construction"}
)
public class TreasureChestHelperPlugin extends Plugin
{
	private static final String ITEMS_RESOURCE = "chest_items.tsv";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TreasureChestTooltipOverlay overlay;

	private Map<Integer, ChestTier> itemTiers = Collections.emptyMap();

	// Every player-owned house has exactly one exit portal, so its presence in the scene means we're in a house.
	@Getter
	private boolean inHouse;

	@Override
	protected void startUp() throws Exception
	{
		itemTiers = loadItemTiers();
		overlayManager.add(overlay);
		clientThread.invoke(this::scanSceneForPortal);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		itemTiers = Collections.emptyMap();
		inHouse = false;
	}

	ChestTier getTier(int itemId)
	{
		return itemTiers.get(itemId);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Spawn events for the new scene arrive after LOADING, so clear here and let them set it again
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			inHouse = false;
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (event.getGameObject().getId() == ObjectID.POH_EXIT_PORTAL)
		{
			inHouse = true;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject().getId() == ObjectID.POH_EXIT_PORTAL)
		{
			inHouse = false;
		}
	}

	// One-off scan for when the plugin is enabled while already standing in a house
	private void scanSceneForPortal()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Scene scene = client.getTopLevelWorldView().getScene();
		for (Tile[][] plane : scene.getTiles())
		{
			for (Tile[] row : plane)
			{
				for (Tile tile : row)
				{
					if (tile == null)
					{
						continue;
					}

					for (GameObject object : tile.getGameObjects())
					{
						if (object != null && object.getId() == ObjectID.POH_EXIT_PORTAL)
						{
							inHouse = true;
							return;
						}
					}
				}
			}
		}
	}

	private Map<Integer, ChestTier> loadItemTiers() throws IOException
	{
		Map<Integer, ChestTier> tiers = new HashMap<>();
		try (InputStream in = TreasureChestHelperPlugin.class.getResourceAsStream(ITEMS_RESOURCE))
		{
			if (in == null)
			{
				throw new IOException("Missing resource " + ITEMS_RESOURCE);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.isEmpty() || line.startsWith("#"))
				{
					continue;
				}

				// TIER<TAB>itemId<TAB>name
				String[] parts = line.split("\t");
				tiers.put(Integer.parseInt(parts[1]), ChestTier.valueOf(parts[0]));
			}
		}

		log.debug("Loaded {} treasure chest items", tiers.size());
		return tiers;
	}
}
