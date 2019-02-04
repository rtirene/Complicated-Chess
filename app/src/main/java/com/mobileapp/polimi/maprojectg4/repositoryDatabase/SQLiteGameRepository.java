package com.mobileapp.polimi.maprojectg4.repositoryDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Vector;

/**
 * Created by Alessandro on 30/12/2016.
 */

public class SQLiteGameRepository extends SQLiteOpenHelper {

    private static final int 	DATABASE_VERSION = 27;
    private static final String DATABASE_NAME = "database.db";
    private static final String TABLE_NAME =  "saved_games";
    private static final String COL_1 = "NAME";
    private static final String COL_2 = "TURNS";
    private static final String COL_3 = "BOARD";
    private static final String COL_4 = "FROZEN_PIECES";
    private static final String COL_5 = "UNUSED_SPELLS";
    private static final String COL_6 = "CURRENT_TEAM";
    private static final String COL_7 = "DEAD_PIECES_WHITE";
    private static final String COL_8 = "DEAD_PIECES_BLACK";
    private static final String COL_9 = "DATE";
    private static final String COL_10 = "TIME";
    private static final String COL_11 = "WINNER";
    private static final String COL_12 = "CPU";
    private static final String COL_13 = "ACTION";

    private int turns = 0;

    public SQLiteGameRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getCol1() {
        return COL_1;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (NAME TEXT, TURNS INTEGER, BOARD BLOB, FROZEN_PIECES BLOB, UNUSED_SPELLS TEXT, CURRENT_TEAM TEXT," +
                " DEAD_PIECES_WHITE BLOB, DEAD_PIECES_BLACK BLOB, DATE TEXT, TIME TEXT, WINNER TEXT, CPU INTEGER, ACTION TEXT, PRIMARY KEY(NAME,TURNS))";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    /**
     * Modifies a match name. Used in GameActivity to update a customized name given by the user.
     * @param name the new name to store into the database
     * @param oldName the oldName. The function first finds the oldName into the database and then replace it with name
     * @return true if the operation is done with no errors otherwise return false
     */
    public boolean updateData(String name, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);

        long result = db.update(TABLE_NAME,contentValues,"NAME=?" ,new String[] { oldName });

        if(result == -1) return false;
        else             return true;
    }

    /**
     * Stores a new record into the database. Each record represents a turn for a specific match.
     * @param name the name of the match
     * @param board the board of the match
     * @param frozenPieces the frozen pieces of the match
     * @param unusedSpells the unused spells of the match
     * @param currentTeam the current team of the recorded turn
     * @param deadPiecesWhite the white dead pieces of the match
     * @param deadPiecesBlack the black dead pieces of the match
     * @param date the exact date when the turn has been played
     * @param time the exact time when the turn has been played
     * @param winner the current winner of the match
     * @param cpu its value is 1 to indicate that there was a turn versus the computer instead it is 0 to indicate a player versus player turn
     * @param action the specific action of the recorded turn
     * @return true if the operation is done with no errors otherwise return false
     */
    public boolean insertData(String name, byte[] board, byte[] frozenPieces, String unusedSpells, String currentTeam, byte[] deadPiecesWhite,
                              byte[] deadPiecesBlack, String date, String time, String winner, int cpu, String action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2,turns);
        contentValues.put(COL_3,board);
        contentValues.put(COL_4,frozenPieces);
        contentValues.put(COL_5,unusedSpells);
        contentValues.put(COL_6,currentTeam);
        contentValues.put(COL_7,deadPiecesWhite);
        contentValues.put(COL_8,deadPiecesBlack);
        contentValues.put(COL_9,date);
        contentValues.put(COL_10,time);
        contentValues.put(COL_11,winner);
        contentValues.put(COL_12,cpu);
        contentValues.put(COL_13,action);

        long result = db.insert(TABLE_NAME,null,contentValues);
        this.turns++;

        if(result == -1) return false;
        else             return true;
    }

