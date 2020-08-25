package uniandes.lym.robot.control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.SwingUtilities;

import uniandes.lym.robot.kernel.*;

/**
 * Receives commands and relays them to the Robot.
 */

public class Interpreter {

	/**
	 * Robot's world
	 */
	private RobotWorldDec world;

	private boolean inRoutine = false;

	ArrayList<String> varNames = new ArrayList<>();
	ArrayList<Integer> varValues = new ArrayList<>();

	Stack<String> opStack;

	public Interpreter() {
	}

	/**
	 * Creates a new interpreter for a given world
	 *
	 * @param world
	 */

	public Interpreter(RobotWorld mundo) {
		this.world = (RobotWorldDec) mundo;

	}

	/**
	 * sets a the world
	 *
	 * @param world
	 */

	public void setWorld(RobotWorld m) {
		world = (RobotWorldDec) m;
	}

	private void assignVar(int n, String name) {
		for (int i = 0; i < varNames.size(); i++) {
			if (name.equals(varNames.get(i))) {
				varValues.add(i, n);
			}
		}
	}

	private void move(String n) throws Error {
		world.moveForward(getValueFromString(n));
	}

	private int getValueFromString(String val) {
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			for (int i = 0; i < varNames.size(); i++) {
			if (val.equals(varNames.get(i))) {
				return varValues.get(i);
			}
		}
		throw new Error("Unexpected parameter " + val);
		}
	}

	private void moveDir(String n, String D) throws Error {
		int times = getValueFromString(n);
		int initialO = world.getFacing();
		switch (initialO) {
			case 0:
				if (D.equals("front")) {
					world.moveForward(times);
				} else if (D.equals("back")) {
					world.moveForward(-times);
				} else if (D.equals("left")) {
					world.moveHorizontally(-times);
				} else if (D.equals("right")) {
					world.moveHorizontally(times);
				} else {
					throw new Error("Unexpected parameter: " + D);
				}
					break;
			case 1:
				if (D.equals("front")) {
					world.moveForward(-times);
				} else if (D.equals("back")) {
					world.moveForward(times);
				} else if (D.equals("left")) {
					world.moveHorizontally(times);
				} else if (D.equals("right")) {
					world.moveHorizontally(-times);
				} else {
					throw new Error("Unexpected parameter: " + D);
				}
					break;
			case 2:
				if (D.equals("front")) {
					world.moveHorizontally(times);
				} else if (D.equals("back")) {
					world.moveHorizontally(-times);
				} else if (D.equals("left")) {
					world.moveVertically(times);
				} else if (D.equals("right")) {
					world.moveVertically(-times);
				} else {
					throw new Error("Unexpected parameter: " + D);
				}
					break;
			case 3:
				if (D.equals("front")) {
					world.moveHorizontally(-times);
				} else if (D.equals("back")) {
					world.moveHorizontally(times);
				} else if (D.equals("left")) {
					world.moveVertically(-times);
				} else if (D.equals("right")) {
					world.moveVertically(times);
				} else {
					throw new Error("Unexpected parameter: " + D);
				}
					break;
			default:
					break;
		}
	}

	private void face(String O) throws Error {
		int initialO = world.getFacing();
		switch (initialO) {
			case 0:
				if (O.equals("north")) {
					break;
				} else if (O.equals("south")) {
					world.turnRight();
					world.turnRight();
				} else if (O.equals("east")) {
					world.turnRight();
				} else if (O.equals("west")) {
					world.turnRight();
					world.turnRight();
					world.turnRight();
				} else {
					throw new Error("Unexpected parameter: " + O);
				}
					break;
			case 1:
				if (O.equals("north")) {
					world.turnRight();
					world.turnRight();
				} else if (O.equals("south")) {
					break;
				} else if (O.equals("east")) {
					world.turnRight();
					world.turnRight();
					world.turnRight();
				} else if (O.equals("west")) {
					world.turnRight();
				} else {
					throw new Error("Unexpected parameter: " + O);
				}
					break;
			case 2:
				if (O.equals("north")) {
					world.turnRight();
					world.turnRight();
					world.turnRight();
				} else if (O.equals("south")) {
					world.turnRight();
				} else if (O.equals("east")) {
					break;
				} else if (O.equals("west")) {
					world.turnRight();
					world.turnRight();
				} else {
					throw new Error("Unexpected parameter: " + O);
				}
					break;
			case 3:
				if (O.equals("north")) {
					world.turnRight();
				} else if (O.equals("south")) {
					world.turnRight();
					world.turnRight();
					world.turnRight();
				} else if (O.equals("east")) {
					world.turnRight();
					world.turnRight();
				} else if (O.equals("west")) {
					break;
				} else {
					throw new Error("Unexpected parameter: " + O);
				}
					break;
			default:
					break;
	}
}

	private void moveInDir(String n, String O) {
		int steps = getValueFromString(n);
		face(O);
		world.moveForward(steps);
	}

	private boolean not(String method) {
		if (method.startsWith("facing")) {
			return !facing(method.replace("facing(", "").replace(")", ""));
		}
		else return false;
	}

	private void turn(String D) throws Error {
		if (D.equals("left")) {
			world.turnRight();
			world.turnRight();
			world.turnRight();
		} else if (D.equals("right")) {
			world.turnRight();
		} else if (D.equals("around")) {
			world.turnRight();
			world.turnRight();
		} else {
			throw new Error("Unexpected parameter " + D);
		}
	}

	private boolean facing(String o) throws Error {
		boolean res = false;
		switch (o) {
			case "north":
				res = world.facingNorth();
					break;
			case "east":
				res = world.facingEast();
					break;
			case "south":
				res = world.facingSouth();
			case "west":
				res = world.facingWest();
			default:
				throw new Error("Unexpected parameter " + o);
		}
		return res;
	}

	private void pick(String n, String X) throws Error {
		int num = getValueFromString(n);
		if (X.equals("Balloons")) {
			for (int i = 0; i < num; i++) {
				world.pickupBalloon();
			}
		} else if (X.equals("Chips")) {
			world.pickChips(num);
		} else {
			throw new Error("Unexpected parameter " + X);
		}
	}

	private void put(String n, String X) {
		int num = getValueFromString(n);
		if (X.equals("Balloons")) {
			world.putBalloons(num);
		} else if (X.equals("Chips")) {
			world.putChips(num);
		} else {
			throw new Error("Unexpected parameter " + X);
		}
	}

	private void execute(StringBuffer out) {
		try {
			while (!opStack.empty()) {
				String op = opStack.pop();
				if (op.startsWith("assignVar")) {
					String[] params = op.replace("assignVar", "").replace("(", "").replace(")", "").split(",");
					assignVar(Integer.parseInt(params[0].trim()), params[1].trim());
				} else if (op.startsWith("move")) {
					String param = op.replace("move(", "").replace(")", "").trim();
					move(param);
				} else if (op.startsWith("turn")) {
					String param = op.replace("turn(", "").replace(")", "").trim();
					turn(param);
				} else if (op.startsWith("face")) {
					String param = op.replace("face(", "").replace(")", "").trim();
					face(param);
				} else if (op.startsWith("put")) {
					String[] params = op.replace("put(", "").replace(")", "").split(",");
					put(params[0].trim(), params[1].trim());
				} else if (op.startsWith("pick")) {
					String[] params = op.replace("pick(", "").replace(")", "").split(",");
					pick(params[0].trim(), params[1].trim());
				} else if (op.startsWith("moveDir")) {
					String[] params = op.replace("moveDir(", "").replace(")", "").split(",");
					moveDir(params[0].trim(), params[1].trim());
				} else if (op.startsWith("moveInDir")) {
					String[] params = op.replace("moveInDir(", "").replace(")", "").split(",");
					moveInDir(params[0].trim(), params[1].trim());
				} else if (op.startsWith("facing")) {
					String param = op.replace("facing(", "").replace(")", "").trim();
					facing(param);
				} else {
					String param = op.replace("not(", "").replace(")", "");
					not(param);
				}
			}
		} catch (Error e) {
			out.append(e.getMessage());
		}
	}

	/**
	 * Processes a sequence of commands. A command is a letter followed by a ";" The
	 * command can be: M: moves forward R: turns right
	 *
	 * @param input Contiene una cadena de texto enviada para ser interpretada
	 */

	public String process(String input) throws Error {
		StringBuffer output = new StringBuffer("SYSTEM RESPONSE: -->\n");
		int i;
		int n;
		boolean ok = true;
		n = input.length();

		i = 0;
		try {
			while (i < n && ok) {
				System.out.println(inRoutine);
				switch (input.charAt(i)) {
					case 'M':
						if(!inRoutine) {
							world.moveForward(1);
							output.append("move \n");
						}
							break;
					case 'R':
						if (input.equals("ROBOT_R")) {
							output.append("Started Routine\n");
							inRoutine = true;
							ok = !ok;
						} else if (!inRoutine) {
							world.turnRight();
							output.append("turnRignt \n");
						}
							break;
					case 'C':
						if (!inRoutine) {
							world.putChips(1);
							output.append("putChip \n");
						}
							break;
					case 'B':
					if (input.equals("BEGIN")) {
						opStack =  new Stack<>();
						ok = !ok;
					}
					else if (!inRoutine) {
						world.putBalloons(1);
						output.append("putBalloon \n");
					}
						break;
					case 'c':
						if (!inRoutine) {
							world.pickChips(1);
							output.append("getChip \n");
						}
							break;
					case 'b':
						if (!inRoutine) {
							world.grabBalloons(1);
							output.append("getBalloon \n");
						}
							break;
					case 'V':
						if (input.startsWith("VARS")) {
							String[] vars = input.replace("VARS ", "").split(",");
							for (String string : vars) {
								if (string.trim().matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
									varNames.add(string);
								} else {
									throw new Error ("Invalid name for var " + string);
								}
							}
						}
						output.append("Vars added");
						ok = !ok;
						break;
					case 'a':
						if (input.startsWith("assignVar")) {
							String[] params = input.replace("assignVar", "").replace("(", "").replace(")", "").split(",");
							try {
								int val = Integer.parseInt(params[0]);
								opStack.push("assignVar(" + val + ","+params[1]+")");
							} catch (NumberFormatException e) {
								throw new Error("Invalid parameter " + params[0] + " is not an int");
							}
							ok = !ok;
						}
							break;
					case 'E':
						if (input.equals("END")) {
							ok = !ok;
							inRoutine = !inRoutine;
							execute(output);
						}
							break;
					case 's':
						if (input.equals("skip")) {
							ok = !ok;
						}
							break;
					case 'm':
						if (input.startsWith("move")) {
							opStack.push(input);
							ok = !ok;
						}
						if (input.startsWith("moveDir")) {
							opStack.push(input);
							ok = !ok;
						}
						if (input.startsWith("moveInDir"))
							break;
					case 'f':
						if (input.startsWith("facing")) {
							opStack.push(input);
							ok = !ok;
						}
							break;
					case 'n':
						if (input.startsWith("not")) {
							opStack.push(input);
							ok = !ok;
						}
							break;
					case 't':
						if (input.startsWith("turn")) {
							opStack.push(input);
							ok = !ok;
						}
							break;
					case 'p':
						if (input.startsWith("put")) {
							opStack.push(input);
							ok = !ok;
						}
						if (input.startsWith("pick")) {
							opStack.push(input);
							ok = !ok;
						}
					default:
						output.append(" Unrecognized command:  " + input.charAt(i));
						ok = false;
				}

				if (ok) {
					if (i + 1 == n) {
						if (!input.equals("ROBOT_R")) {
							output.append("expected ';' ; found end of input; ");
						}
						ok = false;
					} else if (input.charAt(i + 1) == ';' || inRoutine) {
						i = i + 2;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							System.err.format("IOException: %s%n", e);
						}

					} else {
						output.append(" Expecting ;  found: " + input.charAt(i + 1));
						ok = false;
					}
				}

			}

		} catch (Error e) {
			output.append("Error!!!  " + e.getMessage());

		}
		return output.toString();
	}
}
