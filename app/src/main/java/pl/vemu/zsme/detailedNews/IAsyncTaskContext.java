package pl.vemu.zsme.detailedNews;

public interface IAsyncTaskContext {

    void setDetailText(String text);
    void setProgress(int progress);
    void setProgressVisibility (int progressVisibility);
}
