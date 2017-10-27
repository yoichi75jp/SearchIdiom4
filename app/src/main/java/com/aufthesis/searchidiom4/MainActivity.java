package com.aufthesis.searchidiom4;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
}
