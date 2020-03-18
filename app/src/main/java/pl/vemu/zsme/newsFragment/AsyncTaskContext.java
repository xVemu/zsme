package pl.vemu.zsme.newsFragment;

public interface AsyncTaskContext {
    void setIsFound(boolean isFound);

    void startRefreshing();
}
