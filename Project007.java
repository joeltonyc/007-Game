import java.io.*;
import java.util.*;
import java.security.*;
import java.util.concurrent.*;

public class Project007 {

	public static SecureRandom r = new SecureRandom();
	public static Scanner sc = new Scanner(System.in);
	public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public static int maxLives, lifeC, lifeP, chargeP, chargeC, blockP, blockC;
	public static String choiceP, choiceC;
	public static boolean endGame; //Allows you to abort the game in the middle

	public Project007() {
		//Constructor
		blockP = blockC = chargeP = chargeC = 0;
		lifeC = lifeP = maxLives;
		choiceC = choiceP = "";
		gameTitle();
		ruleOutput();
	}

	public void gameEngine() throws Exception {
		initialise();
		do {
			System.out.print("\n\t\tOPTIONS\n(ENTER)   Play now\n   0.\t  EXIT game\n\nOption: ");
			String userOption = br.readLine().toUpperCase();
			if (lifeC <= 0 || lifeP <= 0) {
				break;
			}
			else if (userOption.equals("")) {
				//This ensures that enter is a valid input to continue the game
			}
			else if (userOption.substring(0, 1).equals("0")) {
				endGame = true;
				break;
			}
			gameTurn();
		} while (!endGame);
		clearScreen();
		gameOver();
		if (lifeC < lifeP) {
			youWin();
		}
		else if (lifeP < lifeC) {
			youLose();
		}
		else {
			draw();
		}
		System.out.println("\n\n\n");
		System.out.printf("\t\t\t\t\t\t\tLives Left (Player): %d\tLives left (Computer): %d\n\n", lifeP, lifeC);
		TimeUnit.SECONDS.sleep(2); //Waits before ending the program
		System.exit(0);
	}

	static String initialise() throws Exception {
		//Initialises the game
		System.out.print("Maximum lives: ");
		String maxLivesInput = br.readLine();
		if (!isInteger(maxLivesInput)) {
			System.out.println("Invalid input. Please try again");
			return initialise();
		}
		else if (maxLivesInput.substring(0, 1).equals("0")) {
			System.out.println("Enter a number greater than 0");
			return initialise();
		}
		else {
			maxLives = Integer.parseInt(maxLivesInput);
		}
		return "";
	}

	static void gameTurn() throws Exception {
		//Runs each method required to complete a game turn
		computerMove();
		scoreOutput();
		playerMove();
		scoreUpdater();
		System.out.printf("\nComputer's move: %s\n\n", choiceC);
		scoreOutput();
	}

	static void computerMove() {
		//Performs computer's move
		String option;
		String[] baseOptions = baseOptionsGenerator(); //Makes base options for the current game turn
		String[] options = optionsGenerator(baseOptions[r.nextInt(baseOptions.length)]); //Uses baseOptions and chooses a random base option
		option = options[r.nextInt(options.length)]; //Uses options and chooses a random option
		choiceC = option;
	}

