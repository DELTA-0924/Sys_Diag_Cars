package sys.diag.car;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class OverlapPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        float scaleFactor = Math.max(0.85f, 1 - Math.abs(position * 0.5f));
        float translationY = position * page.getHeight() * 0.5f;

        page.setScaleY(scaleFactor);
        page.setTranslationY(-translationY);
    }
}
