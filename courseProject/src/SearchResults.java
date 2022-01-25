import java.text.DecimalFormat;

public class SearchResults implements Comparable<SearchResults>, SearchResult{
    private String title;
    private double score;

    public SearchResults(String initTitle, double initScore){
        title = initTitle;
        score = initScore;
    }

    public String toString(){ //I was going to use this to display on the GUI but then divided it up into two ListViews instead
        return title +" with score "+score;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(SearchResults o) {
        DecimalFormat threeDecimals = new DecimalFormat("#.###"); //round to three decimal places
        double scoreRounded = Double.parseDouble(threeDecimals.format(score));
        double score2Rounded = Double.parseDouble(threeDecimals.format(o.getScore()));
        if(scoreRounded<score2Rounded){
            return 1;
        }else if(scoreRounded>score2Rounded){
            return -1;
        }
        return title.compareTo(o.getTitle()); //if there is a tie, sort lexicographically
    }
}
