package scoreboard;

import javafx.scene.image.Image;
import java.util.ArrayList;

public class Character {
    private final int index;
    private final String name;
    private final Image icon;

    public Character(int index, String name, Image icon){
        this.index = index;
        this.name = name;
        this.icon = icon;
    }

    public static ArrayList<Character> getCharacters(){
        ArrayList<Character> characters = new ArrayList<Character>();

        String[] characterNames = {"Bowser", "Captain Falcon", "Donkey Kong", "Dr. Mario", "Falco", "Fox", "Ganondorf", "Ice Climbers",
                "Jigglypuff", "Kirby", "Link", "Luigi", "Mario", "Marth", "Mewtwo", "Mr. Game & Watch", "Ness", "Peach",
                "Pichu", "Pikachu", "Roy", "Samus", "Sheik", "Yoshi", "Young Link", "Zelda"};

        for(int i = 0; i < characterNames.length; i++){
            try{
                characters.add(new Character(i, characterNames[i], new Image(String.format("Stock Icons/GUI/%02d %s.png", i, characterNames[i]))));
            }
            catch (Exception e) {
                System.out.println("Error finding icon for: " + characterNames[i]);
            }
        }

        return characters;
    }

    public int getIndex(){
        return index;
    }

    public String getName(){
        return name;
    }

    public Image getIcon(){
        return icon;
    }

    public String getIconPath(String location){
        return String.format("/Stock Icons/%s/%02d %s.png", location, index, name);
    }

    public String toString(){
        return name;
    }
}
