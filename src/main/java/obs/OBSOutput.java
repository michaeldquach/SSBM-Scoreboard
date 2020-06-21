package obs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class OBSOutput {
    private static final String[] fileNames = {"P1 Tag", "P2 Tag", "P1 Score", "P2 Score", "P1 Port", "P2 Port", "P1 Char", "P2 Char", "Bracket Round", "Tournament Name", "Commentator Tag"};

    public static boolean initialize(){
        try{
            for(String fileName:fileNames){
                File temp = new File(String.format("OBS Output/%s.txt", fileName));
                temp.getParentFile().mkdirs();
                temp.createNewFile();
            }
            return true;
        }
        catch (IOException e) {
            System.out.println("Error initializing output files!");
            e.printStackTrace();
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
                //System.out.println("content is null");        //debug
            }

            aFile.close();
            return true;
        }
        catch (IOException e) {
            System.out.println(String.format("Couldn't write to file: %s.txt.", fileName));
            e.printStackTrace();
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

            //System.out.println("Copying " + source + " to " + target);      //debug

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            System.out.println(String.format("Couldn't write to file: %s.png.", fileNameOutput));
            e.printStackTrace();
        }
        return false;
    }
}
