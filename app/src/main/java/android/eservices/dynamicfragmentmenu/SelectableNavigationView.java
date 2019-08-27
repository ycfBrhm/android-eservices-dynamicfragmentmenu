package android.eservices.dynamicfragmentmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

public class SelectableNavigationView extends NavigationView {

    private OnNavigationItemSelectedListener listener;

    public SelectableNavigationView(Context context) {
        super(context);
    }

    public SelectableNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        this.listener = listener;
        super.setNavigationItemSelectedListener(listener);
    }

    public void setSelectedItem(MenuItem menuItem) {
        listener.onNavigationItemSelected(menuItem);
        this.setCheckedItem(menuItem);
    }
}
