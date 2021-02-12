package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ButtonDeepLearnerSelect extends ButtonBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation(DMLConstants.ModInfo.ID, "textures/gui/buttons/button_deep_learner_select.png");
    private final Direction direction;

    public ButtonDeepLearnerSelect(int buttonId, int x, int y, Direction direction) {
        super(buttonId, x, y, 24, 24, TEXTURE);
        this.direction = direction;
    }

    @Nonnull
    @Override
    public ImmutableList<String> getTooltip() {
        switch (direction) {
            case NEXT:
                return ImmutableList.of(I18n.format("deepmoblearning.deep_learner.button_next"));
            case PREV:
                return ImmutableList.of(I18n.format("deepmoblearning.deep_learner.button_prev"));
            default:
                return ImmutableList.of();
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected int getState() {
        return direction == Direction.NEXT ? 1 : 0;
    }

    public enum Direction {
        NEXT, PREV
    }
}
