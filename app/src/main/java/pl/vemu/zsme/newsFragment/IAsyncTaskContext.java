package pl.vemu.zsme.newsFragment;

public interface IAsyncTaskContext {
    void setIsFound(boolean isFound);
    void startRefreshing();
}
