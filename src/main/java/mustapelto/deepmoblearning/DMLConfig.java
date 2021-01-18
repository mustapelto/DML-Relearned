package mustapelto.deepmoblearning;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

import java.util.HashMap;
import java.util.Map;

@Config(modid = DMLConstants.ModInfo.ID, name = "dml_relearned/dmlRelearned")
@EventBusSubscriber
public class DMLConfig {
    @Name("Loot Fabricator RF Cost")
    @Comment("Energy cost of Loot Fabricator in RF/t")
    @RangeInt(min = 0)
    public static int LOOT_FABRICATOR_RF_COST = 256;

    @Name("GUI Overlay Settings")
    @Comment("Configure appearance of Data Model experience overlay")
    public static GuiOverlaySettings GUI_OVERLAY_SETTINGS = new GuiOverlaySettings();

    public static class GuiOverlaySettings {
        @Name("Position")
        @Comment("Overlay screen position. Valid values: topleft / topright / bottomright / bottomleft")
        public String POSITION = "topleft";

        @Name("Horizontal Padding")
        @Comment("Horizontal padding from selected corner")
        public int PADDING_HORIZONTAL = 0;

        @Name("Vertical Padding")
        @Comment("Vertical padding from selected corner")
        public int PADDING_VERTICAL = 0;

        public enum GuiPosition {
            TOP_LEFT("topleft"),
            TOP_RIGHT("topright"),
            BOTTOM_RIGHT("bottomright"),
            BOTTOM_LEFT("bottomleft");

            private final String value;
            private static final Map<String, GuiPosition> map = new HashMap<>();

            GuiPosition(String value) {
                this.value = value;
            }

            static {
                for (GuiPosition position : values())
                    map.put(position.value, position);
            }

            public static GuiPosition fromString(String positionString) {
                return map.getOrDefault(positionString, GuiPosition.TOP_LEFT);
            }
        }
        public GuiPosition getGuiPosition() {
            return GuiPosition.fromString(POSITION);
        }
    }

    @Name("Is Glitch Armor Creative Flight Enabled?")
    public static boolean GLITCH_CREATIVE_FLIGHT = true;

    @Name("Is Sooted Redstone Crafting Enabled?")
    public static boolean SOOTED_REDSTONE_CRAFTING = true;

    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(DMLConstants.ModInfo.ID)) {
            ConfigManager.sync(DMLConstants.ModInfo.ID, Config.Type.INSTANCE);
        }
    }
}
