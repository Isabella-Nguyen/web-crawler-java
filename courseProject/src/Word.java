import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

public class Word {
    private String word;
    private double tf;
    private double idf;
    private double tfidf;
    private int freq;
    private int numDocsFoundIn;

    public Word(String word){
        this.word= word;
        freq=0;
        tf=0;
        idf=0;
        tfidf=0;
        numDocsFoundIn=0;
    }

    //get/set methods
    public double getTf() {
        return tf;
    }
    public void setNumDocsFoundIn(int newVal){numDocsFoundIn=newVal;}
    public int getNumDocsFoundIn(){return numDocsFoundIn;}
    public void setIdf(double newIdf){idf=newIdf;}
    public double getIdf(){return idf;}
    public double getTfidf() {
        return tfidf;
    }
    public void setFreq(int newFreq){freq=newFreq;}
    public int getFreq(){return freq;}

    public String toString(){
        return word;
    }

    public void calcTF(int totalWords){
        if(totalWords==0){ //in case of division by 0
            tf=0.0;
        }else {
            tf = (double) freq / totalWords;
        }
    }

    public void calcTFIDF(double getIdf){
        tfidf=(Math.log((double)1+tf)/Math.log(2))*getIdf;
    }

    public void calcIDF(int totalDocs){
        idf = Math.log((double)totalDocs/(1+numDocsFoundIn))/Math.log(2);
    }

    public void saveIDF(String dirName) throws IOException {
        PrintWriter idfFile = new PrintWriter(new FileWriter(dirName + File.separator + word+".txt"));
        idfFile.println(idf); //save the idf to the file
        idfFile.close();
    }

    //Static because it is not specific to any instance
    public static void calcFreq(List<String> wordList, HashMap<String, Word> uniqueWords){ //Calculate the frequency of the words in a list and save it in a Word instance
        for (String word : wordList){
            Word aWord = uniqueWords.get(word);
            aWord.setFreq(aWord.getFreq()+1);
        }
    }

    //All loading methods for a word
    public static double getTFIDF(String url, String word){
        String path = RetrieveInfo.getMap().get(url) + File.separator + word + File.separator + "tfidf.txt";
        return RetrieveInfo.getDoubleFromFile(path, false);
    }

    public static double getIDF(String word){
        String path = "idf" + File.separator + word+".txt";
        return RetrieveInfo.getDoubleFromFile(path, false);
    }

    public static double getTF(String url, String word) {
        String path = RetrieveInfo.getMap().get(url) + File.separator + word + File.separator + "tf.txt";
        return RetrieveInfo.getDoubleFromFile(path, false);
    }

}
