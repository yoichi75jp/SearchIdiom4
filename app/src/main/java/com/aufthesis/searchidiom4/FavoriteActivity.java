package com.aufthesis.searchidiom4;

/*
 * Created by a2035210 on 2017/10/30.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends Activity {

    private ListView m_favoriteListView;
    private TextView m_favoriteCount;

    private Context m_context;
    private List<Idiom> m_favoriteItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        m_context = this;
        m_favoriteItems = new ArrayList<>();

        m_favoriteCount = findViewById(R.id.favorite_count);
        m_favoriteListView = findViewById(R.id.list_favorite);
        m_favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Idiom idiom = m_favoriteItems.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
                builder.setTitle(getString(R.string.find_meaning));
                builder.setMessage(getString(R.string.confirm_whether_to_check_meaning));
                // OKの時の処理
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        idiom.Checked(MainActivity.m_today);
                        Intent intent = new Intent(m_context, WebBrowserActivity.class);
                        intent.putExtra(SearchManager.QUERY, getString(R.string.search_word, idiom.getName()));
                        startActivityForResult(intent,1);
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // ダイアログの表示
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        this.createFavoriteList();
    }

    private void createFavoriteList()
    {
        m_favoriteItems.clear();
        m_favoriteCount.setVisibility(View.INVISIBLE);
        for(int i = 0; i < MainActivity.m_savedIdiomList.size(); i++)
        {
            Idiom data = MainActivity.m_savedIdiomList.get(i);
            if(data.isFavorite())
                m_favoriteItems.add(data);          // 取得した要素をitemsに追加
        }
        MyBaseAdapter myBaseAdapter = new MyBaseAdapter(this, m_favoriteItems, MyBaseAdapter.eActivity.Favorite);
        m_favoriteListView.setAdapter(myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        myBaseAdapter.notifyDataSetChanged();   // Viewの更新

        m_favoriteCount.setText(getString(R.string.search_count,m_favoriteItems.size()));
        if(m_favoriteItems.size() > 0)
        {
            m_favoriteCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //if (m_adView != null) {
        //    m_adView.resume();
        //}
    }

    @Override
    public void onPause() {
        //if (m_adView != null) {
        //    m_adView.pause();
        //}
        MainActivity.saveList(getString((R.string.key_save)), MainActivity.m_saveList);
        MainActivity.syncData();
        super.onPause();
        //m_soundPool.release();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy()
    {
        //if (m_adView != null) {
        //    m_adView.destroy();
        //}
        MainActivity.saveList(getString((R.string.key_save)), MainActivity.m_saveList);
        MainActivity.syncData();
        super.onDestroy();
        setResult(RESULT_OK);
    }

}
