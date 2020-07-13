package pl.vemu.zsme.newsFragment;

public abstract class Queries {

    protected int page;

    Queries(int page) {
        this.page = page;
    }

    abstract String parseUrl();

    void addOnePage() {
        page++;
    }

    static class Author extends Queries {

        private final String author;

        Author(int page, String author) {
            super(page);
            this.author = author;
        }

        @Override
        public String parseUrl() {
            return author + "/page/" + page;
        }

    }

    static class Page extends Queries {

        Page(int page) {
            super(page);
        }

        @Override
        public String parseUrl() {
            return "page/" + page;
        }
    }

    static class Search extends Queries {

        private final String query;

        Search(int page, String query) {
            super(page);
            this.query = query;
        }

        @Override
        public String parseUrl() {
            return "page/" + page + "/?s=" + query;
        }
    }
}
