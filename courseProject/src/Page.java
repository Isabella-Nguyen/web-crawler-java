import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.*;

public class Page {
    private String url;
    private String title;
    private final int fileNum;
    private static int nextFileNum=0;
    private String content;
    private ArrayList<String> words;
    private HashSet<Page> outgoingPages; //Used HashSet instead of LinkedList to search quicker in the PageRank algorithm
    private LinkedList<Page> incomingPages;
    private double pageRank;
    private HashMap<String, Word> uniqueWords;
    private int tries;

    //variables to keep track of data over all pages
    private static HashMap<String, Page> urlToPage = new HashMap<>();
    private static HashMap<String, Word> wordToWord = new HashMap<>();

    public Page(String initURL){
        url=initURL;
        title="";
        fileNum=nextFileNum;
        nextFileNum++; //add a number each time for each page initialized so they're all unique
        content="";
        words = new ArrayList<>();
        outgoingPages = new HashSet<>();
        incomingPages = new LinkedList<Page>();
        uniqueWords = new HashMap<>();
        pageRank=0;
        tries=0;
    }

    //get/set methods
    public String getUrl(){return url;}
    public HashSet<Page> getOutgoingPages(){return outgoingPages;}
    public LinkedList<Page> getIncomingPages(){return incomingPages;}
    public static HashMap<String, Page> getUrlToPage(){return urlToPage;}
    public static HashMap<String, Word> getWordToWord(){return wordToWord;}
    public int getFileNum(){return fileNum;}
    public void setPageRank(double newPageRank){pageRank = newPageRank;}
    public int getTries(){return tries;}

    public String toString(){
        return url;
    }

    public int extractAllHTMLInfo(){ //Method to call in the Crawl class to make it easier to read
        int extractingResult = extractContent();
        if (extractingResult==0) { //if the content was successfully extracted
            extractTitle();
            extractWords();
            extractOutgoingPages();
            addIncomingPage();
        }
        return extractingResult; //return the result of the WebRequester to handle in the crawl
    }

    private int extractContent(){
        try {
            content = WebRequester.readURL(url);
        }catch(MalformedURLException e){
            System.out.println("ERROR: " + url + " is not a proper url. Please try again with a different link.");
            return 1;
        }catch(IOException e){
            tries++; //keep track of how many times this exception has occurred
            if(tries==3){
                System.out.println("ERROR: +" + url + " is not able to be parsed. Please try again later or with a different link.");
            }
            return 2;
        }
        return 0;
    }

    private void extractTitle(){
        int startInd = content.indexOf("<title>")+"<title>".length();
        int endInd = content.indexOf("</title>", startInd);

        title = content.substring(startInd, endInd);
    }

    private void extractWords(){
        int startInd = 0;
        int endInd = 0;
        String pTag = "<p>";
        String endPTag = "</p>";
        String[] wordArr;
        int end=pTag.length()+"\n".length()-1;

        while (startInd!=end){ //In case there is more than one <p> tag
            startInd = content.indexOf(pTag, endInd)+pTag.length()+"\n".length(); //find the location of the p tags
            endInd = content.indexOf(endPTag, startInd)-"\n".length();
            if(startInd!=end){
                wordArr = content.substring(startInd,endInd).split("\n");
                for (String word : wordArr){
                    word = word.toLowerCase(Locale.ROOT); //non-case sensitive
                    if (!wordToWord.containsKey(word)){ //keep track of unique words throughout the whole crawl
                        wordToWord.put(word, new Word(word));
                    }
                    words.add(word); //keep track of all the words on this page
                    uniqueWords.put(word, new Word(word)); //keep track of all unique words for this specific page
                }
            }
        }
    }

    private String getBaseURL() {
        int endInd = url.lastIndexOf("/");
        return url.substring(0,endInd);
    }

    private String convertToAbsURL(String baseURL, String relativeURL) {
        return baseURL + relativeURL;
    }