	static String playerMove() throws Exception {
		//Player's move
		System.out.print("Enter A(ttack), B(lock) or C(harge): ");
		String baseInput = br.readLine().toUpperCase(); //Input for player's move
		if (baseInput.equals("")) {
			//Invalid input
			System.out.println("Invalid input. Please try again");
			return playerMove();
		}
		else if (baseInput.substring(0, 1).equals("A")) {
			//Attack
			if (chargeP == 0) {
				//Not enough charges to attack
				System.out.println("You have 0 charges. Please try another move");
				return playerMove();
			}
			else {
				System.out.print("\t\tOPTIONS\nCharges Required\tName\n\t1\t\t   Gun\n\t2\t\t   Dual Gun\n\t3\t\t   Grenade\n\t4\t\t   Machine Gun\n\t10\t\t  Missile\n\t25\t\t  Nuke\n\nOption: "); //Outputs attack options
				baseInput = br.readLine().toUpperCase(); //Reads attack option
				if (baseInput.equals("")) {
					//Invalid input
					System.out.println("Invalid input. Please try again");
					return playerMove();	//Restarts playerMove()
				}
				else if (Integer.parseInt(baseInput) > chargeP) {
					//Invaid input
					System.out.println("You do not have enough charges. Please try again");
					return playerMove();	//Restarts playerMove()
				}
				//Sets player choice and subtracts appropriate charges
				else if (chargeP >= 20 && baseInput.substring(0, 2).equals("20")) {
					//Nuke
					choiceP = "N";
					chargeP -= 20;
					blockP = 0;
				}
				else if (chargeP >= 10 && baseInput.substring(0, 2).equals("10")) {
					//Missile
					choiceP = "M";
					chargeP -= 10;
					blockP = 0;
				}
				else if (chargeP >= 4 && baseInput.substring(0, 1).equals("4")) {
					//Machine gun
					choiceP = "MG";
					chargeP -= 4;
					blockP = 0;
				}
				else if (chargeP >= 3 && baseInput.substring(0, 1).equals("3")) {
					//Grenade
					choiceP = "GR";
					chargeP -= 3;
					blockP = 0;
				}
				else if (chargeP >= 2 && baseInput.substring(0, 1).equals("2")) {
					//Dual gun
					choiceP = "D";
					chargeP -= 2;
					blockP = 0;
				}
				else if (chargeP >= 1 && baseInput.substring(0, 1).equals("1")) {
					//Gun
					choiceP = "G";
					chargeP -= 1;
					blockP = 0;
				}
				else {
					//Invalid input
					System.out.println("Invalid input. Please try again");
					return playerMove(); //Restarts player move
				}
			}
		}
		else if (baseInput.substring(0, 1).equals("B")) {
			//Block
			if (chargeP >= 2) {
				System.out.print("\t\tOptions\nB - Block\nSB - Super block\n\nOption: ");
				baseInput = br.readLine().toUpperCase();
				if (baseInput.equals("")) {
					//Invalid input
					System.out.println("Invalid input. Please try again");
					return playerMove();
				}
				else if (baseInput.substring(0, 1).equals("B")) {
					//Normal Block
					if (blockP >= 2) {
						System.out.println("You can only block twice in a row. Please try another move");
						return playerMove();
					}
					else {
						choiceP = "B";
						blockP++;
					}
				}
				else if (baseInput.substring(0, 2).equals("SB")) {
					//Super block
					choiceP = "SB";
					chargeP -= 2; //Charge update
					blockP = 0;
				}
				else {
					//Invalid input
					System.out.println("Incorrect input. Please try again");
					return playerMove();
				}
			}
			else {
				//No option here. Only normal block exists as super block needs 2 charges
				if (blockP >= 2) {
						System.out.println("You can only block twice in a row. Please try another move");
						return playerMove();
					}
					else {
						choiceP = "B";
						blockP++;
					}
			}
		}
		else if (baseInput.substring(0, 1).equals("C")) {
			//Charge
			choiceP = "C";
			chargeP++;
			blockP = 0;
		}
		else {
			//Invalid input
			System.out.println("Invalid input. Please try again.");
			return playerMove();
		}
		return ""; //Returns empty
	}

	static void scoreUpdater() throws Exception {
		//Done after each game turn. Will check computer option and the player option and score accordingly.
		//Computer's charges are modified now
		if (choiceP.equals("C")) {
			if (choiceC.equals("C")) {
				chargeC++;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
				lifeP--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
				lifeP--;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
				lifeP--;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
				lifeP--;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("B")) {
			if (choiceC.equals("C")) {
				chargeC++;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("SB")) {
			if (choiceC.equals("C")) {
				chargeC++;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
			}
		}
		else if (choiceP.equals("G")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
				lifeP--;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
				lifeP--;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
				lifeP--;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("D")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
				lifeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
				lifeP--;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
				lifeP--;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("GR")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
				lifeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
				lifeC--;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
				lifeP--;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("MG")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				chargeC--;
				lifeC--;
			}
			else if (choiceC.equals("D")) {
				chargeC -= 2;
				lifeC--;
			}
			else if (choiceC.equals("GR")) {
				chargeC -= 3;
				lifeC--;
			}
			else if (choiceC.equals("MG")) {
				chargeC -= 4;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
				lifeP--;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
				lifeP--;
			}
		}
		else if (choiceP.equals("M")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("B")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				lifeC--;
				chargeC--;
			}
			else if (choiceC.equals("D")) {
				lifeC--;
				chargeC -= 2;
			}
			else if (choiceC.equals("GR")) {
				lifeC--;
				chargeC -= 3;
			}
			else if (choiceC.equals("MG")) {
				lifeC--;
				chargeC -= 4;
			}
			else if (choiceC.equals("M")) {
				chargeC -= 10;
			}
			else if (choiceC.equals("N")) {
				lifeP--;
				chargeC -= 20;
			}
		}
		else if (choiceP.equals("N")) {
			if (choiceC.equals("C")) {
				lifeC--;
			}
			else if (choiceC.equals("B")) {
				lifeC--;
			}
			else if (choiceC.equals("SB")) {
				chargeC -= 2;
			}
			else if (choiceC.equals("G")) {
				lifeC--;
				chargeC--;
			}
			else if (choiceC.equals("D")) {
				lifeC--;
				chargeC -= 2;
			}
			else if (choiceC.equals("GR")) {
				lifeC--;
				chargeC -= 3;
			}
			else if (choiceC.equals("MG")) {
				lifeC--;
				chargeC -= 4;
			}
			else if (choiceC.equals("M")) {
				lifeC--;
				chargeC -= 10;
			}
			else if (choiceC.equals("N")) {
				chargeC -= 20;
			}
		}
		else {
			//Error in player choice
			System.out.println("Error in your choice. Please try again");
			playerMove();
		}
	}

