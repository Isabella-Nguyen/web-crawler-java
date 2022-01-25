import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ProjectView extends Pane{
    private Search model;
    private Text title;
    private TextField queryField;
    private Button searchButton;
    private ToggleButton boostButton;
    private Text titleListText;
    private ListView<String> titleList;
    private Text scoreListText;
    private ListView<Double> scoreList;

    public ProjectView(Search initModel){
        model=initModel;

        title = new Text("Isabella's Web Crawler!");
        title.relocate(140, 20);

        queryField = new TextField();
        queryField.relocate(44, 44);
        queryField.setPrefSize(325, 20);

        searchButton = new Button("Search");
        searchButton.relocate(380, 44);
        searchButton.setPrefSize(90, 20);

        boostButton = new ToggleButton("page rank boost OFF");
        boostButton.relocate(120, 77);
        boostButton.setPrefSize(170,30);

        titleListText = new Text("Title");
        titleListText.relocate(130, 130);

        titleList = new ListView<String>();
        titleList.relocate(44, 150);
        titleList.setPrefSize(200, 242);

        scoreListText = new Text("Score");
        scoreListText.relocate(350, 130);

        scoreList = new ListView<Double>();
        scoreList.relocate(266, 150);
        scoreList.setPrefSize(196, 242);

        getChildren().addAll(title, queryField, searchButton, boostButton, titleListText, titleList, scoreList, scoreListText);

        setPrefSize(506, 440);
    }

    //get/set methods
    public Button getSearchButton(){return searchButton;}
    public ToggleButton getBoostButton(){return boostButton;}
    public TextField getQueryField(){return queryField;}

    public void update(){
        searchButton.setDisable(queryField.getText().length() == 0);

        if(boostButton.isSelected()){
            boostButton.setText("page rank boost ON");
        }else{
            boostButton.setText("page rank boost OFF");
        }
    }

    public void updateLists(ObservableList<String> titleResults, ObservableList<Double> scoreResults){
        titleList.setItems(titleResults);
        scoreList.setItems(scoreResults);
    }

}
