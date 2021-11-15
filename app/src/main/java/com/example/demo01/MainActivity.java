package com.example.demo01;


import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;



public class MainActivity extends AppCompatActivity {

    private PhotosTab photosTabFragment;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 666);
        }
        else {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 666);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, PhotosTab.class, null)
                    .commit();
        }

        MaterialToolbar topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        topAppBar.getMenu().findItem(R.id.newFolder_option).setVisible(false);

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.selectItems_option:
                        actionMode = setContextualActionBar();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        photosTabFragment = (PhotosTab) fragmentManager.findFragmentById(R.id.fragment_container_view);
                        photosTabFragment.onMsgFromMainToPhotosTabFragment("SELECT ITEMS");
                        break;
                    case R.id.settings_option:
                        //TODO show Settings Screen
                        Toast.makeText(MainActivity.this, "Settings Option selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.trash_option:
                        //TODO show Trash Screen
                        Toast.makeText(MainActivity.this, "Trash Option selected", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.favourites_option:
                        //TODO show Favourites Screen
                        Toast.makeText(MainActivity.this, "Favourites Options selected", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        // Bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Context context = getApplicationContext();
                switch (item.getItemId()) {
                    case R.id.photos_tab:
                        topAppBar.getMenu().findItem(R.id.newFolder_option).setVisible(false);
                        topAppBar.getMenu().findItem(R.id.selectItems_option).setVisible(true);
                        if (savedInstanceState == null) {
                            getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .add(R.id.fragment_container_view, PhotosTab.class, null)
                                    .commit();
                        }
                        break;
                    case R.id.folders_tab:
                        topAppBar.getMenu().findItem(R.id.newFolder_option).setVisible(true);
                        topAppBar.getMenu().findItem(R.id.selectItems_option).setVisible(false);
                        if (savedInstanceState == null) {
                            getSupportFragmentManager().beginTransaction()
                                    .setReorderingAllowed(true)
                                    .add(R.id.fragment_container_view, FoldersTab.class, null)
                                    .commit();
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private final ActionMode.Callback mCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share_option:
                    Toast.makeText(getApplicationContext(), "Share option clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.delete_option:
                    Toast.makeText(getApplicationContext(), "Delete option clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.selectAll_option:
                    photosTabFragment.onMsgFromMainToPhotosTabFragment("SELECT ALL");
                    break;
                case R.id.favorites_option:
                    Toast.makeText(getApplicationContext(), "Favourite option clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.moveToFolder_option:
                    Toast.makeText(getApplicationContext(), "Move To Folder option clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.copyToFolder_option:
                    Toast.makeText(getApplicationContext(), "Copy To Folder option clicked", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            photosTabFragment.onMsgFromMainToPhotosTabFragment("TURN OFF SELECT ITEMS");
        }
    };

    public ActionMode setContextualActionBar() {
        ActionMode actionMode = startSupportActionMode(mCallback);
        actionMode.setTitle("Select items");
        return actionMode;
    }

    public void setTitleContextualActionBar(String str) {
        actionMode.setTitle(str);
    }

    public void changeTitleContextualActionBar() {
        photosTabFragment.onMsgFromMainToPhotosTabFragment("CHANGE CONTEXTUAL ACTION BAR");
    }

    public void onMsgFromPhotosTabFragmentToMain(String key, String value) {
        if (key.equals("CHANGE TITLE")) {
            setTitleContextualActionBar(value);
        }

    }

}