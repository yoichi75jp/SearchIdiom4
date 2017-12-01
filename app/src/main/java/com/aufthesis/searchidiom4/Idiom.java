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

    public boolean isMyID(String id)
    {
        String[] split  = m_ID.split(MainActivity.DELIMITER);
        return split.length >= 2 && split[0].equals(m_idiomName) && split[1].equals(m_idiomRead);
    }

    public void setEntry(boolean entry){m_isEntry = entry;}
    public boolean isEntry(){return m_isEntry;}
}
