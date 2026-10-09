package com.camjewell.treasurechesthelper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TreasureChestHelperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TreasureChestHelperPlugin.class);
		RuneLite.main(args);
	}
}
