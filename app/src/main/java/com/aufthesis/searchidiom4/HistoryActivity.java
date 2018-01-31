package com.aufthesis.searchidiom4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by a2035210 on 2017/10/27.
 */

public class HistoryActivity extends Activity {

    private ListView m_historyListView;
    private TextView m_historyCount;
    static private Button m_deleteHistoryBtn;
    private CheckBox m_deleteAllChk;
    private AdView m_adView;

    private Context m_context;
    static private List<Idiom> m_historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        m_context = this;
        m_historyItems = new ArrayList<>();

        m_historyCount = findViewById(R.id.history_count);
        m_historyListView = findViewById(R.id.list_history);
        m_deleteAllChk = findViewById(R.id.check_delete_all);
        m_historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Idiom idiom = m_historyItems.get(position);

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
        m_historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Idiom idiom = m_historyItems.get(position);

                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("idiom",idiom.getName());
                clipboard.setPrimaryClip(clip);

                Toast toast = Toast.makeText(m_context, getString(R.string.copied, idiom.getName()),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
        m_deleteHistoryBtn = findViewById(R.id.delete_history);
        m_deleteHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
                builder.setTitle(getString(R.string.title_delete_history));
                builder.setMessage(getString(R.string.confirm_whether_to_delete_history));
                // OKの時の処理
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < MainActivity.m_savedIdiomList.size(); i++)
                        {
                            Idiom data = MainActivity.m_savedIdiomList.get(i);
                            if(data.isTryingToDelete())
                            {
                                data.setEntry(false);
                                MainActivity.m_savedIdiomList.remove(i);
                                i--;
                            }
                        }
                        createHistoryList();
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
        m_deleteAllChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyBaseAdapter myBaseAdapter = (MyBaseAdapter)m_historyListView.getAdapter();
                for(int i = 0; i < myBaseAdapter.getCount(); i++)
                {
                    Idiom idiom = (Idiom)myBaseAdapter.getItem(i);
                    idiom.setAboutToDelete(isChecked);
                }
                createHistoryList();
            }
        });
        this.createHistoryList();

        //バナー広告
        m_adView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(!MainActivity.g_isDebug)
            m_adView.loadAd(adRequest);
    }

    private void createHistoryList()
    {
        m_historyItems.clear();
        m_historyCount.setVisibility(View.INVISIBLE);
        m_deleteHistoryBtn.setEnabled(false);
        m_deleteAllChk.setEnabled(false);
        for(int i = 0; i < MainActivity.m_savedIdiomList.size(); i++)
        {
            Idiom data = MainActivity.m_savedIdiomList.get(i);
            if(data.getCheckCount() > 0)
                m_historyItems.add(data);          // 取得した要素をitemsに追加
        }
        MyBaseAdapter myBaseAdapter = new MyBaseAdapter(this, m_historyItems, MyBaseAdapter.eActivity.History);
        m_historyListView.setAdapter(myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        myBaseAdapter.notifyDataSetChanged();   // Viewの更新

        m_historyCount.setText(getString(R.string.search_count,m_historyItems.size()));
        if(m_historyItems.size() > 0)
        {
            m_historyCount.setVisibility(View.VISIBLE);
            m_deleteAllChk.setEnabled(true);
            for(int i = 0; i < m_historyItems.size(); i++)
            {
                if(m_historyItems.get(i).isTryingToDelete())
                {
                    m_deleteHistoryBtn.setEnabled(true);
                    break;
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (m_adView != null) {
            m_adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (m_adView != null) {
            m_adView.pause();
        }
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
        if (m_adView != null) {
            m_adView.destroy();
        }
        MainActivity.saveList(getString((R.string.key_save)), MainActivity.m_saveList);
        MainActivity.syncData();
        super.onDestroy();
        setResult(RESULT_OK);
    }

    static public void setEnableDeleteButton()
    {
        if(m_historyItems.size() > 0)
        {
            boolean isEnable = false;
            for(int i = 0; i < m_historyItems.size(); i++)
            {
                if(m_historyItems.get(i).isTryingToDelete())
                {
                    isEnable = true;
                    break;
                }
            }
            m_deleteHistoryBtn.setEnabled(isEnable);
        }
    }

}
