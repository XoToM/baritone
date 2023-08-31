package baritone.api.process.commandqueue;

import baritone.api.command.exception.CommandException;

public interface IQueuedCommand {
	// returns true if the command wants to take control over the player, false otherwise
	public int runCommand(boolean hasLastFailed);
}
