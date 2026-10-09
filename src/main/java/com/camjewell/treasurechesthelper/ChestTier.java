package com.camjewell.treasurechesthelper;

import java.awt.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChestTier
{
	BEGINNER("Beginner", "Oak", new Color(0xB8B8B8)),
	EASY("Easy", "Oak", new Color(0x4CBB17)),
	MEDIUM("Medium", "Teak", new Color(0x3FA9F5)),
	HARD("Hard", "Mahogany", new Color(0xB563EB)),
	ELITE("Elite", "Mahogany", new Color(0xF2C511)),
	MASTER("Master", "Mahogany", new Color(0xE74C3C));

	private final String displayName;
	// Lowest treasure chest that can store this tier
	private final String minimumChest;
	private final Color color;
}
