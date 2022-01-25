import java.io.*;
import java.util.*;

public class Crawl {
    private String seedURL;
    private LinkedList<Page> queue;
    private ArrayList<Page> allPages;
    private HashSet<String> allURLs;
    private int totalPages;
    private HashSet<Word> allWordsFound;

    public Crawl(String initSeedURL){
        seedURL=initSeedURL;
        queue = new LinkedList<Page>();
        allPages = new ArrayList<Page>();
        allURLs = new HashSet<>();
        totalPages = 0;
        allWordsFound = new HashSet<Word>();
    }

    private Page dequeue(LinkedList<Page> aQueue){
        return aQueue.remove(0);
    }

    private void addToQueue(HashSet<Page> listOfPages, LinkedList<Page> aQueue){
        for(Page page : listOfPages){
            if (!allURLs.contains(page.getUrl())){
                aQueue.add(page);
                allURLs.add(page.getUrl());
            }
        }
    }

    private void addToAllWordsFound(HashMap<String, Word> words){
        for (String word : words.keySet()){
            allWordsFound.add(words.get(word));
        }
    }

    public static void deleteData(File file){ //static because it's not specific to any crawl. It just deletes all necessary files.
        File[] allFiles = file.listFiles();
        for (File subFile : allFiles) {
            if (subFile.isDirectory()) {
                deleteData(subFile); //Delete all files inside the directory first
            } else {
                subFile.delete(); //delete the file if it is not a directory
            }
        }
        file.delete(); //delete the directory once all the files inside are deleted
    }

    private void saveMapping(String dirName) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        for(Page page :allPages){ //Make a HashMap for the urls and file numbers so we know which file to look in when given the url
            map.put(page.getUrl(), String.valueOf(page.getFileNum()));
        }
        PrintWriter mapFile = new PrintWriter(new FileWriter(dirName + File.separator + "mapping.txt"));
        Properties properties = new Properties();
        properties.putAll(map);
        properties.store(mapFile, null); //Store the HashMap into a file
        mapFile.close();
    }

    private void saveInfo() throws IOException {
        String root = "data";
        if (new File(root).mkdir()) { //make a folder that contains all the data so it's easy to delete everything later
            for (Page page : allPages) {
                page.saveAllInfo(root); //save all page information
            }

            String dirName = root + File.separator + "idf";
            if(new File(dirName).mkdir()) { //make a directory to contain the idf values
                for (Word word : allWordsFound) {
                    word.saveIDF(dirName); //Save all idf values for each word
                }
            }else{
                System.out.println("ERROR: idf directory cannot be made");
            }

            saveMapping(root); //Save the HashMap with urls and filenames
        }else{
            System.out.println("ERROR: root directory \"data\" cannot be made");
        }
    }

    private void initializeCrawl(){
        Page seedPage = new Page (seedURL);
        queue.add(seedPage);
        allURLs.add(seedPage.getUrl());
        Page.getUrlToPage().put(seedURL, seedPage);
    }

    public void mainCrawl(){
        initializeCrawl();
        while (queue.size()>0){
            Page nextPage = dequeue(queue);
            //Make sure the page was extracted without fail
            int extractingResult = nextPage.extractAllHTMLInfo(); //includes title, words, outgoing links, total words, incoming links
            if (extractingResult==0) { //the page was properly extracted
                totalPages++;
                allPages.add(nextPage);
                nextPage.numDocsWordAppearsIn(); //record how many times a word appears in a document
                addToQueue(nextPage.getOutgoingPages(), queue); //Add all the pages that were not read into the queue
            }else if (extractingResult==2 && nextPage.getTries()<3){ //Put a limit to how many times this can occur to avoid infinite looping
                queue.add(nextPage); //add to the end of the queue if something went wrong to try again later
                allURLs.add(nextPage.getUrl()); //make sure the url isn't added to queue again
            }
            /*
            Note: If the IOException has occurred three times already, there must be something wrong. Do not parse the page.
            The exception will inform the user that something is wrong and ask them to try again later or use another link.
            Note: if the URL is malformed (extractingResult==1), then do nothing. Nothing will change if we add it back to the queue.
            The malformed URL Exception (in the Page class) will inform the console of the exception so the user can try again
            with another link if they wish.
             */
        }
        addToAllWordsFound((Page.getWordToWord())); //Create a set of unique words

        for(Page page : allPages) {
            page.calcAllTF(); //Calculate all the tf values for each word in each page and save it in each object
        }

        for (Word word : allWordsFound){
            word.calcIDF(totalPages); //Calculate the idf for each word found
        }

        double[][] pageRankVector = CalcMath.pageRankVector(allPages, totalPages); //Get the page rank vector for all the pages

        for (Page page : allPages){
            page.calcAllTFIDF(); //Calculate all tf-idfs after all tf and idfs are calculated for each word in each page
            page.setPageRank(pageRankVector[0][page.getFileNum()]); //record the page rank for each page
        }

        try {
            saveInfo(); //save all the info found during the crawl
        } catch (IOException e) {
            System.out.println("ERROR: cannot save data to files.");
        }

    }

}
