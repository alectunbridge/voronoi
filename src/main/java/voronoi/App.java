package voronoi;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App extends JFrame {

    public static final int WIDTH = 1000;

    public static final List<NorthingsAndEastings> MISSING_NORTHINGS_AND_EASTINGS = new ArrayList<>();
    public static Set<Site> missingSites = new HashSet<>();

    static {
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF55FF", 312623, 175429));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF118GE", 315251, 176555));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF239FP", 320972, 178385));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF401AY", 299450, 192340));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF448EZ", 300452, 202834));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CF470US", 305548, 205164));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CH52GG", 331254, 370751));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CH52LF", 330952, 372077));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CH87FJ", 318737, 375627));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("CH87SA", 318911, 375161));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("SA112DH", 274494, 196138));
        MISSING_NORTHINGS_AND_EASTINGS.add(new NorthingsAndEastings("SA153QG", 250830, 200612));
    }

    private final JPanel jpanel = new JPanel();

    public App() {
        super("Drawing Demo");
        setSize(WIDTH, WIDTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JScrollPane scrollBar=new JScrollPane(jpanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollBar);
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawStuff(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App().setVisible(true);
            }
        });
    }

    public void drawStuff(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        PowerDiagram diagram = new PowerDiagram();

// normal list based on an array
        OpenList sites = new OpenList();

        Random rand = new Random(100);
// create a root polygon which limits the voronoi diagram.
// here it is just a rectangle.

        PolygonSimple rootPolygon = new PolygonSimple();
        int width = WIDTH;
        int height = WIDTH;
        rootPolygon.add(0, 0);
        rootPolygon.add(width, 0);
        rootPolygon.add(width, height);
        rootPolygon.add(0, height);

// create 100 points (sites) and set random positions in the rectangle defined above.
//        for (int i = 0; i < 100; i++) {
//            Site site = new Site(rand.nextInt(width), rand.nextInt(width));
//            // we could also set a different weighting to some sites
//            // site.setWeight(30)
//            sites.add(site);
//        }
        List<Site> sitesFromFile = readFile();
        sitesFromFile.forEach(sites::add);
// set the list of points (sites), necessary for the power diagram
        diagram.setSites(sites);
// set the clipping polygon, which limits the power voronoi diagram
        diagram.setClipPoly(rootPolygon);

// do the computation
        diagram.computeDiagram();

// for each site we can no get the resulting polygon of its cell.
// note that the cell can also be empty, in this case there is no polygon for the corresponding site.
        for (int i = 0; i < sites.size; i++) {
            Site site = sites.array[i];
            PolygonSimple polygon = site.getPolygon();
            if (site.getPolygon() != null) {
                g2d.draw(polygon);
                if(missingSites.contains(site)){
                    g2d.setColor(Color.RED);
                    g2d.fill(polygon);
                    g2d.setColor(Color.BLACK);
                }
            }
        }


    }

    public java.util.List<Site> readFile() {
        java.util.List<Site> result = new ArrayList<>();
        java.util.List<String> input = null;
        try {
            input = Files.lines(Paths.get(ClassLoader.getSystemResource("welsh.csv")
                    .toURI())).collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        java.util.List<NorthingsAndEastings> northingsAndEastings = new ArrayList<>();
        for (String line : input) {
            String[] fields = line.split(",");
            String postcode = fields[0];
            int easting = Integer.parseInt(fields[2]);
            int northing = Integer.parseInt(fields[3]);
            if (easting == 0 || northing == 0) {
                System.err.println(line);
            } else {
                northingsAndEastings.add(new NorthingsAndEastings(postcode, easting, northing));
            }
        }
        northingsAndEastings.addAll(MISSING_NORTHINGS_AND_EASTINGS);

        int minEasting = northingsAndEastings.stream().min(Comparator.comparing(NorthingsAndEastings::getEasting)).get().getEasting();
        int maxEasting = northingsAndEastings.stream().mapToInt(NorthingsAndEastings::getEasting).max().getAsInt();
        int minNorthing = northingsAndEastings.stream().mapToInt(NorthingsAndEastings::getNorthing).min().getAsInt();
        int maxNorthing = northingsAndEastings.stream().mapToInt(NorthingsAndEastings::getNorthing).max().getAsInt();

        int eastingRange = maxEasting - minEasting;
        int northingRange = maxNorthing - minNorthing;

        System.out.println("Easting range: " + eastingRange);
        System.out.println("Northing range: " + northingRange);

        int maxNorthingEastingPlotValue = Integer.max(eastingRange, northingRange);

        for (NorthingsAndEastings northingsAndEasting : northingsAndEastings) {
            double x = WIDTH * (northingsAndEasting.getEasting() - minEasting) / (double) maxNorthingEastingPlotValue;
            double y = WIDTH * (northingsAndEasting.getNorthing() - minNorthing) / (double) maxNorthingEastingPlotValue;
            Site newSite = new Site(x, y);
            if(MISSING_NORTHINGS_AND_EASTINGS.contains(northingsAndEasting)) {
                missingSites.add(newSite);
            }
            result.add(newSite);
        }

        return result;
    }
}
