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
							//TODO Popear stack
						}
							break;
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
