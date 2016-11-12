package ch.epfl.sweng.jassatepfl;

import android.app.Application;

import ch.epfl.sweng.jassatepfl.injections.Graph;

public class App extends Application {
    private static App sInstance;
    private Graph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        graph = Graph.Initializer.init(false);
    }

    public static App getInstance() {
        return sInstance;
    }

    public Graph graph() {
        return graph;
    }

    public void setMockMode(boolean useMock) {
        graph = Graph.Initializer.init(useMock);
    }
}
