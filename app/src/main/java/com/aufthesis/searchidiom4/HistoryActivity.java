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

    private Context m_context;

    private MyBaseAdapter m_myBaseAdapter;
    private List<Idiom> m_historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        m_context = this;
        m_historyItems = new ArrayList<>();

        m_historyCount = findViewById(R.id.history_count);
        m_historyCount.setVisibility(View.INVISIBLE);
        m_historyListView = findViewById(R.id.list_history);
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

        this.createHistoryList();
    }

    private void createHistoryList()
    {
        m_historyItems.clear();
        m_historyCount.setVisibility(View.INVISIBLE);
        for(int i = 0; i < MainActivity.m_saveList.size(); i++)
        {

        }
    }
}
