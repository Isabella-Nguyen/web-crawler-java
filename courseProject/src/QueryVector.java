import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryVector {
    private final String query;
    private int totalWords;
    private ArrayList<String> queryWords;
    private final HashMap<String, Word> uniqueWords;
    private double[] queryVector;

    public QueryVector(String initQuery){
        query = initQuery;
        totalWords = 0;
        queryWords = new ArrayList<>();
        uniqueWords = findUniqueWords();
        queryVector = new double[uniqueWords.size()];
    }

    //get/set methods
    public HashMap<String, Word> getUniqueWords(){return  uniqueWords;}
    public double[] getQueryVector(){return queryVector;}

    private HashMap<String, Word> findUniqueWords(){
        String[] words = query.split(" ");
        totalWords=words.length; //save the number of words in the query
        HashMap<String, Word> uniqueWords = new HashMap<>();
        for(String word : words){
            uniqueWords.put(word, new Word(word)); //unique word hash map
            queryWords.add(word); //all words (including duplicates)
        }
        return uniqueWords;
    }

    public void calcTFIDFQuery(){
        Word.calcFreq(queryWords, uniqueWords); //Find all frequencies of the query words
        int ind=0;
        for(String word : uniqueWords.keySet()) {
            Word aWord = uniqueWords.get(word);
            aWord.calcTF(totalWords); //find tf
            aWord.calcTFIDF(Word.getIDF(word)); //find tfidf
            queryVector[ind] = aWord.getTfidf(); //create vector
            ind++;
        }
    }

}
