# Treasure Chest Helper

While you're inside a player-owned house, hovering an item that can be stored in the costume room's **treasure chest** shows a tooltip with the Treasure Trails tier it belongs to, plus the minimum chest needed to store it:

> Treasure chest: **Medium** (Teak+)

| Tier | Minimum chest |
| --- | --- |
| Beginner, Easy | Oak |
| Medium | Teak |
| Hard, Elite, Master | Mahogany |

The tooltip works on any item widget while in a house: inventory, equipment, and the chest interface itself. It also works in the **bank** (bank items, placeholders, and your inventory while banking) wherever you are, since there is no bank inside a house.

The plugin makes no network requests; all item data is bundled with it.

## Item data

The item → tier mapping lives in [`chest_items.tsv`](src/main/resources/com/camjewell/treasurechesthelper/chest_items.tsv). It was generated from the [OSRS Wiki treasure chest space page](https://oldschool.runescape.wiki/w/Treasure_chest_space). Item IDs were resolved through the wiki's `infobox_item` bucket, so items whose in-game name differs from their wiki page (for example `Rune scimitar (guthix)`, which shows in-game as "Rune scimitar") still match.
