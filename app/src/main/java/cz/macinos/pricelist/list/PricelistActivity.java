package cz.macinos.pricelist.list;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import cz.macinos.pricelist.R;
import cz.macinos.pricelist.model.PricelistItem;

/**
 * Base activity for pricelist and selection fragments.
 */
public class PricelistActivity extends AppCompatActivity {

    private static final String TAG = "PricelistActivity";

    private static String rawPricelist;
    private static List<PricelistItem> selectedPricelistItems = new ArrayList<>();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricelist);

        //get pricelist from shared preferences
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_database), MODE_PRIVATE);
        rawPricelist = sp.getString(getString(R.string.preference_database), getString(R.string.preference_not_existing));

       // Log.i(TAG, "*** Obtained pricelist: " + rawPricelist);

      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static String getRawPricelist() {
        return rawPricelist;
    }

    public static List<PricelistItem> getSelectedPricelistItems() {
        return selectedPricelistItems;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PricelistFragment(), getString(R.string.pricelist_tab));
        adapter.addFragment(new SelectionFragment(), getString(R.string.selection_tab));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
