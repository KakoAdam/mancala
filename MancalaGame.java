import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class MancalaGame {

    //GUI of board (frontend)
    private JPanel gamePanel;
    private JPanel mancalaBoard;
    private GridBagLayout table;
    private GridBagConstraints gbc;
    private static JLabel turnLabel;
    private JButton returnButton;
    private JButton saveButton;
    private static Font font = new Font("Arial", Font.BOLD, 24);

    //Logic of board (backend)
    Player p;
    Machine m;
    boolean isButtonPressable = false;
    Leaderboard leaderboard;

    // Here we can change the look of our buttons
    public static void setButtonStyle(JButton button){
        Font buttonFont = font; // Can be different from all other texts
        Dimension buttonSize = new Dimension(80, 80);
        button.setFont(buttonFont);
        button.setBackground(new Color(60, 120, 200));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(buttonSize);
    }

    // Sets upt the visuals of the board as well as the 
    public void boardSetup(){

        // Setting up the visuals of the board
        gamePanel = new JPanel();
        gamePanel.setBackground(new Color(0, 30, 100));
        gamePanel.setLayout(new BorderLayout());

        table = new GridBagLayout();
        gbc = new GridBagConstraints();
        mancalaBoard = new JPanel(table);
        mancalaBoard.setBackground(new Color(30, 60, 80));
        
        gbc.fill = GridBagConstraints.BOTH;
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(20, 40, 0));
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add a saveGame button to save current progress
        saveButton = new JButton("Save Game");
        setButtonStyle(saveButton);
        saveButton.setPreferredSize(new Dimension(220, 40));
        buttonPanel.add(saveButton); 
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                if(isButtonPressable) saveGame();
            }
        });

        // Return to Main Menu
        returnButton = new JButton("Back to Menu");
        setButtonStyle(returnButton);
        returnButton.setPreferredSize(new Dimension(220, 40)); // Set preferred size
        buttonPanel.add(returnButton);
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                Main.cardLayout.show(Main.mainPanel, "Menu");
            }
        });

        // This label will tell the Player the status of the game (who's turn it is, has the game ended, etc.)
        turnLabel = new JLabel("It's your turn. Pick a pit by clicking on it!");
        turnLabel.setFont(font);
        turnLabel.setHorizontalAlignment(JLabel.CENTER);
        turnLabel.setForeground(new Color(60, 120, 200));
        gamePanel.add(turnLabel, BorderLayout.NORTH);


        // Setting up the visuals of the Machine's row of pits
        for(int x = 0; x < 6; x++){
            JButton button = new JButton(p.getPit(x).getMarbles() + "");
            setButtonStyle(button);
            gbc.gridx = x + 1;
            gbc.gridy = 0;
            mancalaBoard.add(button, gbc);
            m.getPit(x).setButton(button);
        }    

        // Setting up the visuals of the Player's row of pits
        for(int x = 0; x < 6; x++){
            JButton button = new JButton(p.getPit(x).getMarbles() + "");
            setButtonStyle(button);
            gbc.gridx = x + 1;
            gbc.gridy = 1;
            mancalaBoard.add(button, gbc);
            p.getPit(x).setButton(button);
            addPitButtonListener(p, m, x, button); //Only the Player's Pits excluding the Store can be pressed
        }    

        // Setting up the Machine's store
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JButton mStoreButton = new JButton(m.getStore().getMarbles() + "");
        setButtonStyle(mStoreButton);
        mancalaBoard.add(mStoreButton, gbc);
        m.getStore().setButton(mStoreButton);

        // Setting up the Player's store
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JButton pStoreButton = new JButton(p.getStore().getMarbles() + "");
        setButtonStyle(pStoreButton);
        mancalaBoard.add(pStoreButton, gbc);
        p.getStore().setButton(pStoreButton);

        // The gamePanel is ready
        gamePanel.add(mancalaBoard, BorderLayout.CENTER);
        
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }

    // Constructor for NEW GAME
    public MancalaGame() {
        p = new Player();
        m = new Machine();
        isButtonPressable = true;
        leaderboard = new Leaderboard("leaderboard.txt");
    }
    
    // Constructor for LOAD GAME
    public MancalaGame(String file) {

        // File reading
        File inputData = new File(file);
        try{
            Scanner reader = new Scanner(inputData);

            //First line is the Player's pits (IDs 0->6)
            if(!reader.hasNextLine()) {
                reader.close();
                System.out.println("A következő helyen nem olvasható fájl: "+file);
                return;
            }
            String row = reader.nextLine();
            String[] pitValues = row.split(",");

            p = new Player(pitValues);

            //Second line is the Machine's pits (IDs 0->6)
            if(!reader.hasNextLine()) {
                reader.close();
                System.out.println("A következő helyen nem megfelelő formátumú a fájl: "+file);
                return;
            }
            row = reader.nextLine();
            pitValues = row.split(",");

            m = new Machine(pitValues);

            reader.close();
            isButtonPressable = true;
            leaderboard = new Leaderboard("leaderboard.txt");

        } catch (FileNotFoundException e) {
            System.out.println("A következő helyen nincs ilyen fájl: "+file);
            e.printStackTrace();
        }
    }

    //Save the current state of the game 
    private void saveGame(){

        // Creating the output string
        Player current = p;
        String output = "";
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 7; j++){
                output += Integer.toString(current.getPit(j).getMarbles());
                if(j < 6) output += ",";
            }
            output += "\n";
            current = m;
        }

        // The file's name will be the current date and time
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd_hh-mm");
        String fileName = "saved_game_" + date.format(formatter) + ".txt";
        
        try {
            FileWriter fw = new FileWriter(fileName);
            PrintWriter pw = new PrintWriter(fw);
            
            pw.println(output);
            pw.close();
            updateStatus("Game is saved!");

        } catch (Exception e) {
            updateStatus("Game could not be saved due to exception!");
        }
    }

    // Logic for handling pit selection (ActionListener)
    private void addPitButtonListener(Player player, Machine machine, int id, JButton button){
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                new Thread(() ->{
                    if(isButtonPressable) pitButtonPressHandle(player, machine, id);
                }).start();;
            }
        });
    }

    // Logic for handling pit selection (PressHandler)
    private void pitButtonPressHandle(Player player, Machine machine, int id){

        int rules = player.distributeMarbles(machine, id);
        
        // Checking whether the player's move was legal (Selected pit needs to have marbles in it)
        if(rules == -1){
            updateStatus("You need to select a pit with marbles in it.");
            return;
        // Checking the extra rule no. 1 (if the last placed marble goes to your Store then you shall go again skipping the AI)
        }else if(rules == 1){
            updateStatus("Your last marble landed in the Store. Go again!");
            return;
        }

        // Checking whether it's the end of game (all empty pits on one side)
        if(player.isRowEmpty()){

            // This function changes the status label (first param: mPoints; second param: pPoints)  
            decideWinner(machine.getFinalSum(), player.getStore().getMarbles());
            
            // Call endOfGame funtion
            endOfGame();
            return;
        }
        isButtonPressable = false;
        updateStatus("Your opponent is deciding on its move!");

        // Try-catch for Thread.sleep - this makes the moves look smooth for the player
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Thread Error: " + e.getMessage());
            e.printStackTrace();
        }

        while((rules = machine.move(player)) == 1){
            if(rules == -1){
                decideWinner(machine.getStore().getMarbles(), player.getFinalSum());
            
                // Call endOfGame funtion
                endOfGame();
                return;

            } 
            // Checking the extra rule no. 1 (if the last placed marble goes to your Store then you shall go again skipping the AI)
            updateStatus("The AI's last marble landed in their Store. You got skipped!");
            // Try-catch for Thread.sleep - this makes the moves look smooth for the player
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Checking whether it's the end of game (all empty pits on one side)
        if(machine.isRowEmpty()){

            // This function changes the status label (first param: mPoints; second param: pPoints)  
            /* machine.getStore().getMarbles could be used (this time only) interchangeably with
            * machine.getFinalSum() yet the latter has a for cycle so it's slower
            * Same goes for the player when checking player.isRowEmpty() up above
            */
            decideWinner(machine.getStore().getMarbles(), player.getFinalSum());
            
            // Call endOfGame funtion
            endOfGame();
            return;
        }
        isButtonPressable = true;
        updateStatus("It's your turn. Pick a pit by clicking on it!");
    }

    // To be implemented
    private void endOfGame(){
        isButtonPressable = false;
        String name = getPlayerName();
        int score = p.getStore().getMarbles();
        leaderboard.addToLeaderboard(name, score);
    }

    // Checks the points given then decides the winner
    private void decideWinner(int mPoints, int pPoints){
            if(mPoints == pPoints){
                updateStatus("It's a draw. Good game!");
            }else if(mPoints > pPoints){
                updateStatus("You lost. Better luck next time!");
            }else{
                updateStatus("You won. Gongratulations!");
            }
    }

    // Update the message of the status label/turn label
    public static void updateStatus(String message){
        SwingUtilities.invokeLater(() -> {
            turnLabel.setText(message);
        });
    }

    // Pop-up window for the user to add their score to the leaderboard
    private String getPlayerName(){
        return JOptionPane.showInputDialog(null, "Enter your name:", "Player Name", JOptionPane.QUESTION_MESSAGE);
    }
}