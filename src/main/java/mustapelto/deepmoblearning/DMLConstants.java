package mustapelto.deepmoblearning;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.common.util.Point;
import mustapelto.deepmoblearning.common.util.Rect;
import net.minecraft.util.ResourceLocation;

public class DMLConstants {
    public static final class ModInfo {
        public static final String ID = "deepmoblearning";
        public static final String NAME = "DML Relearned";
        public static final String VERSION = "1.0.0";
    }

    public static final String MINECRAFT = "minecraft";

    public static final class ModDependencies {
        public static final String PATCHOULI = "patchouli";
        public static final String CRAFT_TWEAKER = "crafttweaker";
        public static final String DEP_STRING = ""; //"";after:" + PATCHOULI + ";after:" + CRAFT_TWEAKER;
    }

    public static final class Crafting {
        public static final int GLITCH_FRAGMENTS_PER_HEART = 3;
        public static final int SOOTED_REDSTONE_PER_REDSTONE = 1;
    }

    public static final class GlitchSword {
        public static final int DAMAGE_BONUS_INCREASE = 2;
        public static final int DAMAGE_BONUS_MAX = 18;
        public static final int DAMAGE_INCREASE_CHANCE = 6;
    }

    public static final class SimulationChamber {
        public static final int ENERGY_CAPACITY = 2000000;
        public static final int ENERGY_IN_MAX = 25600;
    }

    public static final class LootFabricator {
        public static final int ENERGY_CAPACITY = 2000000;
        public static final int ENERGY_IN_MAX = 25600;
    }

    public static final class Recipes {
        public static final class Placeholders {
            public static final String DATA_MODEL = "dataModel";
            public static final String PRISTINE_MATTER = "pristineMatter";
            public static final String LIVING_MATTER = "livingMatter";
        }

        public static final class Groups {
            public static final ResourceLocation DATA_MODELS = new ResourceLocation(DMLConstants.ModInfo.ID, "data_models");
            public static final ResourceLocation LIVING_MATTER = new ResourceLocation(DMLConstants.ModInfo.ID, "living_matter");
        }
    }

    public static final class DefaultModels {
        public static final ResourceLocation DATA_MODEL = new ResourceLocation(DMLConstants.ModInfo.ID, "items/data_model_default");
        public static final ResourceLocation LIVING_MATTER = new ResourceLocation(DMLConstants.ModInfo.ID, "items/living_matter_default");
        public static final ResourceLocation PRISTINE_MATTER = new ResourceLocation(DMLConstants.ModInfo.ID, "items/pristine_matter_default");
    }

    public static final class Gui {
        public static final int ROW_SPACING = 12;
        public static final ResourceLocation PLAYER_INVENTORY_TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/player_inventory.png");

        public static final class IDs {
            public static final int DEEP_LEARNER = 0;
            public static final int MACHINE = 1;
            public static final int TRIAL_KEYSTONE = 2;
        }

        public static final class Colors {
            public static final int AQUA = 0x62D8FF;
            public static final int WHITE = 0xFFFFFF;
            public static final int LIME = 0x00FFC0;
            public static final int BRIGHT_LIME = 0x33EFDC;
            public static final int BRIGHT_PURPLE = 0xC768DB;
        }
        
        public static final class SimulationChamber {
            // TEXTURE
            public static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/simulation_chamber.png");

            // DIMENSIONS
            public static final int WIDTH = 232;
            public static final int HEIGHT = 230;
            public static final Rect MAIN_GUI = new Rect(8, 0, 216, 141);
            public static final Point MAIN_GUI_TEXTURE_LOCATION = new Point(0, 0);

            // PLAYER INVENTORY
            public static final Point PLAYER_INVENTORY = new Point(28, 145);

            // STATUS DISPLAY
            public static final Point INFO_BOX = new Point(18, 9);
            public static final Point CONSOLE = new Point(29, 51);
            public static final int REDSTONE_DEACTIVATED_LINE_LENGTH = 28;
            public static final int BLINKING_CURSOR_SPEED = 16;

            // XP / ENERGY BAR LOCATIONS
            public static final Rect DATA_BAR = new Rect(14, 47, 7,87);
            public static final Point DATA_BAR_TEXTURE_LOCATION = new Point(18, 141);
            public static final Rect ENERGY_BAR = new Rect(211, 47, 7, 87);
            public static final Point ENERGY_BAR_TEXTURE_LOCATION = new Point(25, 141);

            // ITEM SLOT LOCATIONS
            public static final Rect DATA_MODEL_SLOT = new Rect(-14, 0, 18, 18);
            public static final Point DATA_MODEL_SLOT_TEXTURE_LOCATION = new Point(0, 141);
            public static final Point POLYMER_SLOT = new Point(192, 7);
            public static final Point LIVING_MATTER_SLOT = new Point(182, 27);
            public static final Point PRISTINE_MATTER_SLOT = new Point(202, 27);

            // BUTTON LOCATIONS
            public static final Point REDSTONE_BUTTON = new Point(-14, 24);
        }
        
