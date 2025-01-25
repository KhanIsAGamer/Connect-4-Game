import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FinalProjectPF {
    static char[][] board = new char[6][7];

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        boolean anyoneWon = false;
        char player = 'X';

        try {
            loadGameHistory("history.txt");
        } catch (IOException e) {
            System.out.println("No previous game history found.");
        }

        InitializeBoard(0, 0); // Using recursive method
        printBoard(0, 0);

        while (!anyoneWon) {
            System.out.println("Player " + player + " Turn");
            System.out.print("Choose the position from 1 to 7: ");

            boolean inputValidation = false;
            while (!inputValidation) {
                try {
                    int column = input.nextInt() - 1;
                    if (isColumnFull(column)) {
                        System.out.println("Column is full. Try another one.");
                    } else if (placeCharacter(column, player, board.length - 1)) {
                        recordMove(column, player);
                        inputValidation = true;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid column. Enter a number between 1 and 7.");
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Enter a numeric value.");
                    input.nextLine(); // Clear invalid input
                }
            }

            printBoard(0, 0);
            anyoneWon = checkWin(player);
            if (anyoneWon) {
                System.out.println("Player " + player + " Won!");
                writeToFile(player);
                playAgain();
            } else if (isBoardFull()) {
                System.out.println("The game is a draw!");
                playAgain();
            }

            player = (player == 'X') ? 'Z' : 'X';
        }
    }

    public static void InitializeBoard(int row, int col) {
        if (row == board.length) return;
        if (col == board[0].length) {
            InitializeBoard(row + 1, 0);
        } else {
            board[row][col] = ' ';
            InitializeBoard(row, col + 1);
        }
    }

    public static void printBoard(int row, int col) {
        if (row == 0 && col == 0) {
            System.out.println("\n  Connect Four");
            System.out.println("  1   2   3   4   5   6   7");
            System.out.println("┌───┬───┬───┬───┬───┬───┬───┐");
        }
        if (row == board.length) {
            System.out.println("└───┴───┴───┴───┴───┴───┴───┘");
            return;
        }
        if (col == board[0].length) {
            System.out.println("│");
            if (row < board.length - 1) {
                System.out.println("├───┼───┼───┼───┼───┼───┼───┤");
            }
            printBoard(row + 1, 0);
        } else {
            System.out.print("│ " + board[row][col] + " ");
            printBoard(row, col + 1);
        }
    }

    public static boolean placeCharacter(int column, char player, int row) {
        if (row < 0) return false;
        if (board[row][column] == ' ') {
            board[row][column] = player;
            return true;
        }
        return placeCharacter(column, player, row - 1);
    }

    public static boolean checkWin(char player) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= board[0].length - 4; j++) {
                if (checkHorizontal(i, j, player, 0)) return true;
            }
        }
        for (int i = 0; i <= board.length - 4; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == player && board[i + 1][j] == player &&
                    board[i + 2][j] == player && board[i + 3][j] == player) {
                    return true;
                }
            }
        }
        for (int i = 0; i <= board.length - 4; i++) {
            for (int j = 0; j <= board[0].length - 4; j++) {
                if (board[i][j] == player && board[i + 1][j + 1] == player &&
                    board[i + 2][j + 2] == player && board[i + 3][j + 3] == player) {
                    return true;
                }
            }
        }
        for (int i = 0; i <= board.length - 4; i++) {
            for (int j = 3; j < board[0].length; j++) {
                if (board[i][j] == player && board[i + 1][j - 1] == player &&
                    board[i + 2][j - 2] == player && board[i + 3][j - 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkHorizontal(int row, int col, char player, int count) {
        if (col >= board[0].length || board[row][col] != player) return count >= 4;
        return checkHorizontal(row, col + 1, player, count + 1);
    }

    public static boolean isColumnFull(int column) {
        return board[0][column] != ' ';
    }

    public static boolean isBoardFull() {
        for (int j = 0; j < board[0].length; j++) {
            if (board[0][j] == ' ') return false;
        }
        return true;
    }

    public static void playAgain() {
        Scanner input = new Scanner(System.in);
        System.out.println("Do you want to play again? (yes/no)");
        String response = input.nextLine().toLowerCase();
        if (response.equals("yes")) {
            InitializeBoard(0, 0);
            main(null);
        } else if (response.equals("no")) {
            System.out.println("Thanks for playing!");
            System.exit(0);
        } else {
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            playAgain();
        }
    }

    public static void recordMove(int column, char player) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("game_moves.txt", true))) {
            writer.println("Player " + player + " placed a marker in column " + (column + 1));
        } catch (IOException e) {
            System.out.println("Error writing move to file.");
        }
    }

    public static void writeToFile(char player) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("history.txt", true))) {
            writer.println("Congratulations! Player " + player + " Won the Game!");
            writer.println("Final Board State:");
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    writer.print("|" + board[i][j]);
                }
                writer.println("|");
            }
            writer.println();
        } catch (IOException e) {
            System.out.println("Error writing winner to file.");
        }
    }

    public static void loadGameHistory(String filename) throws IOException {
        try (Scanner fileReader = new Scanner(new File(filename))) {
            System.out.println("Previous Game Results:");
            while (fileReader.hasNextLine()) {
                System.out.println(fileReader.nextLine());
            }
        }
    }
}
