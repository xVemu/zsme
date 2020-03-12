package pl.vemu.zsme.detailedNews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadImages extends AsyncTask<String, Void, Drawable> {

    private final Fragment fragment;

    @Override
    protected Drawable doInBackground(String... strings) {
        try {
            Bitmap bitmap = Glide.with(fragment)
                    .asBitmap()
                    .load(strings[0])
                    .submit()
                    .get();
            Drawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return drawable;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