    private void extractOutgoingPages(){
        int startInd = content.indexOf("href=\".")+"href=\".".length();
        int endInd = 0;
        int end = "href=\".".length()-1;

        while (startInd!=end){
            endInd = content.indexOf("\"", startInd);
            String relativeURL = content.substring(startInd, endInd);
            String absURL = convertToAbsURL(getBaseURL(), relativeURL);
            if (urlToPage.containsKey(absURL)){ //Make sure to add a page object that already exists (if it does) to not make a new empty page object
                outgoingPages.add(urlToPage.get(absURL));
            }
            else{
                Page newPage = new Page(absURL); //Make new page if this page has never been encountered before
                outgoingPages.add(newPage);
                urlToPage.put(absURL, newPage); //update hashmap
            }
            startInd = content.indexOf("href=\".", endInd)+"href=\".".length();
        }
    }

    private void addIncomingPage(){
        for (Page page : outgoingPages){
            page.getIncomingPages().add(this); //add this page to the outgoing pages representing its incoming pages
        }
    }

    public void numDocsWordAppearsIn(){
        for (String word : uniqueWords.keySet()){
            if (wordToWord.containsKey(word)){
                Word curWord = wordToWord.get(word);
                curWord.setNumDocsFoundIn(curWord.getNumDocsFoundIn()+1); //Add one to number of docs the word appears in
            }
        }
    }

    public void calcAllTF(){
        Word.calcFreq(words, uniqueWords); //find all the frequencies first
        int totalWords = words.size();
        for (String word : uniqueWords.keySet()){ //calculate the TF of unique words on the page
            uniqueWords.get(word).calcTF(totalWords);
        }
    }

    public void calcAllTFIDF(){
        for (String word : uniqueWords.keySet()){
            uniqueWords.get(word).calcTFIDF(wordToWord.get(word).getIdf()); //calculate all TF values for this page
        }
    }

    public void saveAllInfo(String mainDirName) throws IOException {
        String dirName = mainDirName + File.separator + fileNum;
        if(new File(dirName).mkdir()) { //make a directory for each page
            //Save title
            PrintWriter titleFile = new PrintWriter(new FileWriter(dirName + File.separator + "title.txt"));
            titleFile.println(title);
            titleFile.close();

            //Save outgoing links
            PrintWriter outgoingFile = new PrintWriter(new FileWriter(dirName + File.separator + "outgoing.txt"));
            for (Page outgoingPage : outgoingPages) {
                outgoingFile.println(outgoingPage);
            }
            outgoingFile.close();

            //Save incoming links
            PrintWriter incomingFile = new PrintWriter(new FileWriter(dirName + File.separator + "incoming.txt"));
            for (Page incomingPage : incomingPages) {
                incomingFile.println(incomingPage);
            }
            incomingFile.close();

            //Save page rank
            PrintWriter pageRankFile = new PrintWriter(new FileWriter(dirName + File.separator + "pagerank.txt"));
            pageRankFile.println(pageRank);
            pageRankFile.close();

            //Make a directory for each unique word object and save the tf and tf-idf values in that directory
            for (String word : uniqueWords.keySet()) {
                String wordPath = dirName + File.separator + word;
                if(new File(wordPath).mkdir()) { //make directory for the word
                    PrintWriter tfFile = new PrintWriter(new FileWriter(wordPath + File.separator + "tf.txt"));
                    tfFile.println(uniqueWords.get(word).getTf());

                    PrintWriter tfidfFile = new PrintWriter(new FileWriter(wordPath + File.separator + "tfidf.txt"));
                    tfidfFile.println(uniqueWords.get(word).getTfidf());

                    tfFile.close();
                    tfidfFile.close();
                }else{
                    System.out.println("ERROR: not able to make a directory for "+word+" in page directory "+fileNum);
                }
            }
        }else{
            System.out.println("ERROR: not able to make a directory for the page with URL "+url);
        }
    }

    //All loading methods for pages
    public static double getPageRank(String url){
        String path = RetrieveInfo.getMap().get(url) + File.separator + "pagerank.txt";
        return RetrieveInfo.getDoubleFromFile(path, true);
    }

    public static String getTitle(String url){
        return RetrieveInfo.getTitle(url);
    }

    public static List<String> getOutgoingLinks(String url) {
        return RetrieveInfo.getListOfLinks(url, "outgoing.txt");
    }

    public static List<String> getIncomingLinks(String url) {
        return RetrieveInfo.getListOfLinks(url, "incoming.txt");
    }

}
