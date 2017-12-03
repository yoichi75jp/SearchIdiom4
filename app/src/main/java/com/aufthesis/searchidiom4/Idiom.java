package com.aufthesis.searchidiom4;

/*
 * Created by a2035210 on 2017/10/31.
 */

import java.util.List;

public class Idiom
{
    private String m_ID;
    private String m_idiomName;
    private String m_idiomRead;
    private boolean m_isFavorite = false;
    private boolean m_aboutToDelete = false;
    private int m_checkCount = 0;
    private String m_lastCheckDay;
    private boolean m_isEntry = false;

    Idiom(String name, String read, boolean isFavorite, int count, String checkedDay)
    {
        m_ID = "";
        m_idiomName = name;
        m_idiomRead = read;
        m_isFavorite = isFavorite;
        m_checkCount = count;
        m_lastCheckDay = checkedDay;
        this.createID();
    }

    Idiom(String id)
    {
        m_ID = "";
        String[] split  = id.split(MainActivity.DELIMITER);
        if(split.length == 5)
        {
            m_idiomName = split[0];
            m_idiomRead = split[1];
            m_isFavorite = split[2].equals("true");
            m_checkCount = Integer.parseInt(split[3]);
            m_lastCheckDay = split[4];
        }
        this.createID();
    }

    //Copy Constructor
    Idiom(Idiom idiom)
    {
        m_ID = "";
        m_idiomName = idiom.getName();
        m_idiomRead = idiom.getRead();
        m_isFavorite = idiom.isFavorite();
        m_checkCount = idiom.getCheckCount();
        m_lastCheckDay = idiom.getLastCheckedDay();
        this.createID();
    }

    boolean equals(Idiom idiom)
    {
        return this.m_idiomName.equals(idiom.getName()) && this.m_idiomRead.equals(idiom.getRead());
    }

    public String getName(){return m_idiomName;}

    String getRead(){return m_idiomRead;}

    void setFavorite(boolean isFavorite){m_isFavorite = isFavorite;}
    boolean isFavorite(){return m_isFavorite;}

    int getCheckCount(){return m_checkCount;}

    String getLastCheckedDay(){return m_lastCheckDay;}

    void Checked(String today)
    {
        m_checkCount++;
        m_lastCheckDay = today;
        this.createID();

        for(int i = 0; i < MainActivity.m_saveList.size(); i++)
        {
            if(this.isMyID(MainActivity.m_saveList.get(i)))
            {
                MainActivity.m_saveList.remove(i);
                break;
            }
        }
        MainActivity.m_saveList.add(m_ID);

    }

    public void setAboutToDelete(boolean isDelete){m_aboutToDelete = isDelete;}
    public boolean isTryingToDelete(){return m_aboutToDelete;}

    private void createID()
    {
        StringBuilder stringuBuilder = new StringBuilder();
        stringuBuilder.append(m_idiomName);
        stringuBuilder.append(MainActivity.DELIMITER);
        stringuBuilder.append(m_idiomRead);
        stringuBuilder.append(MainActivity.DELIMITER);
        stringuBuilder.append(m_isFavorite);
        stringuBuilder.append(MainActivity.DELIMITER);
        stringuBuilder.append(m_checkCount);
        stringuBuilder.append(MainActivity.DELIMITER);
        stringuBuilder.append(m_lastCheckDay);
        m_ID = stringuBuilder.toString();
    }

    private boolean isMyID(String id)
    {
        String[] split  = id.split(MainActivity.DELIMITER);
        return split.length >= 2 && split[0].equals(m_idiomName) && split[1].equals(m_idiomRead);
    }

    public String getID(){return m_ID;}

    public void setEntry(boolean entry){m_isEntry = entry;}
    public boolean isEntry(){return m_isEntry;}
}
