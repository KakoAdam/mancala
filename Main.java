import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Main {

    public static JFrame frame;
    public static JPanel mainPanel;
    public static CardLayout cardLayout;
    private static JPanel leaderboardPanel;

    public static void main(String[] args) {
        setupGUI();
    }

    // MAIN MENU
    public static void setupGUI() {

        // Frame
        frame = new JFrame("Mancala");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        leaderboardPanel = new JPanel(cardLayout);

        // Menu Panel
        JPanel menuPanel = new JPanel(new BorderLayout());
        JPanel logoPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();

        // Panel Components
        ImageIcon logo = new ImageIcon("mancala_logov2.jpg");
        JLabel label = new JLabel(logo);
        JButton newGameButton = new JButton("New Game");
        JButton loadGameButton = new JButton("Load Game");
        JButton leaderboardButton = new JButton("Leaderboard");

        // Set Layouts
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Add logo to the logoPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        logoPanel.add(label, gbc);

        // Add buttons to the buttonPanel
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Make visual changes to the buttons
        MancalaGame.setButtonStyle(newGameButton);
        newGameButton.setPreferredSize(new Dimension(220, 30)); 
        MancalaGame.setButtonStyle(loadGameButton);
        loadGameButton.setPreferredSize(new Dimension(220, 30)); 
        MancalaGame.setButtonStyle(leaderboardButton);
        leaderboardButton.setPreferredSize(new Dimension(220, 30)); 


        // Add some padding around buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // space between buttons
        buttonPanel.add(loadGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // space between buttons
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(Box.createVerticalGlue());

        // Add panels to menuPanel
        menuPanel.add(logoPanel, BorderLayout.CENTER);
        menuPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set Background Colors
        logoPanel.setBackground(new Color(60, 120, 200));
        buttonPanel.setBackground(new Color(0, 30, 100));
        

        // Add menuPanel to mainPanel
        mainPanel.add(menuPanel, "Menu");

        // Add actionListeners for the NEW GAME, LOAD GAME and LEADERBOARD buttons
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    startNewGame();
                } catch (FileNotFoundException e) {
                    System.out.println("Could not add ActionListener for NEW GAME button: " + e.getMessage());
                }
            }
        });

        loadGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    loadGame();
                } catch (FileNotFoundException e) {
                    System.out.println("Could not add actionlistener for LOAD GAME button: " + e.getMessage());
                } 
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                    showLeaderboard();
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void startNewGame() throws FileNotFoundException {
        MancalaGame newGame = new MancalaGame();
        newGame.boardSetup();
        mainPanel.add(newGame.getGamePanel(), "Game");
        cardLayout.show(mainPanel, "Game");
    }

    public static void loadGame() throws FileNotFoundException {
        String filePath = selectFile();
        MancalaGame loadedGame = new MancalaGame(filePath);
        loadedGame.boardSetup();
        mainPanel.add(loadedGame.getGamePanel(), "Game");
        cardLayout.show(mainPanel, "Game");
    }
    public static String selectFile(){
        JFileChooser selectFile = new JFileChooser();
        selectFile.setDialogTitle("Select file to open");
        int selection = selectFile.showOpenDialog(null);

        if(selection == JFileChooser.APPROVE_OPTION){
            return selectFile.getSelectedFile().getAbsolutePath();
        }else{
            return null;
        }
    }

    /**
     * 
     */
    public static void showLeaderboard(){
        Leaderboard lb = new Leaderboard("leaderboard.txt");
        leaderboardPanel = new JPanel(new BorderLayout());
        leaderboardPanel.setBackground(new Color(0, 51, 102));
        mainPanel.add(leaderboardPanel, "Leaderboard");
        cardLayout.show(mainPanel, "Leaderboard");

        // Retrieve leaderboard entries and sort them by score in descending order
        List<Map.Entry<String, Integer>> lbSort = new ArrayList<>(lb.getLeaderboard().entrySet());
        lbSort.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Player", "Score"}, 0);

        for(Map.Entry<String, Integer> entry : lbSort){
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        JTable table = new JTable(model);
        table.setBackground(new Color(40, 48, 155));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set table header style
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(0, 51, 102)); // Dark blue background
        tableHeader.setForeground(Color.WHITE); // White text
        tableHeader.setFont(new Font("Arial", Font.BOLD, 16)); // Set header font

        // Customize the scroll pane
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(240, 248, 255)); // Match table background

        // Set borders for aesthetics
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        leaderboardPanel.add(scroll, BorderLayout.CENTER);

        // Customize the return button
        JButton returnButton = new JButton("Back To Menu");
        MancalaGame.setButtonStyle(returnButton);
        leaderboardPanel.add(returnButton, BorderLayout.SOUTH);

        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                cardLayout.show(mainPanel, "Menu");
            }
        });
    }

}