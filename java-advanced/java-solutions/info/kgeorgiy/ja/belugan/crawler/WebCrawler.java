package info.kgeorgiy.ja.belugan.crawler;



import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.NewCrawler;
import info.kgeorgiy.java.advanced.crawler.Result;

/**
 * Class that recursively crawls sites.
 */

public class WebCrawler implements Crawler, NewCrawler {
    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private static final String USAGE = "WebCrawler url [depth [downloads [extractors [perHost]]]]";

    /**
     * Constructor for this WebCrawler.
     *
     * @param downloader  a class for downloading files from the internet.
     * @param downloaders number of parallel threads to be downloaded.
     * @param extractors  number of parallel threads for retrieving references.
     * @param perHost     the number of parallel threads that can access the host.
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    /**
     * Main method of a class that downloads pages from the internet.
     *
     * @param args url [depth [downloaders [extractors [perHost]]]].
     */
    public static void main(String[] args) {
        // :NOTE: depth, downloaders .. are option parameters
        if (args == null || args.length < 1) {
            System.err.println("Wrong number of arguments");
            return;
        }
        try {
            WebCrawler webCrawler = new WebCrawler(new CachingDownloader((1)), Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]), Integer.parseInt(args[4])
            );
            webCrawler.download(args[0], Integer.parseInt(args[1]));
        } catch (IOException e) {
            // :NOTE: usage
            System.err.println("couldn't create a downloader:" + e.getMessage());
            System.err.println(USAGE);
        } catch (NumberFormatException e) {
            // :NOTE: usage
            System.err.println("couldn't parse integer argument" + e.getMessage());
            System.err.println(USAGE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        BFSCrawler bfsCrawler = new BFSCrawler();
        Phaser phaser = new Phaser(1);
        if (excludes.stream().noneMatch(url::contains)) {
            bfsCrawler.links.add(url);
            LinkedList<String> queueForUrls;
            while (depth > 0) {
                queueForUrls = bfsCrawler.getLinksFromQueue();
                int finalDepth = depth;
                Consumer<String> consumer = string -> {
                    if (checkProcessLink(excludes, string, bfsCrawler)) {
                        phaser.register();
                        downloaders.submit(new WebDownload(bfsCrawler, string, finalDepth, phaser));
                    }
                };
                queueForUrls.forEach(consumer);
                // :NOTE: bad sync on parallel download()
                phaser.arriveAndAwaitAdvance();
                depth--;
            }
        }
        return new Result(new ArrayList<>(bfsCrawler.addedToQueue), bfsCrawler.wrongUrls);
    }

    private static boolean checkProcessLink(Set<String> excludes, String string, BFSCrawler bfsCrawler) {
        return !(bfsCrawler.addedToQueue.contains(string) || bfsCrawler.wrongUrls.containsKey(string))
                && excludes.stream().noneMatch(string::contains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result download(String url, int depth) {
        return download(url, depth, ConcurrentHashMap.newKeySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        downloaders.close();
        extractors.close();
    }

    /**
     * Static class that containing structures for working with URLs
     */
    public static class BFSCrawler {
        // :NOTE: package-private
        Set<String> addedToQueue;
        ConcurrentMap<String, IOException> wrongUrls;
        Set<String> links;

        /**
         * Constructor for this {@link BFSCrawler}
         */
        public BFSCrawler() {
            this.addedToQueue = ConcurrentHashMap.newKeySet();
            this.wrongUrls = new ConcurrentHashMap<>();
            this.links = ConcurrentHashMap.newKeySet();
        }

        /**
         * Returns a set of links and clear {@link ArrayList}
         *
         * @return {@link LinkedList} that contains all links
         */
        public LinkedList<String> getLinksFromQueue() {
            LinkedList<String> list = new LinkedList<>(links);
            links.clear();
            return list;
        }
    }

// :NOTE: WebDownload

    /**
     * Class for loading pages implements {@link Runnable}
     */
    private class WebDownload implements Runnable {
        private final BFSCrawler bfsCrawler;
        private final int depth;
        private final String url;
        private final Phaser phaser;


        /**
         * Constructor for {@link WebDownload}
         *
         * @param bfsCrawler copy of {@link BFSCrawler} class
         * @param url        URL string
         * @param depth      depth
         * @param phaser phaser
         */
        public WebDownload(BFSCrawler bfsCrawler, String url, int depth, Phaser phaser) {
            this.bfsCrawler = bfsCrawler;
            this.url = url;
            this.depth = depth;
            this.phaser = phaser;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                Document document = downloader.download(url);
                bfsCrawler.addedToQueue.add(url);
                if (depth > 1) {
                    phaser.register();
                    extractors.submit(() -> {
                        try {
                            bfsCrawler.links.addAll(document.extractLinks());
                        } catch (IOException ignored) {
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    });
                }
            } catch (IOException e) {
                bfsCrawler.wrongUrls.put(url, e);
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }
}





