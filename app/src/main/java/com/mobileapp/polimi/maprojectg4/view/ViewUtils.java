package com.mobileapp.polimi.maprojectg4.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;

import static android.R.attr.id;


public class ViewUtils {
    /**
     * Draws a circle of a specified color
     * @param context
     * @param color
     * @return a ShapeDrawable with the circle
     */
    public static ShapeDrawable drawCircle (Context context, int color) {
        ShapeDrawable oval = new ShapeDrawable (new OvalShape());
        oval.getPaint().setColor (color);
        return oval;
    }

    /**
     * Creates LayerDrawable with a background color and a circle inside
     * @param context
     * @param backgroundColor
     * @param circleColor
     * @return the created LayerDrawable
     */
    public static LayerDrawable possibleDirectionCellDraw(Context context, int backgroundColor, int circleColor) {
        //background
        ShapeDrawable background = new ShapeDrawable();
        background.getPaint().setColor(backgroundColor);
        //circle
        ShapeDrawable circle = ViewUtils.drawCircle(context, circleColor);

        Drawable[] layers = {background, circle};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        int border= dipToPixels(context,16);
        layerDrawable.setLayerInset(1,border,border,border,border); //make the circle smaller
        return layerDrawable;
    }

    /**
     * Converts the input from dip to Pixels for the current screen
     * @param context
     * @param dipValue
     * @return the input value in Pixel
     */
    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
        return px;
    }

    /**
     * Retrieve the height of the status bar
     * @param context
     * @return the height of the status bar
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Calculates the correct size of the square board considering the orientation of the screen and
     * the presence of the status bar
     * @param context
     * @return the size of the square board
     */
    public static int getSizeOfBoard(Context context) {
        int size;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            size = width - ViewUtils.dipToPixels(context, 16 * 2);
        else
            size = height - ViewUtils.dipToPixels(context, 16 * 2);
        return size;
    }

    public static void initializeTips(Context context, LinearLayout.LayoutParams lp, LinearLayout ll, int tips) {
        final TextView text = new TextView(context);
        text.setText(tips);
        text.setLayoutParams(lp);
        text.setGravity(Gravity.CENTER);
        text.setPadding(ViewUtils.dipToPixels(context, 8), 0, ViewUtils.dipToPixels(context, 8), 0);
        text.setTextColor(ContextCompat.getColor(context, R.color.primaryTextDark));
        text.setId(R.id.tips);
        ll.addView(text);
    }

    public static String moveDirectionAsString(Piece piece , Context context){
        String outString;
        if(piece.getMoveDirection() == Piece.Direction.ANY)
            outString = " " + context.getString(R.string.horizontal) +", " + context.getString(R.string.vertical) + ", " + context.getString(R.string.diagonal);
        else if(piece.getMoveDirection() == Piece.Direction.STRAIGHT)
            outString = " " + context.getString(R.string.horizontal) +", " + context.getString(R.string.vertical);
        else if(piece.getMoveDirection() == Piece.Direction.DIAGONAL)
            outString=" " + context.getString(R.string.diagonal);
        else outString ="error";
        return outString;
    }

    public static String attackDirectionAsString(Piece piece , Context context){
        String outString;
        if(piece.getAttackDirection() == Piece.Direction.ANY)
            outString = " " + context.getString(R.string.horizontal) +", " + context.getString(R.string.vertical) + ", " + context.getString(R.string.diagonal);
        else if(piece.getAttackDirection() == Piece.Direction.STRAIGHT)
            outString = " " + context.getString(R.string.horizontal) +", " + context.getString(R.string.vertical);
        else if(piece.getAttackDirection() == Piece.Direction.DIAGONAL)
            outString=" " + context.getString(R.string.diagonal);
        else outString ="error";
        return outString;
    }

    public static String moveTypeAsString(Piece piece, Context context){
        String type = "";

        if(piece.getMoveType() == Piece.Movement.WALK)
            type = " " + context.getString(R.string.walk);
        else if(piece.getMoveType() == Piece.Movement.FLY)
            type = " " + context.getString(R.string.fly);

        return type;
    }

    public static String getUnusedSpellAsString(Piece piece, Match match, Context context) {
        Match.Spell[] unusedSpell;
        if (piece.getMyTeam()== Piece.Team.WHITE)
            unusedSpell = match.getUnusedSpellsWhite();
        else unusedSpell = match.getUnusedSpellsBlack();

        String stringSpells = " ";
        for(int i = 0; i<unusedSpell.length; i++)
            if(unusedSpell[i] != Match.Spell.NOSPELL)
                switch (unusedSpell[i]) {
                    case FREEZE:
                        stringSpells = stringSpells  + context.getString(R.string.freeze_pop) + ",";
                        break;
                    case REVIVE:
                        stringSpells = stringSpells + " " + context.getString(R.string.revive_pop) + ",";
                        break;
                    case TELEPORT:
                        stringSpells = stringSpells + " " + context.getString(R.string.teleport_pop) + ",";
                        break;
                    case HEAL:
                        stringSpells = stringSpells + " " + context.getString(R.string.heal_pop) + ",";
                }
        //to take off the last comma
        stringSpells = stringSpells.substring(0, stringSpells.length()-1);
        return stringSpells.toString();
    }
}
