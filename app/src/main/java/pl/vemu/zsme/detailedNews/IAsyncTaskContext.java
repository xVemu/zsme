package pl.vemu.zsme.detailedNews;

public interface IAsyncTaskContext {

    void setDetailText(String text);
    void setAuthor(String text);
    void setDate(String text);
    void setProgress(int progress);
    void setProgressVisibility (int progressVisibility);
}
