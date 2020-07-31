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

        Author(String author) {
            super(1);
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

        Search(String query) {
            super(1);
            this.query = query;
        }

        @Override
        public String parseUrl() {
            return "page/" + page + "/?s=" + query;
        }
    }
}
