package alex.taran.picworld;

import android.util.Log;


public class World {
	public enum WorldState {
		FINISHED,
		CRASHED,
		STOPPED,
		EXECUTION,
	}
	
	public World(GameField gameField, Robot initRobot) {
		this.initGameField = gameField;
		this.initRobot = initRobot;
		resetLevel();
	}
	
	public void beginExecution(Program program) {
		this.program = program;
		this.program.beginExecution();
		if (this.program.hasRemainingCommand() && validateCommand(program.getCurrentCommand())) {
			worldState = WorldState.EXECUTION;
		}
		completionOperationProgress = 0.0f;
		resetLevel();
	}
	
	public void interruptExecution() {
		worldState = WorldState.STOPPED;
		resetLevel();
	}
	
	public WorldState getWorldState() {
		return worldState;
	}
	
	public Robot.ImmutableRobot getCurrentRobotState() {
		return robot.getImmutable();
	}
	
	public float getCompletionOperationProgress() {
		return completionOperationProgress;
	}
	
	public Command getCurrentRunningCommand() {
		return program.getCurrentCommand();
	}
	
	public GameField getGameField() {
		return gameField;
	}
	
	private void resetRobot() {
		robot = initRobot.clone();
	}
	
	private void resetGameField() {
		gameField = new GameField(initGameField);
	}
	
	private void resetLevel() {
		resetRobot();
		resetGameField();
	}
	
	public void update(float deltaTime) {
		if (worldState != WorldState.EXECUTION) {
			return;
		}
		// 1 op/sec
		completionOperationProgress += deltaTime;
		while (completionOperationProgress >= 1.0f && program.hasRemainingCommand()) {
			completionOperationProgress -= 1.0f;
			executeCommand(program.getCurrentCommand());
			if(!program.nextCommand()) {
				worldState = WorldState.FINISHED;
				completionOperationProgress = 0.0f;
				return;
			}
			if(!validateCommand(program.getCurrentCommand())) {
				worldState = WorldState.CRASHED;
				completionOperationProgress = 0.0f;
				return;
			}
		}
	}
	
	private void executeCommand(Command cmd) {
		if (!validateCommand(cmd)) {
			throw new RuntimeException("Trying to execute not-validated command");
		}
		switch(cmd) {
		case MOVE_FORWARD: case JUMP: {
			robot.setPosX(robot.getPosX() + robot.getLookDirection().getLookX());
			robot.setPosZ(robot.getPosZ() + robot.getLookDirection().getLookZ());
			robot.setPosY(gameField.getCellAt(robot.getPosX(), robot.getPosZ()).getHeight());
			break;
		}
		case ROTATE_LEFT: {
			robot.rotateLeft();
			break;
		}
		case ROTATE_RIGHT: {
			robot.rotateRight();
			break;
		}
		case TOGGLE_LIGHT: {
			gameField.getCellAt(robot.getPosX(), robot.getPosZ()).toggleLight();
			break;
		}
		default:
			throw new RuntimeException("Trying to execute unsupported command in 'World' ");
		}
	}
	
	private boolean validateCommand(Command cmd) {
		switch(cmd) {
		case MOVE_FORWARD: case JUMP: {
			GameField.Cell currentCell = gameField.getCellAt(robot.getPosX(), robot.getPosZ());
			int nextCellX = robot.getPosX() + robot.getLookDirection().getLookX();
			int nextCellZ = robot.getPosZ() + robot.getLookDirection().getLookZ();
			if (nextCellX < 0 || nextCellZ < 0 || nextCellX >= gameField.getSizeX() ||
					nextCellZ > gameField.getSizeZ()) {
				return false;
			}
			GameField.Cell nextCell = gameField.getCellAt(nextCellX, nextCellZ);
			if (cmd == Command.MOVE_FORWARD) {
				if (currentCell.getHeight() == nextCell.getHeight()) {
					return true;
				} else {
					return false;
				}
			} else if (cmd == Command.JUMP) {
				if (currentCell.getHeight() == nextCell.getHeight() + 1 ||
						currentCell.getHeight() == nextCell.getHeight() - 1) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new RuntimeException("Wrong logic structure in 'validateCommand' in 'World'");
			}
			}
		case ROTATE_LEFT: 
		case ROTATE_RIGHT: return true; // always can rotate
		case TOGGLE_LIGHT: return true; // always can toggle light
		default:
			return false;
		}
	}
	
	private Robot robot;
	private Program program;
	private GameField gameField;
	
	private WorldState worldState = WorldState.STOPPED;
	private float completionOperationProgress;
	private final Robot initRobot;
	private final GameField initGameField;
}
