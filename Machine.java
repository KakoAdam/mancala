import java.util.Random;

public class Machine extends Player{
    Random rnd;

    // The Machine's moves are randomized. This function gives a random ID of a pit that's not empty.
    public int giveRandomPitId(){
        int numOfSelectablePits = 0;
        rnd = new Random();

        // Go through each Pit that is the Machine's and see how many are available 
        for(int i = 0; i < 6; i++){
            if(!this.getPit(i).isEmpty()){
                numOfSelectablePits++;
                System.out.println("Number of selectable pits: " + numOfSelectablePits);
            }
        }

        // Check if all pits are empty in the Machine's row because it would mean the end of the game
        if(numOfSelectablePits == 0){
            System.out.println(this.getFinalSum());
            return -1;
        }

        int randomId = rnd.nextInt(numOfSelectablePits);

        // If the Pit with the randomly generated Id does not have marbles, then we select its mate etc. 
        while(this.getPit(randomId).isEmpty()){
            randomId++;
            if(randomId == 6) randomId = 0;
        }

        return randomId;

    }

    // This executes the Machine's move right after the player has moved
    public int move(Player player){
        int selectedPitId = giveRandomPitId();
        System.out.println("Selected Pit's ID: " + selectedPitId);
        return this.distributeMarbles(player, selectedPitId);
    }

    public int distributeMarbles(Player player, int id){
        Machine machine = this;
        Player current = machine;
        int currentId = id;
        Pit selectedPit = current.getPit(currentId);
        int marblesLeft = selectedPit.getMarbles();

        // We distribute all marbles that are in the selected Pit
        while(marblesLeft > 0){

            // Alternating between the Machine's row and the Player's row
            if(current == machine){
                
                // Checking if the store is supposed to come, for its id is 6
                if(currentId == 0){
                    selectedPit.decrementMarbles();
                    machine.getStore().incrementMarbles();
                    marblesLeft--;

                    // Checking extra rule no.1
                    // If the last marble was placed in the Machine's Store then the AI will go once again skipping the Player
                    if(marblesLeft == 0 && !machine.isRowEmpty()){
                        return 1;
                    }
                    current = player;
                    currentId = -1;

                // Iterating through the Machine's row of pits from right to left(5 to 0)
                }else{
                    currentId--;
                    selectedPit.decrementMarbles();

                    if(marblesLeft == 1 && machine.getPit(currentId).isEmpty()){
                        MancalaGame.updateStatus("The AI stole your marbles!");
                        machine.getStore().incrementMarbles();
                        marblesLeft--;

                        Pit stealPit = player.getPit(currentId);
                        int marblesToSteal = stealPit.getMarbles();

                            while(marblesToSteal > 0){
                                stealPit.decrementMarbles();
                                machine.getStore().incrementMarbles();
                                marblesToSteal--;
                            }

                        return 0;
                    }

                    machine.getPit(currentId).incrementMarbles();
                    marblesLeft--;

                    
                }
            }else{

                // We skip the Store of the Machine's opponent (Player's Store) as we ought to do
                if(currentId == 6){
                    current = machine;
                    
                // Iterating through the Player's row of Pits from left to right (0 to 5)
                }else{
                    selectedPit.decrementMarbles();
                    currentId++;
                    player.getPit(currentId).incrementMarbles();
                    marblesLeft--;   
                }
            }
        }

    return 0;
    }

    public Machine(){ super(); }
    public Machine(String[] pits){ super(pits);}
}