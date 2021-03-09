package mustapelto.deepmoblearning.common.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

import javax.annotation.Nonnull;

public class CommandDML extends CommandTreeBase {
    public CommandDML() {
        addSubcommand(new CommandConvert());
        addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    @Nonnull
    public String getName() {
        return "dml";
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "deepmoblearning.commands.dml.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
