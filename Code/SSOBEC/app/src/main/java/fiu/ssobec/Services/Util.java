package fiu.ssobec.Services;

import android.graphics.PointF;
import android.widget.ImageView;

public class Util {

    /**
     * Calculates scaling factor for an image with original dimensions of
     * {@code originalWidth x originalHeight} being displayed with {@code imageView}.
     *
     * The assumption with this example code is that a) layout has been already performed for
     * {@code imageView} and that {@link android.widget.ImageView.ScaleType#CENTER_INSIDE} is used.
     *
     * @param originalWidth  height of the original bitmap to be displayed using {@code imageView}
     * @param originalHeight width of the original bitmap to be displayed using {@code imageView}
     */
    public static float calculateScaleFactor(int originalWidth, int originalHeight,
                                             ImageView imageView) {

        if (imageView.getScaleType() != ImageView.ScaleType.CENTER_INSIDE) {
            throw new IllegalArgumentException("only scale type of CENTER_INSIDE supported, was: "
                    + imageView.getScaleType());
        }

        final int availableX = imageView.getWidth()
                - (imageView.getPaddingLeft() + imageView.getPaddingRight());
        final int availableY = imageView.getHeight()
                - (imageView.getPaddingTop() + imageView.getPaddingBottom());

        if (originalWidth > availableX || originalHeight > availableY) {
            // original image would not fit without scaling
            return originalWidth > availableX
                    ? availableX / (float) originalWidth
                    : availableY / (float) originalHeight;
        } else {
            return 1f; // no scaling required
        }

    }


    /**
     * Calculates point where to draw coordinates {@code x} and {@code y} in a bitmap that's
     * original dimensions were {@code originalWidth x originalHeight} and may now be scaled down
     * as it's been displayed with {@code imageView}.
     *
     * @param originalWidth  width of the original bitmap before any scaling
     * @param originalHeight height of the original bitmap before any scaling
     * @param x              x-coordinate on original bitmap
     * @param y              y-coordinate on original bitmap
     * @param imageView      view that will be used to display bitmap
     * @param point          point where result value is to be stored
     * @see #calculateScaleFactor(int, int, ImageView)
     */
    public static void calculateScaledPoint(int originalWidth, int originalHeight,
                                            int x, int y,
                                            ImageView imageView,
                                            PointF point) {


        final float scale = calculateScaleFactor(originalWidth, originalHeight, imageView);
        final float scaledWidth = originalWidth * scale;
        final float scaledHeight = originalHeight * scale;

        // when image inside view is smaller than the view itself and image is centered (assumption)
        // there will be some empty space around the image (here offset)
        final float offsetX = Math.max(0, (imageView.getWidth() - scaledWidth) / 2);
        final float offsetY = Math.max(0, (imageView.getHeight() - scaledHeight) / 2);

        point.x = offsetX + (x * scale);
        point.y = offsetY + (y * scale);


    }


}