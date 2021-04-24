package pl.vemu.zsme.newsFragment;

public abstract class Queries {

    abstract String parseUrl(int page);

    static class Author extends Queries {

        private final String author;

        Author(String author) {
            this.author = author;
        }

        @Override
        public String parseUrl(int page) {
            return author + "/page/" + page;
        }

    }

    public static class Page extends Queries {

        @Override
        public String parseUrl(int page) {
            return "page/" + page;
        }
    }

    static class Search extends Queries {

        private final String query;

        Search(String query) {
            this.query = query;
        }

        @Override
        public String parseUrl(int page) {
            return "page/" + page + "/?s=" + query;
        }
    }
}
