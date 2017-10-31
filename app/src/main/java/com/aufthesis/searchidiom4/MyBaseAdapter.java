package com.aufthesis.searchidiom4;

/*
 * Created by a2035210 on 2017/10/31.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MyBaseAdapter extends BaseAdapter {

    private Context m_context;
    private List<Idiom> m_items;
    private eActivity m_activity;

    // 毎回findViewByIdをする事なく、高速化が出来るようするholderクラス
    private class ViewHolder {
        TextView m_txtIdiomName;
        TextView m_txtIdiomRead;
        TextView m_txtCheckCount;
        TextView m_txtCheckedDay;
        CheckBox m_chkDelete;
        CheckBox m_chkFavorite;
    }

    public enum eActivity
    {
        Main,
        History,
        Favorite
    }

    // コンストラクタの生成
    MyBaseAdapter(Context context, List<Idiom> items, eActivity activity) {
        this.m_context = context;
        this.m_items = items;
        this.m_activity = activity;
    }

    // Listの要素数を返す
    @Override
    public int getCount() {
        return m_items.size();
    }

    // indexやオブジェクトを返す
    @Override
    public Object getItem(int position) {
        return m_items.get(position);
    }

    // IDを他のindexに返す
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 新しいデータが表示されるタイミングで呼び出される
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;

        // データを取得
        final Idiom idiomData = m_items.get(position);

        int activityId = 0;
        switch (m_activity)
        {
            case Main:
                activityId = R.layout.activity_main;
                break;
            case History:
                activityId = R.layout.activity_history;
                break;
            case Favorite:
                activityId = R.layout.activity_favorite;
                break;
        }

        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater == null) return null;

            view = inflater.inflate(activityId, parent, false);

            TextView txtIdiomName = view.findViewById(R.id.idiom_name);
            TextView txtIdiomRead = view.findViewById(R.id.idiom_read);
            TextView txtCheckCount = view.findViewById(R.id.view_count);
            TextView txtCheckedDay = view.findViewById(R.id.last_view_day);
            CheckBox chkFavorite  = view.findViewById(R.id.check_favorite);
            chkFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String isFavorite = isChecked ? "1" : "0";
                    idiomData.setFavorite(isChecked);

                    boolean isJP = Locale.getDefault().toString().equals(Locale.JAPAN.toString());
                    String lang = "\"read-en\"";
                    if(isJP) lang = "\"read-ja\"";
                    String sql = "update CharacterIdiom4 set is_favorite = "
                            + isFavorite
                            + " where idiom = "
                            + idiomData.getName()
                            + " and "
                            + lang
                            + " = "
                            + idiomData.getRead();
                    MainActivity.m_db.execSQL(sql);
                }
            });
            CheckBox chkDelete = null;
            if(m_activity == eActivity.History)
                chkDelete = view.findViewById(R.id.check_delete);

            // holderにviewを持たせておく
            holder = new ViewHolder();
            holder.m_txtIdiomName    = txtIdiomName;
            holder.m_txtIdiomRead    = txtIdiomRead;
            holder.m_txtCheckCount   = txtCheckCount;
            holder.m_txtCheckedDay   = txtCheckedDay;
            holder.m_chkDelete       = chkDelete;
            holder.m_chkFavorite     = chkFavorite;
            view.setTag(holder);

        } else {
            // 初めて表示されるときにつけておいたtagを元にviewを取得する
            holder = (ViewHolder) view.getTag();
        }

        // 取得した各データを各TextViewにセット
        holder.m_txtIdiomName.setText(idiomData.getName());
        holder.m_txtIdiomRead.setText(idiomData.getRead());
        int checkedCount = idiomData.getCheckCount();
        if(checkedCount == 0)
        {
            holder.m_txtCheckCount.setText("");
            holder.m_txtCheckedDay.setText("");
        }
        else
        {
            holder.m_txtCheckCount.setText(m_context.getString(R.string.check_count, checkedCount));
            String lastCheckedDay = idiomData.getLastCheckedDay();
            holder.m_txtCheckedDay.setText(m_context.getString(R.string.last_check_day, lastCheckedDay));
        }
        holder.m_chkFavorite.setChecked(idiomData.isFavorite());
        if(m_activity == eActivity.History)
            holder.m_chkDelete.setChecked(false);

        return view;

    }
}
