package pl.vemu.zsme.newsFragment;

import lombok.RequiredArgsConstructor;

public abstract class Queries {

    abstract String parseUrl(int page);

    @RequiredArgsConstructor
    static class Author extends Queries {

        private final String author;

        @Override
        public String parseUrl(int page) {
            return author + "page/" + page;
        }

    }

    static class Page extends Queries {

        @Override
        public String parseUrl(int page) {
            return "page/" + page;
        }
    }

    @RequiredArgsConstructor
    static class Search extends Queries {

        private final String query;

        @Override
        public String parseUrl(int page) {
            return "page/" + page + "/?s=" + query;
        }
    }
}
