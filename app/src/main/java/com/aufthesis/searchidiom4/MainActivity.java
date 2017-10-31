package com.aufthesis.searchidiom4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private String m_today;

    private Spinner m_patternSelectSpinner;
    private ListView m_idiomListView;
    private SearchView m_searchView;
    static public SQLiteDatabase m_db;
    private Context m_context;

    private SharedPreferences m_prefs;

    private List<Idiom> m_resultItems;

    private MyBaseAdapter m_myBaseAdapter;



    private boolean m_isJP = Locale.getDefault().toString().equals(Locale.JAPAN.toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_context = this;
        DateFormat format = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        m_today = format.format(new Date());
        DBOpenHelper dbHelper = new DBOpenHelper(this);
        m_db = dbHelper.getDataBase();
        m_resultItems = new ArrayList<>();

        m_myBaseAdapter = new MyBaseAdapter(this, m_resultItems, MyBaseAdapter.eActivity.Main);

        m_prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        m_patternSelectSpinner = findViewById(R.id.pattern_select);
        m_idiomListView = findViewById(R.id.list_result);
        m_idiomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
               MyBaseAdapter adapter = (MyBaseAdapter)arg0.getItemAtPosition(arg2);
               final Idiom idiom = (Idiom)adapter.getItem(arg2);

                AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
                builder.setTitle("意味を調べる");
                builder.setMessage("この四字熟語の意味を調べますか？");
                // OKの時の処理
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        idiom.Checked(m_today);
                        Intent intent = new Intent(m_context, WebBrowserActivity.class);
                        intent.putExtra(SearchManager.QUERY, getString(R.string.search_word, idiom.getName()));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // ダイアログの表示
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        m_searchView = findViewById(R.id.search_view);
        m_searchView.setIconified(false);
        m_searchView.setIconifiedByDefault(false);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        if(searchManager == null) return;
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
                searchIdiom(s);
                return false;
            }
        });


        m_idiomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });
    }

    private void searchIdiom(String query)
    {
        //ArrayAdapterに対してListViewのリスト(items)の更新
        m_resultItems.clear();

        String lang = "\"read-en\"";
        if(m_isJP) lang = "\"read-ja\"";
        String sql = "Select * from CharacterIdiom4 where ";
        String pattern = m_patternSelectSpinner.getSelectedItem().toString();
        if(pattern.equals(getString(R.string.switch_forward)))
        {
            sql += "idiom like '%"
                    + query
                    + "' or "
                    + lang
                    + " like '%"
                    + query
                    + "'";
        }
        else if(pattern.equals(getString(R.string.switch_partial)))
        {
            sql += "idiom like '%"
                    + query
                    + "%' or "
                    + lang
                    + " like '%"
                    + query
                    + "%'";
        }
        else if(pattern.equals(getString(R.string.switch_backward)))
        {
            sql += "idiom like '"
                    + query
                    + "%' or "
                    + lang
                    + " like '"
                    + query
                    + "%'";
        }

        // DBからデータを取得
        Cursor cursor = m_db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                // MyListItemのコンストラクタ呼び出しIdiomのオブジェクト生成)
                String idomName = cursor.getString(0);
                String idomRead;
                if(m_isJP)
                    idomRead = cursor.getString(1);
                else
                    idomRead = cursor.getString(2);
                boolean isFavorite = cursor.getInt(4) == 1;
                int checkCount = cursor.getInt(5);
                String checkedDay = cursor.getString(6);
                Idiom data = new Idiom(idomName, idomRead, isFavorite, checkCount, checkedDay);
                m_resultItems.add(data);          // 取得した要素をitemsに追加
            } while (cursor.moveToNext());
        }
        cursor.close();
        m_idiomListView.setAdapter(m_myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        m_myBaseAdapter.notifyDataSetChanged();   // Viewの更新
    }


    // 設定値 ArrayList<String> を保存（Context は Activity や Application や Service）
    private void saveList(String key, ArrayList<String> list) {
        JSONArray jsonAry = new JSONArray();
        for(int i = 0; i < list.size(); i++) {
            jsonAry.put(list.get(i));
        }
        SharedPreferences.Editor editor = m_prefs.edit();
        editor.putString(key, jsonAry.toString());
        editor.apply();
    }

    // 設定値 ArrayList<String> を取得（Context は Activity や Application や Service）
    private ArrayList<String> loadList(String key) {
        ArrayList<String> list = new ArrayList<>();
        String strJson = m_prefs.getString(key, ""); // 第２引数はkeyが存在しない時に返す初期値
        if(!strJson.equals("")) {
            try {
                JSONArray jsonAry = new JSONArray(strJson);
                for(int i = 0; i < jsonAry.length(); i++) {
                    list.add(jsonAry.getString(i));
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
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
