import java.io.*;
import java.util.*;
import java.util.List;

public final class RetrieveInfo {
    private static final HashMap<String, String> mapping = getMapping();
    public static HashMap<String, String> getMap(){return mapping;}
    private static HashMap<String, String> getMapping() {
        HashMap<String, String> map = new HashMap<>();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("data" + File.separator + "mapping.txt")); //extract the map from the file
        } catch (IOException e) {
            System.out.println("ERROR: Mapping file is not able to be read.");
        }
        for(String url : properties.stringPropertyNames()){
            map.put(url, properties.get(url).toString()); //add everything to the map
        }
        return map;
    }

    public static List<String> getListOfLinks(String url, String filename){
        String path = "data" + File.separator + mapping.get(url)+ File.separator + filename;
        Scanner in;
        try {
            in = new Scanner(new FileReader(path));
        } catch (FileNotFoundException e) {
            return null; //if the file is not found, return null
        }
        List<String> listOfLinks = new LinkedList<>();
        while(in.hasNext()){
            listOfLinks.add(in.nextLine()); //add the contents of the file into the list
        }
        in.close();
        return listOfLinks;
    }

    public static double getDoubleFromFile(String path, boolean pageRank) {
        String fullPath = "data" + File.separator + path;
        Scanner in;
        try {
            in = new Scanner((new FileReader(fullPath)));
        } catch (FileNotFoundException e) {
            if(pageRank) {
                return -1; //If the file is not found, return -1 for page rank
            }
            return 0; //all the other ones should return 0 if not found
        }
        double data = in.nextDouble();
        in.close();
        return data;
    }

    public static String getTitle(String url){
        String filename = mapping.get(url);
        String path = "data" + File.separator + filename + File.separator + "title.txt";
        Scanner in;
        try {
            in = new Scanner(((new FileReader(path))));
        } catch (FileNotFoundException e) {
            return null; //return null if the url is not found
        }
        String title = in.nextLine();
        in.close();
        return title;
    }

}