	static void scoreOutput() {
		//Outputs score at every turn
		System.out.printf("Your charges: %d\tComputer's charges: %d\nLives Left (Player): %d\tLives left (Computer): %d\n\n", chargeP, chargeC, lifeP, lifeC);
	}

	static String[] optionsGenerator(String type) {
		//Makes options array for computerMove based on the type of move
		String[] options;
		type = (type.substring(0, 1)).toUpperCase();
		if (type.equals("A")) {
			//Making options based on charge available chargeC and chargeP
			if (chargeP >= 2) {
				if (chargeC >= 20) {
					options = new String[] {"G", "D", "GR", "MG", "M", "N", "G", "D", "GR", "MG"};
				}
				else if (chargeC >= 10) {
					options = new String[] {"G", "D", "GR", "MG", "M", "G", "D", "GR", "MG"};
				}
				else if (chargeC >= 4) {
					options = new String[] {"G", "D", "GR", "MG"};
				}
				else if (chargeC >= 3) {
					options = new String[] {"G", "D", "GR"};
				}
				else if (chargeC >= 2) {
					options = new String[] {"G", "D"};
				}
				else if (chargeC >= 1) {
					options = new String[] {"G"};
				}
				else {
					System.out.println("ERROR");
					options = new String[] {};
				}
			}
			else if (chargeP > 0) {
				if (chargeC >= 20) {
					options = new String[] {"N", "N", "M"};
				}
				else if (chargeC >= 10) {
					options = new String[] {"M"};
				}
				else if (chargeC >= 4) {
					options = new String[] {"G", "D", "GR", "MG", "D", "GR", "MG", "GR", "MG", "MG"};
				}
				else if (chargeC >= 3) {
					options = new String[] {"G", "D", "GR", "D", "GR", "GR"};
				}
				else if (chargeC >= 2) {
					options = new String[] {"G", "D", "D"};
				}
				else if (chargeC >= 1) {
					options = new String[] {"G"};
				}
				else {
					System.out.println("ERROR");
					options = new String[] {};
				}
			}
			else {
				if (chargeC >= 20) {
					options = new String[] {"N", "N", "M"};
				}
				else if (chargeC >= 10) {
					options = new String[] {"M"};
				}
				else if (chargeC >= 4) {
					options = new String[] {"G", "D", "GR", "MG"};
				}
				else if (chargeC >= 3) {
					options = new String[] {"G", "D", "GR"};
				}
				else if (chargeC >= 2) {
					options = new String[] {"G", "D"};
				}
				else if (chargeC >= 1) {
					options = new String[] {"G"};
				}
				else {
					System.out.println("ERROR");
					options = new String[] {};
				}
			}
		}
		else if (type.equals("B")) {
			//Block
			if (chargeC >= 2 && chargeP >= 10) {
				options = new String[] {"SB", "B", "SB"};
			}
			else {
				options = new String[] {"B"};
			}
		}
		else {
			//Charge
			options = new String[] {"C"};
		}
		return options;
	}

	static String[] baseOptionsGenerator() {
		//Makes baseOptions for the computerMove()
		String[] baseOptions;
		if (chargeC == 0) {
			if (chargeP == 0) {
				baseOptions = new String[] {"C"};
			}
			else {
				baseOptions = new String[] {"B", "C", "B", "C", "B"};
			}
		}
		else {
			if (chargeP == 0) {
				baseOptions = new String[] {"A", "C", "A"};
			}
			else if (chargeC > chargeP) {
				baseOptions = new String[] {"A", "B", "A", "B", "A"};
			}
			else {
				baseOptions = new String[] {"A", "B"};
			}
		}
		return baseOptions;
	}

