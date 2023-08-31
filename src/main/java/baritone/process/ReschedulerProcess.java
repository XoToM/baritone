package baritone.process;

import java.util.LinkedList;
import java.util.Queue;

import baritone.Baritone;
import baritone.api.process.IReschedulerProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.process.commandqueue.IQueuedCommand;
import baritone.utils.BaritoneProcessHelper;
import baritone.api.process.commandqueue.*;

public class ReschedulerProcess extends BaritoneProcessHelper implements IReschedulerProcess{
	Queue<IQueuedCommand> commandQueue;
	
	public ReschedulerProcess(Baritone baritone){
		super(baritone);
		commandQueue = new LinkedList<IQueuedCommand>();
	}
	
	@Override
	public double priority() {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public boolean isActive() {
		return !commandQueue.isEmpty();
	}
	
	@Override
	public void onLostControl() {
	}

	@Override
	public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
		if(baritone.getPathingBehavior().isPathing() || !baritone.getPathingControlManager().mostRecentInControl().isEmpty()) return new PathingCommand(null, PathingCommandType.DEFER);
		logDirect("Baritone idle, scheduling next command...");
		while(true) {
			IQueuedCommand command = commandQueue.poll();
			if(command == null) break;
			int result = command.runCommand(calcFailed);
			if(result == 0) break;
			if(result == -1) {
				calcFailed = true;
				logDirect("Error while scheduling command");
			}
			logDirect("Command skipped, attempting to schedule next command in queue");
		}
		return new PathingCommand(null, PathingCommandType.DEFER);
	}

	@Override
	public String displayName0() {
		return "IDLE";
	}
	
	@Override
	public void clearQueue() {
		commandQueue.clear();
	}
	@Override
	public void scheduleCommand(IQueuedCommand command) {
		commandQueue.add(command);
	}
}
