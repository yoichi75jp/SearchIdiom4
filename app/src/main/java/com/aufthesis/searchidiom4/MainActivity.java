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
import android.widget.TextView;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    static public String m_today;
    private String m_query = "";

    private Spinner m_patternSelectSpinner;
    private SearchView m_searchView;
    private ListView m_idiomListView;
    private TextView m_txtCount;

    static public SQLiteDatabase m_db;
    private Context m_context;

    static public SharedPreferences m_prefs;
    static public final String DELIMITER = "@";

    private MyBaseAdapter m_myBaseAdapter;
    private List<Idiom> m_resultItems;
    static public ArrayList<String> m_saveList;
    static public ArrayList<Idiom> m_savedIdiomList;

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

        m_prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        m_saveList = loadList(getString(R.string.key_save));
        m_savedIdiomList = new ArrayList<>();
        for(int i = 0; i < m_saveList.size(); i++)
            m_savedIdiomList.add(new Idiom(m_saveList.get(i)));

        m_txtCount = findViewById(R.id.txt_hit_count);
        m_txtCount.setVisibility(View.INVISIBLE);
        m_idiomListView = findViewById(R.id.list_result);
        m_idiomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Idiom idiom = m_resultItems.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
                builder.setTitle(getString(R.string.find_meaning));
                builder.setMessage(getString(R.string.confirm_whether_to_check_meaning));
                // OKの時の処理
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        idiom.Checked(m_today);
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
                m_searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchIdiom(s);
                m_query = s;
                return false;
            }
        });

        m_patternSelectSpinner = findViewById(R.id.pattern_select);
        m_patternSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchIdiom(m_query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void searchIdiom(String query)
    {
        //ArrayAdapterに対してListViewのリスト(items)の更新
        m_resultItems.clear();
        m_txtCount.setVisibility(View.INVISIBLE);
        if(query.equals("")) query = "@@@";     //検索結果が0件になるように適当な文字列(@@@)を格納

        String lang = "\"read-en\"";
        if(m_isJP) lang = "\"read-ja\"";
        String sql = "Select * from CharacterIdiom4 where ";
        String pattern = m_patternSelectSpinner.getSelectedItem().toString();
        if(pattern.equals(getString(R.string.switch_backward)))
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
        else if(pattern.equals(getString(R.string.switch_forward)))
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
                String idiomName = cursor.getString(0);
                String idiomRead;
                if(m_isJP)
                    idiomRead = cursor.getString(1);
                else
                    idiomRead = cursor.getString(2);
                boolean isFavorite = cursor.getInt(4) == 1;
                int checkCount = cursor.getInt(5);
                String checkedDay = cursor.getString(6);
                Idiom data = new Idiom(idiomName, idiomRead, isFavorite, checkCount, checkedDay);
                for(int i = 0; i < m_savedIdiomList.size(); i++)
                {
                    if (data.equals(m_savedIdiomList.get(i)))
                    {
                        data = m_savedIdiomList.get(i);
                        break;
                    }
                }
                m_resultItems.add(data);          // 取得した要素をitemsに追加
            } while (cursor.moveToNext());
        }
        cursor.close();
        m_myBaseAdapter = new MyBaseAdapter(this, m_resultItems, MyBaseAdapter.eActivity.Main);
        m_idiomListView.setAdapter(m_myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        m_myBaseAdapter.notifyDataSetChanged();   // Viewの更新

        m_txtCount.setText(getString(R.string.search_count,m_resultItems.size()));
        if(m_resultItems.size() > 0)
            m_txtCount.setVisibility(View.VISIBLE);
    }


    // 設定値 ArrayList<String> を保存（Context は Activity や Application や Service）
    static public void saveList(String key, ArrayList<String> list) {
        JSONArray jsonAry = new JSONArray();
        for(int i = 0; i < list.size(); i++) {
            jsonAry.put(list.get(i));
        }
        SharedPreferences.Editor editor = m_prefs.edit();
        editor.putString(key, jsonAry.toString());
        editor.apply();
    }

    // 設定値 ArrayList<String> を取得（Context は Activity や Application や Service）
    static public ArrayList<String> loadList(String key) {
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

    /**/
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
    /**/

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                m_myBaseAdapter.notifyDataSetChanged();   // Viewの更新
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        m_saveList = loadList(getString(R.string.key_save));
        m_savedIdiomList.clear();
        for(int i = 0; i < m_saveList.size(); i++)
            m_savedIdiomList.add(new Idiom(m_saveList.get(i)));
        //if (m_adView != null) {
        //    m_adView.resume();
        //}
    }

    @Override
    public void onPause() {
        //if (m_adView != null) {
        //    m_adView.pause();
        //}
        saveList(getString((R.string.key_save)), m_saveList);
        super.onPause();
        //m_soundPool.release();
    }
    @Override
    protected void onRestart() {
        m_saveList = loadList(getString(R.string.key_save));
        m_savedIdiomList.clear();
        for(int i = 0; i < m_saveList.size(); i++)
            m_savedIdiomList.add(new Idiom(m_saveList.get(i)));
        super.onRestart();
    }

    @Override
    public void onDestroy()
    {
        //if (m_adView != null) {
        //    m_adView.destroy();
        //}
        saveList(getString((R.string.key_save)), m_saveList);
        super.onDestroy();
        setResult(RESULT_OK);
    }

}
