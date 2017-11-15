import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Point {

    private final double x;
    private final double y;
    private final double z;

    public Point(Double x, Double y, Double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX()
    {

        return x;
    }

    public double getY()
    {
        return y;
    }
    public double getZ()
    {
        return z;
    }

    public static void readWrite(String filename, List<Point> points){
        try{
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNextLine()){
                String string = sc.nextLine();
                String[] parse = string.split(",");
                points.add(new Point(
                        Double.parseDouble(parse[0]),
                        Double.parseDouble(parse[1]),
                        Double.parseDouble(parse[2])));
            }
            List<Point> ptList = points.stream()
                    .filter(s -> s.z <= 2.0)
                    .map(s -> new Point((s.x *.5), (s.y *.5) , s.z *.5))
                    .map(s -> new Point(s.x - 150, s.y - 37, s.z))
                    .collect(Collectors.toList());
            PrintStream ps = new PrintStream(new File("drawMe.txt"));
            for (Point p : ptList){
                ps.print(p.getX());
                ps.print(", ");
                ps.print(p.getY());
                ps.print(", ");
                ps.print(p.getZ());
                ps.println();
            }
            ps.close();
        }
        catch (Exception e) {
            System.out.println("Invalid");
        }



    }

    private static String getFilename(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Log file not specified.");
            System.exit(1);
        }

        return args[0];
    }

    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();
        String filename = getFilename(args);
        readWrite(filename, points);
    }

}



