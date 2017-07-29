package com.darurats.popularmovies.utils;

public final class ImageUtils {

    private ImageUtils() {
        throw new AssertionError("No instances.");
    }

    /**
     * Helper method which build corresponding url for poster based on the target's size
     * NOTE: only works with "poster_size" values
     *
     * @param imagePath which is returned with movies
     * @param width     of the target (ex. ImageView)
     * @return poster's full url
     */
    public static String buildImageUrl(String imagePath, int width) {
        String widthPath;

        if (width <= 92) {
            widthPath = "/w92";
        } else if (width <= 154) {
            widthPath = "/w154";
        } else if (width <= 185) {
            widthPath = "/w185";
        } else if (width <= 342) {
            widthPath = "/w342";
        } else if (width <= 500) {
            widthPath = "/w500";
        } else {
            widthPath = "/w780";
        }

        return MovieConstants.API.IMAGE_BASE_URL + widthPath + imagePath;
    }
}