import java.io.File;
import java.util.List;

public class ProjectTesterImp implements ProjectTester{
    @Override
    public void initialize() {
        File dataFolder = new File("data");
        if(dataFolder.isDirectory()) { //make sure the folder exists. It's possible no crawl has been done before.
            Crawl.deleteData(dataFolder);
        }
    }

    @Override
    public void crawl(String seedURL) {
        Crawl newCrawl = new Crawl(seedURL);
        newCrawl.mainCrawl();
    }

    @Override
    public List<String> getOutgoingLinks(String url) {
        return Page.getOutgoingLinks(url);
    }

    @Override
    public List<String> getIncomingLinks(String url) {
        return Page.getIncomingLinks(url);
    }

    @Override
    public double getPageRank(String url) {
        return Page.getPageRank(url);
    }

    @Override
    public double getIDF(String word) {
        return Word.getIDF(word);
    }

    @Override
    public double getTF(String url, String word) {
        return Word.getTF(url, word);
    }

    @Override
    public double getTFIDF(String url, String word) {
        return Word.getTFIDF(url, word);
    }

    @Override
    public List<SearchResult> search(String query, boolean boost, int X) {
        Search search = new Search();
        return search.mainSearch(query, boost, X);
    }
}
