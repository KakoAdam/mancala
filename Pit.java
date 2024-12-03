import javax.swing.JButton;

public class Pit{
    protected int id;
    protected int marbles = 0; //May also be called seeds or stones
    protected JButton button;

    /* Constructor for when a new game is launched */
    public Pit(){
        System.out.println("Pit constructor NEW GAME");
        marbles = 4; //The number of marbles at the start of the game
    }
    
    /*Constructor for when reading in data from a file */
    public Pit(int value){
        System.out.println("Pit constructor LOAD GAME");
        marbles = value;
    }

    // Updating the button's text to match the number of marbles
    private void updateButtonLabel(){
        button.setText(String.valueOf(marbles));
    }

    // Getters
    public int getMarbles(){ return marbles; }
    public boolean isEmpty(){ return (marbles == 0);}
    public int getId(){ return id; }

    // Setters
    public void setId(int number){ id = number; }
    public void incrementMarbles(){ marbles++; updateButtonLabel();}
    public void decrementMarbles(){ if(marbles > 0){marbles--; updateButtonLabel();}}
    public void setButton(JButton b){
        button = b;
        updateButtonLabel();
    }
    public void setMarbles(int number){
        marbles = number;
        updateButtonLabel();
        
    }
}