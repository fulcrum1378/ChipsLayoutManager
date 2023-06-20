package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import ir.mahdiparastesh.chlm.sample.BuildConfig;
import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b;
    private Drawer drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.toolbar.setTitle(getString(R.string.app_name_and_version, BuildConfig.VERSION_NAME));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, ItemsFragment.newInstance())
                    .commit();
        }

        setSupportActionBar(b.toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = new DrawerBuilder(this)
                .withToolbar(b.toolbar)
                .addDrawerItems(new PrimaryDrawerItem().withName(R.string.main).withIdentifier(1))
                .addDrawerItems(new PrimaryDrawerItem().withName(R.string.bottom_sheet).withIdentifier(2))
                .withOnDrawerItemClickListener(this::onDrawerItemClickListener)
                .build();
    }

    private boolean onDrawerItemClickListener(View view, int position, IDrawerItem drawerItem) {
        int id = (int) drawerItem.getIdentifier();
        switch (id) {
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, ItemsFragment.newInstance())
                        .commit();
                drawer.closeDrawer();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, BottomSheetFragment.newInstance())
                        .commit();
                drawer.closeDrawer();
                break;
        }
        return true;
    }
}
