package pl.vemu.zsme.detailedNews;

public interface IAsyncTaskContext {

    default void setDetailText(String text) { }
    void setProgress(int progress);
    void setProgressVisibility (int progressVisibility);
}
