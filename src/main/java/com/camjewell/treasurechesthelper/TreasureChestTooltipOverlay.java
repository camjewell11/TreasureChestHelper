package com.camjewell.treasurechesthelper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

class TreasureChestTooltipOverlay extends Overlay
{
	private final Client client;
	private final TreasureChestHelperPlugin plugin;
	private final TooltipManager tooltipManager;
	private final ItemManager itemManager;

	@Inject
	TreasureChestTooltipOverlay(Client client, TreasureChestHelperPlugin plugin, TooltipManager tooltipManager, ItemManager itemManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;
		this.itemManager = itemManager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.isMenuOpen())
		{
			return null;
		}

		MenuEntry[] menuEntries = client.getMenu().getMenuEntries();
		if (menuEntries.length == 0)
		{
			return null;
		}

		// The last entry is the one under the cursor (the left-click option)
		MenuEntry entry = menuEntries[menuEntries.length - 1];
		Widget widget = entry.getWidget();
		if (widget == null || !(plugin.isInHouse() || isBankWidget(widget)))
		{
			return null;
		}

		int itemId = getItemId(widget);
		if (itemId <= 0)
		{
			return null;
		}

		// Resolves bank placeholders (and notes) to the real item
		ChestTier tier = plugin.getTier(itemManager.canonicalize(itemId));
		if (tier == null)
		{
			return null;
		}

		tooltipManager.add(new Tooltip("Treasure chest: "
			+ ColorUtil.wrapWithColorTag(tier.getDisplayName(), tier.getColor())
			+ " (" + tier.getMinimumChest() + "+)"));
		return null;
	}

	// There's no bank in a house, so the bank and its inventory panel are allowed anywhere
	private static boolean isBankWidget(Widget widget)
	{
		int interfaceId = WidgetUtil.componentToInterface(widget.getId());
		return interfaceId == InterfaceID.BANKMAIN || interfaceId == InterfaceID.BANKSIDE;
	}

	private static int getItemId(Widget widget)
	{
		if (widget.getItemId() > 0)
		{
			return widget.getItemId();
		}

		// Equipment slots hold the item in a child widget
		Widget child = widget.getChild(1);
		return child != null ? child.getItemId() : -1;
	}
}
