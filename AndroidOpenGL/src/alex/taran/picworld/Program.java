package alex.taran.picworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Program {
	public Program(Map<String, List<Command>> program) {
		this.program = program;
	}
	
	public void beginExecution() {
		beginExecution("main");
	}
	
	public void beginExecution(String procName) {
		currentProcedureName = procName;
		currentCommandIndex = 0;
	}
	
	public Command getCurrentCommand() {
		return program.get(currentProcedureName).get(currentCommandIndex);
	}
	
	public boolean nextCommand() {
		List<Command> currentProc = program.get(currentProcedureName);
		++currentCommandIndex;
		while (currentProc.size() <= currentCommandIndex) {
			if (callStack.size() == 0) {
				return false;
			} else {
				ReturnPoint ret = callStack.remove(callStack.size() - 1);
				currentCommandIndex = ret.commandIndex;
				currentProcedureName = ret.procedureName;
				++currentCommandIndex;
			}
		}
		Command cmd = program.get(currentProcedureName).get(currentCommandIndex);
		while (cmd.isCall()) {
			String name = cmd.getProcName();
			if (program.get(name).size() > 0) {
				callStack.add(new ReturnPoint(currentProcedureName, currentCommandIndex));
				currentProcedureName = name;
				currentCommandIndex = 0;
			} else {
				return nextCommand();
			}
			cmd = program.get(currentProcedureName).get(currentCommandIndex);
		}
		return true;
	}
	
	private class ReturnPoint {
		public ReturnPoint(String procedureName, int commandIndex) {
			this.procedureName = procedureName;
			this.commandIndex = commandIndex;
		}
		public final String procedureName;
		public final int commandIndex;
	}
	
	private int currentCommandIndex;
	private String currentProcedureName;
	private final List<ReturnPoint> callStack = new ArrayList<ReturnPoint>();
	private Map<String, List<Command>> program;
}
