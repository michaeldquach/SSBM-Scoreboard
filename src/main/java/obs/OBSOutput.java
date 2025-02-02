package obs;

import scoreboard.ConsolePane;
import scoreboard.ScoreboardModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class OBSOutput {
    private static final String[] fileNames = {"P1 Tag", "P2 Tag", "P1 Score", "P2 Score", "P1 Port", "P2 Port", "P1 Char", "P2 Char", "Bracket Round", "Tournament Name", "Commentator Tag"};
    private static final String[] imageFileNames = {"P1 Character Icon", "P1 Port Image", "P2 Character Icon", "P2 Port Image"};

    //also initialize image files
    public static boolean initialize(){
        try{
            for(String fileName:fileNames){
                File temp = new File(String.format("OBS Output/%s.txt", fileName));
                if(!temp.exists()){
                    temp.getParentFile().mkdirs();
                    temp.createNewFile();
                }
            }
            for(String fileName:imageFileNames){
                File temp = new File(String.format("OBS Output/%s.png", fileName));
                if(!temp.exists()){
                    temp.getParentFile().mkdirs();
                    temp.createNewFile();
                }
            }
            return true;
        }
        catch (IOException e) {
            ConsolePane.outputText("Error initializing output files!");
            //e.printStackTrace();
        }
        return false;
    }

    public static boolean writeOutputText(String fileName, String fileContent){
        PrintWriter aFile;
        try{
            String dir = String.format("OBS Output/%s.txt", fileName);
            aFile = new PrintWriter(new FileWriter(dir));       //Redundantly creates text output files. Also overwrites those files on each save.

            if(fileContent != null){
                aFile.println(fileContent);
            }
            else{
                aFile.println();

                if(ScoreboardModel.DEBUG){System.out.println("Content is null.");}        //debug
            }

            aFile.close();
            return true;
        }
        catch (IOException e) {
            ConsolePane.outputText(String.format("Couldn't write to file: %s.txt.", fileName));
            //e.printStackTrace();
        }
        return false;
    }

    //Secretly, we're copying an image from /resources and replacing it in /output. Secretly.
    public static boolean writeOutputImage(String fileNameSource, String fileNameOutput){
        try{
            InputStream source;

            if(fileNameSource != null){
                source = OBSOutput.class.getResourceAsStream(fileNameSource);
            }
            else{
                source = OBSOutput.class.getResourceAsStream("/Images/OBS/empty.png");
            }

            Path target = Paths.get(String.format("OBS Output/%s.png", fileNameOutput));

            if(ScoreboardModel.DEBUG){System.out.println("Copying " + source + " to " + target);}       //debug

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            ConsolePane.outputText(String.format("Couldn't write to file: %s.png.", fileNameOutput));
            //e.printStackTrace();
        }
        return false;
    }
}
