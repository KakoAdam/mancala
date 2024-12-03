
public class Player{
    protected Pit[] row; //6 (pits) + 1 (store)
    public enum rules{
        ILLEGAL,
        SKIP,
        STEAL,
        NORMAL
    }

    /* Constructor for when a new game is launched */
    public Player(){
        System.out.println("Player constructor NEW GAME");
        row = new Pit[6 + 1];
        for(int i = 0; i < 6; i++){
            row[i] = new Pit();
            row[i].setId(i);
            System.out.println("i: " + i);
        }
        row[6] = new Store();
        row[6].setId(6);
    }

    /*Constructor for when reading in data from a file */
    public Player(String[] pits){
        System.out.println("Player constructor LOAD GAME");
        row = new Pit[7];
        for(int i = 0; i < 6; i++){
            row[i] = new Pit(Integer.parseInt(pits[i]));
            row[i].setId(i);
            System.out.println("i: " + i);
        }
        row[6] = new Store(Integer.parseInt(pits[6]));
        row[6].setId(6);
    }

    // Getters
    public int getPoints(){ return row[6].getMarbles();}
    public Pit getPit(int index){ return row[index]; }
    public Pit getStore(){ return row[6]; }

    // The game ends whenever a side(row) has no more marbles
    // Then the oder side's (player's) marbles are added to to its respective Store
    public int getFinalSum(){
        int points = 0;
        for(Pit p : row){
            points += p.getMarbles();
            p.setMarbles(0); 
        }
        row[6].setMarbles(points);
        return points;
    }


    //Optimalizálni, hogy a kód újrafelhasználható legyen a Machine-nál
    public int distributeMarbles(Player machine, int id){ 

            Player player = this;
            Pit selectedPit = player.getPit(id);
            int marblesLeft = selectedPit.getMarbles();
    
            // If there are no marbles in the Pit it shall not be selected
            if(marblesLeft == 0){
                return -1; // int return meaning the move was illegal
            }

            int currentId = id;
            Player currentPlayer = player;
            Pit currentPit;
        
            while(marblesLeft > 0){
                
                // Switching back and forth between the Player and Machine (IDs go 0 through 6)
                if(currentPlayer == player){
                    
                    // When at the Store we shall switch to the Machine's row
                    if(currentId == 6) {
                        System.out.println("id is 6");
                        if(marblesLeft == 1) System.out.println("1 marble");
                        currentPlayer = machine;
                        currentId = 4;
                    
                    // Checking extra rule no. 1
                    } else if(currentId == 5 && marblesLeft == 1){
                        currentId++;
                        currentPit = currentPlayer.getPit(currentId);
                        selectedPit.decrementMarbles();
                        currentPit.incrementMarbles();
                        marblesLeft--;
                        return 1; // return with 1 indicating thath we will be skipping the opponent's turn

                    // Chechking extra rule no. 2
                    } else if(currentId < 5 && marblesLeft == 1){
                        currentId++;
                        currentPit = currentPlayer.getPit(currentId);

                        // If the last marble was placed on your side in a formerly empty Pit then you Steal
                        if(currentPit.isEmpty()){
                            MancalaGame.updateStatus("You stole some marbles!");
                            selectedPit.decrementMarbles();
                            player.getStore().incrementMarbles();
                            marblesLeft--;

                            Pit stealPit = machine.getPit(currentId);
                            int marblesToSteal = stealPit.getMarbles();
                            while(marblesToSteal > 0){
                                stealPit.decrementMarbles();
                                player.getStore().incrementMarbles();
                                marblesToSteal--;
                            }
                            
                            return 0;
                        }

                        //Inactivate the effect of the previous current++;
                        currentId--;
                    }
                    currentId++;
                }else{
                    
                    // Skipping the opponent's store
                    if(currentId == 0){
                        currentPlayer = player;
                        currentId = 1;
                    }
                    currentId--;
                }
                currentPit = currentPlayer.getPit(currentId);
                selectedPit.decrementMarbles();
                currentPit.incrementMarbles();
                marblesLeft--;

                /*  Makes the distribution smooth
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Thread Error: " + e.getMessage());
                    
                }
                */
            }
        return 0;
    }

    // Returns whether every Pit in the row is empty
    public boolean isRowEmpty(){
        for(int i = 0; i < 6; i++){
            if(!this.getPit(i).isEmpty()) return false;
        }
        return true;
    }
}
