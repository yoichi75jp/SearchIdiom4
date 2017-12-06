package com.aufthesis.searchidiom4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by a2035210 on 2017/10/27.
 */

public class HistoryActivity extends Activity {

    private ListView m_historyListView;
    private TextView m_historyCount;
    private Button m_deleteHistoryBtn;
    private CheckBox m_deleteAllChk;

    private Context m_context;
    private List<Idiom> m_historyItems;

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
                                data.setEntry(false);
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
                    //if(isChecked) m_deleteAllChk.setText(getString(R.string.uncheck_all));
                    //else m_deleteAllChk.setText(getString(R.string.check_all));
                }
            }
        });
        this.createHistoryList();
    }

    private void createHistoryList()
    {
        MyBaseAdapter myBaseAdapter = (MyBaseAdapter)m_historyListView.getAdapter();
        m_historyItems.clear();
        if(myBaseAdapter != null) myBaseAdapter.refreshItemList(m_historyItems);
        m_historyCount.setVisibility(View.INVISIBLE);
        m_deleteHistoryBtn.setEnabled(false);
        m_deleteAllChk.setEnabled(false);
        for(int i = 0; i < MainActivity.m_savedIdiomList.size(); i++)
        {
            Idiom data = MainActivity.m_savedIdiomList.get(i);
            if(data.getCheckCount() > 0)
                m_historyItems.add(data);          // 取得した要素をitemsに追加
        }
        myBaseAdapter = new MyBaseAdapter(this, m_historyItems, MyBaseAdapter.eActivity.History);
        m_historyListView.setAdapter(myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        myBaseAdapter.notifyDataSetChanged();   // Viewの更新

        m_historyCount.setText(getString(R.string.search_count,m_historyItems.size()));
        if(m_historyItems.size() > 0)
        {
            m_historyCount.setVisibility(View.VISIBLE);
            m_deleteHistoryBtn.setEnabled(true);
            m_deleteAllChk.setEnabled(true);
        }
    }
}
