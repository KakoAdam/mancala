import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Leaderboard{
    private Map<String, Integer> leaderboard;
    private String fileName;

    public Leaderboard(String fileName){
        this.fileName = fileName;
        leaderboard = new TreeMap<>();
        loadLeaderboard();
    }

    public void loadLeaderboard(){
        File lb = new File(fileName);
        try {
            Scanner reader = new Scanner(lb);
            while(reader.hasNextLine()){
                String row = reader.nextLine();
                String[] parts = row.split(",");
                if(parts.length == 2){
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    leaderboard.put(name, score);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error in leaderboard file: " + e.getMessage());
        }
    }

    public void saveLeaderboard(){
        try {
            PrintWriter pw = new PrintWriter(fileName);
            for(Map.Entry<String, Integer> entry : leaderboard.entrySet()){
                pw.println(entry.getKey() + "," + entry.getValue());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error writing leaderboard file: " + e.getMessage());
        }
        
    }

    public void addToLeaderboard(String name, int points){
        if (leaderboard.containsKey(name)) {
            if (leaderboard.get(name) < points) {
                leaderboard.put(name, points); // Update only if new score is higher
            }
        } else {
            leaderboard.put(name, points); // Add new player
        }
        saveLeaderboard();
    }


    public Map<String, Integer> getLeaderboard(){
        return new TreeMap<String, Integer>(leaderboard);
    }
}