package org.app.support;

/**
 * Created by patel on 6/1/2017.
 */
import org.app.support.TypefaceUtil;
import android.app.Application;

public class AppFonts extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        //TypefaceUtil.overrideFont(this, "SANS_SERIF", "fonts/quicksand.ttf");
        TypefaceUtil.overrideFont(this, "SANS_SERIF", "fonts/exoregular.ttf");
    }
}