    /**
     * Retieves all the names of the stored matches that are marked with no winner. This method is used in the load game functionality when the user wants load a match.
     * @return a string vector containing all names of the matches marked with no winner
     */
    public Vector<String> findAllLoad() {
        Vector<String> listGames = new Vector<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT DISTINCT *, MAX(TURNS) FROM saved_games GROUP BY NAME ORDER BY DATE,TIME DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String element = cursor.getString(cursor.getColumnIndex("NAME"));
                String winner = cursor.getString(cursor.getColumnIndex("WINNER"));
                if(winner.equals("NOWINNER")) {
                    listGames.add(element);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listGames;
    }

    /**
     * Retrieves all the names of the stored matches. This method is used in the old games functionality when the user wants to see a match.
     * @return a string vector containing all the names the matches
     */
    public Vector<String> findAllReplay() {
        Vector<String> listGames = new Vector<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT DISTINCT NAME FROM " + TABLE_NAME + " WHERE WINNER = 'NOWINNER' ORDER BY DATE,TIME DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String element = cursor.getString(cursor.getColumnIndex("NAME"));
                    listGames.add(element);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listGames;
    }

    /**
     * Retrieves the winner of a specific match
     * @param matchName the name of the match we want to know the winner
     * @return the winner of the match passed in matchName
     */
    public String getWinner(String matchName){
        String winner = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT WINNER FROM " + TABLE_NAME + " WHERE NAME = '" + matchName + "'";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()) {
            do {
                winner = cursor.getString(cursor.getColumnIndex("WINNER"));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return winner;
    }

    /**
     * Retrieves all the information of a match's turn.
     * @param selectedMatch the match name we want to know the information
     * @param lastTurn has to be passed as true if we want to extract information about the last turn otherwise it has to be false
     * @param turn the turn we want to know the information
     * @return a Cursor containing the whole record detected by the primary key (selectedMatch,turn)
     */
    public Cursor retrieveMatch(String selectedMatch,boolean lastTurn, int turn){
        SQLiteDatabase db = getWritableDatabase();
        String query = null;
        if(lastTurn) {
            query = "SELECT DISTINCT *, MAX(TURNS) FROM saved_games WHERE NAME = '" + selectedMatch + "' GROUP BY NAME";
        }
        else{
            query = "SELECT DISTINCT * FROM saved_games WHERE NAME = '" + selectedMatch + "' AND TURNS = " + turn + " GROUP BY NAME";
        }
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    /**
     * Retrieves the value of the last turn of a match.
     * @param selectedMatch the match we want to know the last turn
     * @return the last turn of selectedMatch
     */
    public int getMaxTurn(String selectedMatch){
        int maxTurn = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT  MAX(TURNS) FROM saved_games WHERE NAME = '" + selectedMatch + "' GROUP BY NAME";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
               maxTurn = cursor.getInt(cursor.getColumnIndex("MAX(TURNS)"));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return maxTurn;
    }

    /**
     * Deletes a match stored in the database. This method is activated when the user clicks on the delete button
     * @param selectedMatch the name of the match we want to delete from the database
     * @return the number of rows affected
     */
    public int delete(String selectedMatch) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME,"NAME = ?",new String[] {selectedMatch});
    }

    /**
     * Checks if a match into the database is against computer or against another human player
     * @param selectedMatch the name of the match we want to check
     * @return true if the selectedMatch is against computer, false if it is against human player
     */
    public boolean isCpuGame(String selectedMatch){
        boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT DISTINCT CPU FROM " + TABLE_NAME + " WHERE NAME = '" + selectedMatch +"'";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                if(cursor.getInt(cursor.getColumnIndex("CPU")) == 1){
                    result = true;
                    return result;
                }else if(cursor.getInt(cursor.getColumnIndex("CPU")) == 0) {
                    result =  false;
                    return  result;
                    }
                }while(cursor.moveToNext());
        }
        return result;
    }

    /**
     * Retrieves a description of a turn's action.
     * @param selectedMatch the name of the match we want to know the action
     * @param turn the turn we want to know the action
     * @return a String containing the description of the action
     */
    public String getAction(String selectedMatch,int turn){
        String action = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT ACTION FROM " + TABLE_NAME + " WHERE NAME = '" + selectedMatch + "' AND TURNS = " + turn;
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                action = cursor.getString(cursor.getColumnIndex("ACTION"));
            }while(cursor.moveToNext());
        }
        return action;
    }

}
