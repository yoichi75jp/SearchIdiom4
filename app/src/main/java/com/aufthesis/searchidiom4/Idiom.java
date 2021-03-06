package com.aufthesis.searchidiom4;

/*
 * Created by a2035210 on 2017/10/31.
 */

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
        if(split.length == 4 || split.length == 5)
        {
            m_idiomName = split[0];
            m_idiomRead = split[1];
            m_isFavorite = split[2].equals("true");
            m_checkCount = Integer.parseInt(split[3]);
            if(split.length == 5)
                m_lastCheckDay = split[4];
        }
        this.createID();
    }
    /**
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
    /**/
    boolean equals(Idiom idiom)
    {
        return this.m_idiomName.equals(idiom.getName()) && this.m_idiomRead.equals(idiom.getRead());
    }

    public String getName(){return m_idiomName;}

    String getRead(){return m_idiomRead;}

    void setFavorite(boolean isFavorite)
    {
        m_isFavorite = isFavorite;
        this.update();
    }

    boolean isFavorite(){return m_isFavorite;}

    int getCheckCount(){return m_checkCount;}

    String getLastCheckedDay(){return m_lastCheckDay;}

    void Checked(String today)
    {
        m_checkCount++;
        m_lastCheckDay = today;
        setEntry(true);
        this.update();

    }

    void setAboutToDelete(boolean isDelete){m_aboutToDelete = isDelete;}
    boolean isTryingToDelete(){return m_aboutToDelete;}

    private void createID()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(m_idiomName);
        stringBuilder.append(MainActivity.DELIMITER);
        stringBuilder.append(m_idiomRead);
        stringBuilder.append(MainActivity.DELIMITER);
        stringBuilder.append(m_isFavorite);
        stringBuilder.append(MainActivity.DELIMITER);
        stringBuilder.append(m_checkCount);
        if(m_lastCheckDay != null)
        {
            stringBuilder.append(MainActivity.DELIMITER);
            stringBuilder.append(m_lastCheckDay);
        }
        m_ID = stringBuilder.toString();
    }

    private boolean isMyID(String id)
    {
        String[] split  = id.split(MainActivity.DELIMITER);
        return split.length >= 2 && split[0].equals(m_idiomName) && split[1].equals(m_idiomRead);
    }

    //public String getID(){return m_ID;}

    void setEntry(boolean enter)
    {
        m_isEntry = enter;
        if(!enter)
        {
            m_checkCount = 0;
            m_lastCheckDay = null;
            this.update();
        }
    }
    boolean isEntry(){return m_isEntry;}

    private void update()
    {
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
}
