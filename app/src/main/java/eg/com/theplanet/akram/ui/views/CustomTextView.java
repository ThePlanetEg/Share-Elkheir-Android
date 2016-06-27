package eg.com.theplanet.akram.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.utils.Constants;


public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            String fontName = a.getString(R.styleable.CustomTextView_fontName);
            if (fontName != null) {
                String language =
                        PreferenceManager.getDefaultSharedPreferences(
                                getContext()).getString(Constants.LANGUAGE, "");

                if (language.contentEquals("en"))
                    fontName = "VodafoneRg.ttf";
                else
                    fontName = "GE_SS_Two_Light.otf";

                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);

                if (typeface != null)
                    setTypeface(typeface);
            }
            a.recycle();
        }
    }
}
