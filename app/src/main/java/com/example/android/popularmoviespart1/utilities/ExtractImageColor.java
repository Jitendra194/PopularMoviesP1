package com.example.android.popularmoviespart1.utilities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;


@SuppressWarnings("EmptyMethod")
public abstract class ExtractImageColor implements Target {

    private final ImageView mImageView;

    public ExtractImageColor(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        mImageView.setImageBitmap(bitmap);
        generateColorSwatch(bitmap);

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    private void generateColorSwatch(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    List<Palette.Swatch> swatchList = palette.getSwatches();
                    int population = 0;
                    int swatchNumber = 0;
                    Palette.Swatch swatch;
                    for (int i = 0; i < swatchList.size(); i++) {
                        swatch = swatchList.get(i);
                        if (swatch.getPopulation() > population) {
                            population = swatch.getPopulation();
                            swatchNumber = i;
                        }
                    }
                    Palette.Swatch colors = palette.getSwatches().get(swatchNumber);
                    if (colors != null) {
                        setSwatchToViews(colors);
                        onSuccess();
                    } else {
                        onError();
                    }
                }
            });
        }
    }

    public abstract void setSwatchToViews(Palette.Swatch swatch);

    public abstract void onSuccess();

    public abstract void onError();
}
