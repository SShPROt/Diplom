import model.StartPageModel;
import presenter.StartPagePres;
import view.StartPageView;

public class Main {
    public static void main(String[] args) {
        StartPageView startPageView = new StartPageView();
        StartPageModel startPageModel = new StartPageModel();
        StartPagePres startPagePres = new StartPagePres(startPageView, startPageModel);
    }
}