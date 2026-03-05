import re
import io

main_menu_path = r'c:\Users\mohza\IdeaProjects\Democracy-Core-Plugin\src\main\java\id\democracycore\gui\MainMenuGUI.java'
listener_path = r'c:\Users\mohza\IdeaProjects\Democracy-Core-Plugin\src\main\java\id\democracycore\gui\GUIListener.java'

with open(main_menu_path, 'r', encoding='utf-8') as f:
    text = f.read()

# Pattern for MainMenuGUI.java
# int playerHeadSlot = getGUISlot("gui.main_menu.items.player_head.gui_slot", 4);
# setItemSafe.accept(playerHeadSlot, playerHead);
pattern = re.compile(r'int\s+(\w+)Slot\s*=\s*getGUISlot\(([^,]+),\s*([^)]+)\);\s*setItemSafe\.accept\(\1Slot,\s*([^)]+)\);')
text = pattern.sub(r'getGUISlots(\2, \3).forEach(s -> setItemSafe.accept(s, \4));', text)

# For loadGenericConfigItems in MainMenuGUI:
# int slot = plugin.getGUIConfig().getInt(basePath + ".gui_slot", -1);
# if (slot < 0 || slot >= inventorySize)
#     continue;
# ...
# if (item != null) {
#     inv.setItem(slot, item);
# }
text = re.sub(
    r'int slot = plugin\.getGUIConfig\(\)\.getInt\(basePath \+ "\.gui_slot", -1\);(?:.*?)\n(.*?// Build the item purely from config)',
    r'List<Integer> slots = getGUISlots(basePath + ".gui_slot", -1);\n            for (int slot : slots) {\n                if (slot < 0 || slot >= inventorySize) continue;\n                ItemStack existing = inv.getItem(slot);\n                if (existing != null && existing.getType() != Material.AIR) continue;\n\n\1',
    text, flags=re.DOTALL
)

# Fix the end bracket for loadGenericConfigItems loop
text = re.sub(
    r'(inv\.setItem\(slot,\s*item\);\n\s*})\n\s+}',
    r'\1\n            }\n        }',
    text, count=1
)

# For getGenericItemKeyForSlot:
text = re.sub(
    r'int configSlot = plugin\.getGUIConfig\(\)\.getInt\(basePath \+ "\.gui_slot", -1\);\s*if\s*\(configSlot\s*==\s*slot\)\s*return key;',
    r'if (getGUISlots(basePath + ".gui_slot", -1).contains(slot))\n                return key;',
    text
)

with open(main_menu_path, 'w', encoding='utf-8') as f:
    f.write(text)

print("MainMenuGUI replaced.")

with open(listener_path, 'r', encoding='utf-8') as f:
    l_text = f.read()

# Pattern for GUIListener
# int closeSlot = mainMenuGUI.getItemSlot("close_button", 49);
# if (slot == closeSlot) {
pattern2 = re.compile(r'int\s+\w+Slot\s*=\s*mainMenuGUI\.getItemSlot\("([^"]+)",\s*([^)]+)\);\s*if\s*\(slot\s*==\s*\w+Slot\)\s*\{')
l_text = pattern2.sub(r'if (mainMenuGUI.isItemSlot("\1", slot, \2)) {', l_text)

with open(listener_path, 'w', encoding='utf-8') as f:
    f.write(l_text)

print("GUIListener replaced.")
