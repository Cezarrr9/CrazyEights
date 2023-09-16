package game;

import game.model.CrazyEights;
import game.view.CrazyEightsView;
public class Main {
    public static void main(String[] args) {
        CrazyEights model = new CrazyEights();
        new CrazyEightsView(model);
        model.start();
    }
}
