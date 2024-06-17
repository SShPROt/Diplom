import model.MainMenuModel;
import presenter.MainMenuPres;
import view.MainMenuView;

public class Main {
    public static void main(String[] args) {
        MainMenuView mainMenuView = new MainMenuView();
        MainMenuModel mainMenuModel = new MainMenuModel();
        new MainMenuPres(mainMenuView, mainMenuModel);
    }
}