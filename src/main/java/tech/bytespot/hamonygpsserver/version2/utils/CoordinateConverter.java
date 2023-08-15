package tech.bytespot.hamonygpsserver.version2.utils;

public class CoordinateConverter {

    public static double[] convertCoordinates(String latitude, String longitude) {
        return new double[]{convertCoordinate(latitude), convertCoordinate(longitude)};
    }

    private static double convertCoordinate(String coordinate) {
        String direction = coordinate.substring(coordinate.length() - 1); // S, N, E, or W
        coordinate = coordinate.substring(0, coordinate.length() - 1).trim(); // Remove the direction

        int degreeIndex = coordinate.indexOf('.');
        String degrees = coordinate.substring(0, degreeIndex - 2);
        String minutes = coordinate.substring(degreeIndex - 2);

        double decimalCoordinate = Integer.parseInt(degrees) + Double.parseDouble(minutes) / 60;

        // Check for South or West, which will make the coordinate negative
        if (direction.equals("S") || direction.equals("W")) {
            decimalCoordinate = -decimalCoordinate;
        }

        return decimalCoordinate;
    }

}

