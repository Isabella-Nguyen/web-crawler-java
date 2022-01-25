import java.util.*;

public class Search {
    private final HashMap<String, String> allPages;

    public Search(){
        allPages = RetrieveInfo.getMap(); //get all the pages and filenames from RetrieveInfo
    }

    private List<SearchResult> getTopX(TreeSet<SearchResults> results, int X){
        LinkedList<SearchResult> topXResults = new LinkedList<>();
        int count=0;
        for(SearchResults result : results){
            topXResults.add(result);
            count++;
            if(count==X){ //stop adding when X results are added to the list
                break;
            }
        }
        return topXResults;
    }

    public List<SearchResult> mainSearch(String query, boolean boost, int X){
        QueryVector queryVector = new QueryVector(query.toLowerCase(Locale.ROOT)); //non-case sensitive
        queryVector.calcTFIDFQuery(); //Create the query vector

        PageSearch[] allPagesSearches = new PageSearch[allPages.size()];

        int ind=0;
        for (String url : allPages.keySet()){
            PageSearch newPage = new PageSearch(url);
            allPagesSearches[ind] = newPage;
            newPage.findScore(queryVector); //calculate the cosine similarity score
            ind++;
        }

        if(boost){
            for(PageSearch page : allPagesSearches){
                page.useBoost();
            }
        }

        TreeSet<SearchResults> results = new TreeSet<>(); //the TreeSet sorts the results

        for (PageSearch page : allPagesSearches) {
            results.add(new SearchResults(page.getTitle(), page.getScore())); //Create new search results and add them to the TreeSet to be sorted automatically
        }
        return getTopX(results, X);
    }

}
