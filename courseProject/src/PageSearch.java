import java.io.File;
import java.util.HashMap;

public class PageSearch {
    private String url;
    private double score;

    public PageSearch(String initUrl){
        url=initUrl;
        score = 0;
    }

    public double getScore(){return score;}
    public String getTitle(){return Page.getTitle(url);}

    private double[] findPageVector(QueryVector queryVector){
        HashMap<String, Word> uniqueWords = queryVector.getUniqueWords();
        double[] pageVector = new double[uniqueWords.size()];

        int wordInd = 0;
        for(String word : uniqueWords.keySet()) {
            pageVector[wordInd] = Word.getTFIDF(url, word); //get the TF-IDF values for the words in the query for this page
            wordInd++;
        }
        return pageVector;
    }

    public void findScore(QueryVector queryVector){
        score = CalcMath.calcCosineSimilarity(findPageVector(queryVector), queryVector.getQueryVector());
    }

    public void useBoost(){
        score*=Page.getPageRank(url);
    }

}