        public static final class LootFabricator {
            // TEXTURE
            public static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/loot_fabricator.png");

            // DIMENSIONS
            public static final int WIDTH = 177;
            public static final int HEIGHT = 230;
            public static final Rect MAIN_GUI = new Rect(0, 0, 177, 83);
            public static final Point MAIN_GUI_TEXTURE_LOCATION = new Point(0, 0);

            // PLAYER INVENTORY
            public static final Point PLAYER_INVENTORY = new Point(0, 88);

            // ITEM SLOT LOCATIONS
            public static final Point INPUT_SLOT = new Point(79, 62);
            public static final Point OUTPUT_FIRST_SLOT = new Point(101, 7);
            public static final int OUTPUT_SLOT_SIDE_LENGTH = 18;

            // BUTTONS
            public static final Point REDSTONE_BUTTON = new Point(-20, 0);
            public static final Point OUTPUT_SELECT_LIST = new Point(14, 6);
            public static final int OUTPUT_SELECT_LIST_PADDING = 2;
            public static final int OUTPUT_SELECT_LIST_GUTTER = 1;
            public static final int OUTPUT_SELECT_BUTTON_SIZE = 18;
            public static final Point PREV_PAGE_BUTTON = new Point(13, 66);
            public static final Point NEXT_PAGE_BUTTON = new Point(44, 66);
            public static final Point DESELECT_BUTTON = new Point(79, 4);

            public static final int ITEMS_PER_PAGE = 9;

            public static final int PREV_PAGE_BUTTON_ID = 10;
            public static final int NEXT_PAGE_BUTTON_ID = 11;
            public static final int DESELECT_BUTTON_ID = 12;

            public static final int ITEM_SELECT_BUTTON_ID_OFFSET = 20;

            // PROGRESS AND ENERGY BAR
            public static final Rect ENERGY_BAR = new Rect(4, 6, 7, 71);
            public static final Point ENERGY_BAR_TEXTURE_LOCATION = new Point(0, 83);
            public static final Rect PROGRESS_BAR = new Rect(84, 22, 6,36);
            public static final Point PROGRESS_BAR_TEXTURE_LOCATION = new Point(7, 83);
            public static final Point ERROR_BAR_TEXTURE_LOCATION = new Point(13, 83);
            public static final long ERROR_BAR_CYCLE = 20; // Duration of one on-off cycle (ticks)
        }
        
        public static final class DeepLearner {
            // TEXTURE
            public static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/deep_learner.png");

            // DIMENSIONS
            public static final int WIDTH = 338;
            public static final int HEIGHT = 235;
            public static final Rect MAIN_GUI = new Rect(41, 0, 256, 140);
            public static final Point MAIN_GUI_TEXTURE_LOCATION = new Point(0, 0);

            // PLAYER INVENTORY
            public static final Point PLAYER_INVENTORY = new Point(81, 145);

            // MOB DISPLAY
            public static final Rect MOB_DISPLAY = new Rect(-41, 0, 75, 101);
            public static final Point MOB_DISPLAY_TEXTURE_LOCATION = new Point(0, 140);
            public static final Point MOB_DISPLAY_ENTITY = new Point(0, 80);

            // MAIN DISPLAY
            public static final Point TEXT_START = new Point(49, 8);
            public static final Rect HEART_ICON = new Rect(228, 2 * ROW_SPACING - 6, 9, 9);
            public static final Point HEART_ICON_TEXTURE_LOCATION = new Point(75, 140);
            public static final Point HEALTH_POINTS_HEADER_LOCATION = new Point(228, ROW_SPACING - 4);
            public static final Point HEALTH_POINTS_TEXT_LOCATION = new Point(239, 2 * ROW_SPACING - 4);

            // ITEM SLOTS
            public static final ImmutableList<Point> DATA_MODEL_SLOTS = ImmutableList.of(
                    new Point(257, 100),
                    new Point(275, 100),
                    new Point(257, 118),
                    new Point(275, 118)
            );

            // BUTTONS
            public static final Point PREV_MODEL_BUTTON = new Point(-27, 105);
            public static final Point NEXT_MODEL_BUTTON = new Point(-1, 105);
        }
        
        public static final class DataOverlay {
            public static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/experience_gui.png");

            public static final int COMPONENT_HEIGHT = 26;
            public static final int DATA_MODEL_WIDTH = 18;
            public static final int EXP_BAR_MAX_WIDTH = 89;
            public static final int EXP_BAR_INNER_HEIGHT = 11;
            public static final int EXP_BAR_OUTER_HEIGHT = 12;
            public static final int PADDING_BASE_HORIZONTAL = 5;
            public static final int PADDING_BASE_VERTICAL = 5;
        }

        public static final class ButtonTextures {
            public static final ResourceLocation DEEP_LEARNER_SELECT = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_deep_learner_select.png");
            public static final ResourceLocation ITEM_SELECT = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_select.png");
            public static final ResourceLocation PAGE_SELECT = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_page_select.png");
            public static final ResourceLocation REDSTONE_MODE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_redstone.png");
        }
    }
}
