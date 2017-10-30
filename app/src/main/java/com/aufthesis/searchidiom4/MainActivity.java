package com.aufthesis.searchidiom4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends Activity {

    private SearchView m_searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_searchView = findViewById(R.id.search_view);
        m_searchView.setIconified(false);
        m_searchView.setIconifiedByDefault(false);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName compName = getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(compName);
        m_searchView.setSearchableInfo(searchableInfo);
        m_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // queryを用いて処理を行う
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // m_searchViewに音声入力の結果を入れる
            // 音声入力後にそのまま別のActivityに飛ばす等する際はここで処理をする
            m_searchView.setQuery(query, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //m_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.close)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.final_title));
            dialog.setMessage(getString(R.string.final_message));
            dialog.setPositiveButton(getString(R.string.final_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    //moveTaskToBack(true);
                }
            });
            dialog.setNegativeButton(getString(R.string.final_cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }
        if(id == R.id.favorite)
        {
            Intent intent = new Intent(this, FavoriteActivity.class);
            int requestCode = 1;
            startActivityForResult(intent, requestCode);
            //startActivity(intent);
            // アニメーションの設定
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
            return true;
        }
        if(id == R.id.history)
        {
            Intent intent = new Intent(this, HistoryActivity.class);
            int requestCode = 2;
            startActivityForResult(intent, requestCode);
            //startActivity(intent);
            // アニメーションの設定
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