	public static boolean isInteger(String s) {
		//Checks if String s is a valid Integer
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static void ruleOutput() {
		//Outputs rules at the beggining of the game
		System.out.println("\tRULES\n\nThe aim of this game is to survive longer than your opponent.\nThe game finishes when either you or the computer has 0 lives left.\n\nThere are 3 basic moves in this game.\n    Charge\n    Block\n    Attack\n\nYou can't Attack without having any Charges.\nWhen you Charge, you are vulnerable to Attack.\nThe Block doesn't use any Charges, and blocks all Attacks until the Machine Gun. (The Missile and Nuke can only be Blocked by the Super Block)\nThe Super Block always uses 2 charges, but will protect you from the Missile and Nuke.\nIn case both players attack, the one who uses a stronger attack (The attack which uses more charges) will prevail.\nYou can only Block 2 times in a row (Super block doesn't count)\n\n");
	}

	public static void clearScreen() {
		//Clears the screen
		System.out.print('\u000C');
	}

	public static void gameOver() {
		//Outputs game over
		System.out.println("\t\t\t\t\t   _____              __  __   ______      ____   __      __  ______   _____  ");
		System.out.println("\t\t\t\t\t  / ____|     /\\     |  \\/  | |  ____|    / __ \\  \\ \\    / / |  ____| |  __ \\ ");
		System.out.println("\t\t\t\t\t | |  __     /  \\    | \\  / | | |__      | |  | |  \\ \\  / /  | |__    | |__) |");
		System.out.println("\t\t\t\t\t | | |_ |   / /\\ \\   | |\\/| | |  __|     | |  | |   \\ \\/ /   |  __|   |  _  / ");
		System.out.println("\t\t\t\t\t | |__| |  / ____ \\  | |  | | | |____    | |__| |    \\  /    | |____  | | \\ \\ ");
		System.out.println("\t\t\t\t\t  \\_____| /_/    \\_\\ |_|  |_| |______|    \\____/      \\/     |______| |_|  \\_\\");
		System.out.println("\n\n\n\n");
	}

	public static void youWin() {
		//Outputs win message
		System.out.println("\t\t\t\t\t\t\t__     __          __          ___         _ ");
		System.out.println("\t\t\t\t\t\t\t\\ \\   / /          \\ \\        / (_)       | |");
		System.out.println("\t\t\t\t\t\t\t \\ \\_/ /__  _   _   \\ \\  /\\  / / _ _ __   | |");
		System.out.println("\t\t\t\t\t\t\t  \\   / _ \\| | | |   \\ \\/  \\/ / | | '_ \\  | |");
		System.out.println("\t\t\t\t\t\t\t   | | (_) | |_| |    \\  /\\  /  | | | | | |_|");
		System.out.println("\t\t\t\t\t\t\t   |_|\\___/ \\__,_|     \\/  \\/   |_|_| |_| (_)");
	}

	public static void youLose() {
		//Outputs lose message
		System.out.println("\t\t\t\t\t\t\t__     __           _                      _ ");
		System.out.println("\t\t\t\t\t\t\t\\ \\   / /          | |                    | |");
		System.out.println("\t\t\t\t\t\t\t \\ \\_/ /__  _   _  | |     ___  ___  ___  | |");
		System.out.println("\t\t\t\t\t\t\t  \\   / _ \\| | | | | |    / _ \\/ __|/ _ \\ | |");
		System.out.println("\t\t\t\t\t\t\t   | | (_) | |_| | | |___| (_) \\__ \\  __/ |_|");
		System.out.println("\t\t\t\t\t\t\t   |_|\\___/ \\__,_| |______\\___/|___/\\___| (_)");
	}

	public static void draw() {
		//Outputs draw message
		System.out.println("  \t\t\t\t\t\t _____ _   _               _____                       _ ");
		System.out.println("  \t\t\t\t\t\t|_   _| | ( )             |  __ \\                     | |");
		System.out.println("  \t\t\t\t\t\t  | | | |_|/ ___    __ _  | |  | |_ __ __ ___      __ | |");
		System.out.println("  \t\t\t\t\t\t  | | | __| / __|  / _` | | |  | | '__/ _` \\ \\ /\\ / / | |");
		System.out.println("  \t\t\t\t\t\t _| |_| |_  \\__ \\ | (_| | | |__| | | | (_| |\\ V  V /  |_|");
		System.out.println("  \t\t\t\t\t\t|_____|\\__| |___/  \\__,_| |_____/|_|  \\__,_| \\_/\\_/   (_)");
	}

	public static void gameTitle() {
		//Outputs game title
		System.out.println("\t\t\t\t\t\t\t  ___   ___ ______    _____                      ");
		System.out.println("\t\t\t\t\t\t\t / _ \\ / _ \\____  |  / ____|                     ");
		System.out.println("\t\t\t\t\t\t\t| | | | | | |  / /  | |  __  __ _ _ __ ___   ___ ");
		System.out.println("\t\t\t\t\t\t\t| | | | | | | / /   | | |_ |/ _` | '_ ` _ \\ / _ \\");
		System.out.println("\t\t\t\t\t\t\t| |_| | |_| |/ /    | |__| | (_| | | | | | |  __/");
		System.out.println("\t\t\t\t\t\t\t \\___/ \\___//_/      \\_____|\\__,_|_| |_| |_|\\___|");
	}
}