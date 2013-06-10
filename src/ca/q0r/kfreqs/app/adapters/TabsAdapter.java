package ca.q0r.kfreqs.app.adapters;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener , ViewPager.OnPageChangeListener {
    private final FragmentActivity mActivity;
    private final ActionBar mActionBar;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    private int pos = 0;

    static final class TabInfo {
        private final Class<?> clazz;
        private String tag;

        TabInfo(Class<?> _class){
            clazz = _class;
            tag =  "tag_" + clazz.getSimpleName();
        }

        public String getTag() {
            return tag;
        }
    }

    public TabsAdapter(FragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());

        mActivity = activity;
        mActionBar = activity.getActionBar();
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<? extends Fragment> clazz){
        TabInfo info = new TabInfo(clazz);

        tab.setTag(info.getTag());
        tab.setTabListener(this);

        mTabs.add(info);
        mActionBar.addTab(tab);

        notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);

        pos = position;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());

        String tag = (String) tab.getTag();

        for (int i = 0; i < mTabs.size(); i++){
            if (mTabs.get(i).getTag().equals(tag)){
                mViewPager.setCurrentItem(i);
            }
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mActivity, mTabs.get(position).clazz.getName());
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        final TabInfo info = mTabs.get(position);
        info.tag = fragment.getTag();
        return fragment;
    }

    public Fragment getFragment() {
        return mActivity.getSupportFragmentManager().findFragmentByTag(mTabs.get(pos).getTag());
    }
}
