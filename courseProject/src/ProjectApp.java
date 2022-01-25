import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.LinkedList;

public class ProjectApp extends Application {
    private Search model;
    private ProjectView view;

    public ProjectApp(){
        model = new Search();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane aPane = new Pane();

        // Create the view
        view = new ProjectView(model);
        aPane.getChildren().add(view);

        view.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleSearchButton();
            }
        });
        view.getQueryField().setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                view.update();
            }
        });

        view.getBoostButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                view.update();
            }
        });

        stage.setTitle("Isabella's Web Crawler");
        stage.setResizable(false);
        stage.setScene(new Scene(aPane));
        stage.show();

        view.update();

    }

    public void handleSearchButton(){
        boolean boost = view.getBoostButton().isSelected();
        String query = view.getQueryField().getText();

        LinkedList<SearchResult> resultList = new LinkedList<>(model.mainSearch(query, boost, 10)); //get the results from the model
        LinkedList<String> titles = new LinkedList<>();
        LinkedList<Double> scores = new LinkedList<>();
        for(SearchResult result : resultList){
            titles.add(result.getTitle()); //make two lists for both ListViews (I thought it would look cleaner with two ListViews instead of one)
            scores.add(result.getScore());
        }
        view.updateLists(FXCollections.observableArrayList(titles), FXCollections.observableArrayList(scores));
        view.update();
    }

}
