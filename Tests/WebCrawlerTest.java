package Tests;

import Crawler.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class WebCrawlerTest {

    private WebCrawler crawler;
    private final String URL = "http://localhost:3000/A";
    private final String[] TRAVERSE_ORDER_BFS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"};
    private final String[] TRAVERSE_ORDER_DFS = {"A", "D", "J", "M", "N", "H", "I", "L", "C", "G", "F", "B", "E", "K"};

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setup() {

        crawler = new WebCrawler(URL);
        crawler.randomizeTraversal(false); // Set this to predict travel pattern
        crawler.sendJSONtoStdout(false);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testBreadthFirstSearch(){

        crawler.sendJSONtoStdout(true);
        crawler.buildCyclicGraph(true);
        crawler.setHeightLimit(3);
        WebGraph graph = crawler.executeCrawl(14, DataStructureType.QUEUE);

        graph.printTransmissionInformation();
        graph.printGraphSummary();
        graph.printGraphContents();


        // Check the traversal order
        for(int i = 0; i < graph.getWebPages().size(); i++){

            WebPage page = graph.getWebPages().get(i);
            Assert.assertTrue(page.getTitle().compareTo(TRAVERSE_ORDER_BFS[i]) == 0);
        }

        // Check the links
        Assert.assertTrue(graph.getLinkCount() == 14);
        Assert.assertTrue(graph.getAcyclicLinkCount() == 12);
        Assert.assertTrue(graph.getCyclicLinkCount() == 2);
        Assert.assertTrue(graph.getBidirectionalLinkCount() == 2);

        // Check the domains
        Assert.assertTrue(graph.getDomainCount() == 1);

        // Invalid Link Count
        Assert.assertTrue(graph.getInvalidURLCount() == 0);
    }

    @Test
    public void testBFSSearchTerm(){

        crawler.setDataStructureType(DataStructureType.QUEUE);
        crawler.setSearchTerm("testA");
        WebGraph graph = crawler.executeCrawl(13, DataStructureType.QUEUE);

        graph.printGraphSummary();

        // Check the traversal order, search should stop at first page
        WebPage page = graph.getWebPages().get(0);
        Assert.assertTrue(page.getTitle().compareTo(TRAVERSE_ORDER_BFS[0]) == 0);

        // Check the links
        Assert.assertTrue(graph.getLinkCount() == 0);
        Assert.assertTrue(graph.getAcyclicLinkCount() == 0);
        Assert.assertTrue(graph.getCyclicLinkCount() == 0);
        Assert.assertTrue(graph.getBidirectionalLinkCount() == 0);

        // Check the domains
        Assert.assertTrue(graph.getDomainCount() == 1);

        // Invalid Link Count
        Assert.assertTrue(graph.getInvalidURLCount() == 0);
    }

    @Test
    public void testDepthFirstSearch(){

        crawler.setDataStructureType(DataStructureType.STACK);
        WebGraph graph = crawler.executeCrawl(14,  DataStructureType.STACK);

        graph.printGraphSummary();

        // Check the traversal order
        for(int i = 0; i < graph.getWebPages().size(); i++){

            WebPage page = graph.getWebPages().get(i);
            Assert.assertTrue(page.getTitle().compareTo(TRAVERSE_ORDER_DFS[i]) == 0);
        }

        // Check the links
        Assert.assertTrue(graph.getLinkCount() == 17);
        Assert.assertTrue(graph.getAcyclicLinkCount() == 13);
        Assert.assertTrue(graph.getCyclicLinkCount() == 4);
        Assert.assertTrue(graph.getBidirectionalLinkCount() == 2);

        // Check the domains
        Assert.assertTrue(graph.getDomainCount() == 1);

        // Invalid Link Count
        Assert.assertTrue(graph.getInvalidURLCount() == 0);
    }

    @Test
    public void testDFSSearchTerm(){

        crawler.setDataStructureType(DataStructureType.STACK);
        crawler.setSearchTerm("testA");
        WebGraph graph = crawler.executeCrawl(14, DataStructureType.STACK);

        graph.printGraphSummary();

        // Check the traversal order, search should stop at first page
        WebPage page = graph.getWebPages().get(0);
        Assert.assertTrue(page.getTitle().compareTo(TRAVERSE_ORDER_BFS[0]) == 0);

        // Check the links
        Assert.assertTrue(graph.getLinkCount() == 0);
        Assert.assertTrue(graph.getAcyclicLinkCount() == 0);
        Assert.assertTrue(graph.getCyclicLinkCount() == 0);
        Assert.assertTrue(graph.getBidirectionalLinkCount() == 0);

        // Check the domains
        Assert.assertTrue(graph.getDomainCount() == 1);

        // Invalid Link Count
        Assert.assertTrue(graph.getInvalidURLCount() == 0);
    }
}
