package bases.imageTransform;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

public class RoundedTransform implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin;  // dp
    private boolean tl, tr, br, bl;
    private boolean isCustom = false;

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransform(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
        this.isCustom = false;
    }

    public RoundedTransform(final int radius, final int margin, boolean tl, boolean tr, boolean br, boolean bl) {
        this.radius = radius;
        this.margin = margin;
        this.isCustom = true;
        this.tl = tl;
        this.tr = tr;
        this.br = br;
        this.bl = bl;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        if(!isCustom) canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);
        else{
            canvas.drawPath(RoundedRect(margin, margin, source.getWidth() - margin, source.getHeight() - margin, radius, radius), paint);
        }

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    public Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry){
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else{
            path.rLineTo(0, -ry);
            path.rLineTo(-rx,0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.rLineTo(-rx, 0);
            path.rLineTo(0,ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.rLineTo(0, ry);
            path.rLineTo(rx,0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.rLineTo(rx,0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
